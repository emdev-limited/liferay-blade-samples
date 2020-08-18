package ru.emdev.samples.googlesheets.web.servlet;

import com.google.api.client.auth.oauth2.TokenResponseException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import ru.emdev.samples.googlesheets.web.configuration.GoogleSheetsPortletConfiguration;
import ru.emdev.samples.googlesheets.web.oauth.OAuth2Manager;
import ru.emdev.samples.googlesheets.web.oauth.OAuth2StateUtil;
import ru.emdev.samples.googlesheets.web.oauth.model.OAuth2State;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

@Component(
        property = {
                "osgi.http.whiteboard.servlet.name=ru.emdev.samples.googlesheets.web.servlet.GoogleSheetsOAuth2Servlet",
                "osgi.http.whiteboard.servlet.pattern=/sheets/oauth2",
                "servlet.init.httpMethods=GET,POST"
        },
        service = Servlet.class
)
public class GoogleSheetsOAuth2Servlet extends HttpServlet {

    private static final long serialVersionUID = -6485507854239487956L;

    private static final Log LOG = LogFactoryUtil.getLog(GoogleSheetsOAuth2Servlet.class);

    @Reference
    private Portal portal;

    @Reference
    private OAuth2Manager oAuth2Manager;

    @Reference
    private ConfigurationProvider configurationProvider;

    @Reference
    private LayoutLocalService layoutLS;

    @Override
    protected void doGet(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse)
            throws ServletException, IOException {
        LOG.info("Receive response from Google OAuth2 API");

        Optional<OAuth2State> oAuth2StateOptional =
                OAuth2StateUtil.getOAuth2StateOptional(portal.getOriginalServletRequest(httpServletRequest));

        OAuth2State oAuth2State = oAuth2StateOptional.orElseThrow(
                () -> new IllegalStateException("Authorization oAuth2State not initialized"));

        if (!OAuth2StateUtil.isValid(oAuth2State, httpServletRequest)) {
            OAuth2StateUtil.cleanUp(httpServletRequest);

            httpServletResponse.sendRedirect(oAuth2State.getFailureURL());
        } else {
            requestAuthorizationToken(httpServletRequest, httpServletResponse, oAuth2State);
        }
    }

    @Override
    protected void doPost(
            HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse)
            throws IOException, ServletException {

        doGet(httpServletRequest, httpServletResponse);
    }

    private void requestAuthorizationToken(
            HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse, OAuth2State oAuth2State)
            throws IOException {
        String code = ParamUtil.getString(httpServletRequest, "code");
        LOG.debug("AuthorizationCode: " + code);

        if (Validator.isNull(code)) {
            OAuth2StateUtil.cleanUp(httpServletRequest);
            httpServletResponse.sendRedirect(oAuth2State.getFailureURL());
        } else {
            try {
                Layout layout = layoutLS.getLayout(oAuth2State.getLayoutId());
                GoogleSheetsPortletConfiguration configuration =
                        configurationProvider.getPortletInstanceConfiguration(GoogleSheetsPortletConfiguration.class,
                                layout, oAuth2State.getPortletId());

                oAuth2Manager.requestAuthorizationToken(
                        configuration, oAuth2State.getUserId(), code, OAuth2StateUtil.getRedirectURI(
                                portal.getPortalURL(httpServletRequest)));

                OAuth2StateUtil.cleanUp(httpServletRequest);

                LOG.info("==>> redirect to success URL: " + oAuth2State.getSuccessURL());
                httpServletResponse.sendRedirect(oAuth2State.getSuccessURL());
            } catch (TokenResponseException tokenResponseException) {
                OAuth2StateUtil.cleanUp(httpServletRequest);

                SessionErrors.add(httpServletRequest, "externalServiceFailed");

                LOG.info("==>> redirect to failure URL: " + oAuth2State.getFailureURL());
                httpServletResponse.sendRedirect(oAuth2State.getFailureURL());
            } catch (PortalException e) {
                throw new IOException(e);
            }
        }
    }

}

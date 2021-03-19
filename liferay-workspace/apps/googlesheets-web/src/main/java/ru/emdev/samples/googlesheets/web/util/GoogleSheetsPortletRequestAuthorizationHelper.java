package ru.emdev.samples.googlesheets.web.util;

import com.google.api.client.auth.oauth2.Credential;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.portlet.LiferayPortletURL;
import com.liferay.portal.kernel.portlet.PortletURLFactory;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HttpUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PwdGenerator;
import com.liferay.portal.kernel.util.WebKeys;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import ru.emdev.samples.googlesheets.web.configuration.GoogleSheetsPortletConfiguration;
import ru.emdev.samples.googlesheets.web.oauth.OAuth2Manager;
import ru.emdev.samples.googlesheets.web.oauth.OAuth2StateUtil;
import ru.emdev.samples.googlesheets.web.oauth.model.OAuth2State;

import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component(service = GoogleSheetsPortletRequestAuthorizationHelper.class)
public class GoogleSheetsPortletRequestAuthorizationHelper {

    private static final Log LOG = LogFactoryUtil.getLog(GoogleSheetsPortletRequestAuthorizationHelper.class);

    @Reference
    private HttpUtil httpUtil;

    @Reference
    private Portal portal;

    @Reference
    private ConfigurationProvider configurationProvider;

    @Reference
    private PortletURLFactory portletURLFactory;

    @Reference
    private OAuth2Manager oAuth2Manager;

    public String performAuthorizationFlow(PortletRequest portletRequest, PortletResponse portletResponse)
            throws IOException, PortalException {
        ThemeDisplay themeDisplay = (ThemeDisplay) portletRequest.getAttribute(WebKeys.THEME_DISPLAY);
        String state = PwdGenerator.getPassword(5);

        HttpServletRequest originalHttpServletRequest =
                portal.getOriginalServletRequest(portal.getHttpServletRequest(portletRequest));

        OAuth2StateUtil.save(
                originalHttpServletRequest,
                new OAuth2State(themeDisplay.getPlid(),
                        themeDisplay.getPortletDisplay().getId(),
                        themeDisplay.getUserId(),
                        getSuccessURL(portletRequest), getFailureURL(portletRequest), state));

        HttpServletResponse httpServletResponse = portal.getHttpServletResponse(portletResponse);

        GoogleSheetsPortletConfiguration configuration =
                configurationProvider.getPortletInstanceConfiguration(GoogleSheetsPortletConfiguration.class,
                        themeDisplay.getLayout(), themeDisplay.getPortletDisplay().getId());

        String authorizationURL = oAuth2Manager.getAuthorizationURL(
                configuration, state, OAuth2StateUtil.getRedirectURI(portal.getPortalURL(portletRequest)));

        if (!hasValidCredential(configuration, themeDisplay.getUserId())) {
            authorizationURL = httpUtil.setParameter(authorizationURL, "prompt", "select_account");
        }

        LOG.info("Go " + authorizationURL);
        httpServletResponse.sendRedirect(authorizationURL);

        return authorizationURL;
    }

    private String getFailureURL(PortletRequest portletRequest) throws PortalException {
        LiferayPortletURL liferayPortletURL = portletURLFactory.create(
                portletRequest, portal.getPortletId(portletRequest),
                portal.getControlPanelPlid(portletRequest),
                PortletRequest.RENDER_PHASE);

        return liferayPortletURL.toString();
    }

    private String getSuccessURL(PortletRequest portletRequest) {
        return portal.getCurrentURL(portal.getHttpServletRequest(portletRequest));
    }

    public boolean hasValidCredential(GoogleSheetsPortletConfiguration configuration, long userId)
            throws IOException, PortalException {
        Credential credential = oAuth2Manager.getCredential(configuration, userId);

        return  (credential != null && (credential.getExpiresInSeconds() > 0 || credential.refreshToken()));
    }

}

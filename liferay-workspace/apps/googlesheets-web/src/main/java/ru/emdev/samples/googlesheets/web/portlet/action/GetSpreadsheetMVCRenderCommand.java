package ru.emdev.samples.googlesheets.web.portlet.action;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderConstants;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import ru.emdev.samples.googlesheets.web.configuration.GoogleSheetsPortletConfiguration;
import ru.emdev.samples.googlesheets.web.constants.GoogleSheetsPortletKeys;
import ru.emdev.samples.googlesheets.web.service.GoogleSheetsService;
import ru.emdev.samples.googlesheets.web.oauth.OAuth2Manager;
import ru.emdev.samples.googlesheets.web.oauth.OAuth2StateUtil;
import ru.emdev.samples.googlesheets.web.oauth.model.OAuth2State;
import ru.emdev.samples.googlesheets.web.util.GoogleSheetsPortletRequestAuthorizationHelper;

import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.Optional;

@Component(
        immediate = true,
        property = {
                "javax.portlet.name=" + GoogleSheetsPortletKeys.GOOGLESHEETS,
                "mvc.command.name=/sheets/get_spreadsheet"
        },
        service = MVCRenderCommand.class
)
public class GetSpreadsheetMVCRenderCommand implements MVCRenderCommand {

    private static final Log LOG = LogFactoryUtil.getLog(GetSpreadsheetMVCRenderCommand.class);

    @Reference
    private GoogleSheetsPortletRequestAuthorizationHelper authorizationHelper;

    @Reference
    private OAuth2Manager oAuth2Manager;

    @Reference
    private ConfigurationProvider configurationProvider;

    @Reference
    private Portal portal;

    @Reference
    private GoogleSheetsService sheetsService;

    @Override
    public String render(RenderRequest renderRequest, RenderResponse renderResponse) throws PortletException {
        HttpServletRequest originalServletRequest =
                portal.getOriginalServletRequest(portal.getHttpServletRequest(renderRequest));

        try {
            Optional<OAuth2State> oAuth2StateOptional = OAuth2StateUtil.getOAuth2StateOptional(originalServletRequest);

            if (oAuth2StateOptional.isPresent()) {
                OAuth2State oAuth2State = oAuth2StateOptional.get();
                LOG.info("=>> Load OAuth2 state: " + oAuth2State);

                ThemeDisplay themeDisplay = (ThemeDisplay) renderRequest.getAttribute(WebKeys.THEME_DISPLAY);
                GoogleSheetsPortletConfiguration configuration =
                        configurationProvider.getPortletInstanceConfiguration(GoogleSheetsPortletConfiguration.class,
                                themeDisplay.getLayout(), themeDisplay.getPortletDisplay().getId());

                Credential credential = oAuth2Manager.getCredential(configuration, themeDisplay.getUserId());

                if (credential != null) {
                    List<List<Object>> spreadsheetTable = sheetsService.readTable(credential,
                            configuration.spreadsheetId(), configuration.spreadsheetRange());

                    renderRequest.setAttribute("spreadsheetTable", spreadsheetTable);

                    return "/view.jsp";
                }
            }
        } catch (GoogleJsonResponseException e) {
            LOG.error("Google API Error: ", e);
        } catch (IOException | PortalException | GeneralSecurityException e) {
            LOG.error(e);
        }

        return performAuthorization(renderRequest, renderResponse);
    }

    private String performAuthorization(RenderRequest renderRequest, RenderResponse renderResponse) {
        LOG.info("=>> performAuthorizationFlow ");

        try {
            authorizationHelper.performAuthorizationFlow(renderRequest, renderResponse);
        } catch (PortalException | IOException e) {
            LOG.error("AuthorizationFlowError: ", e);
        }

        return MVCRenderConstants.MVC_PATH_VALUE_SKIP_DISPATCH;
    }

}

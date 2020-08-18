package ru.emdev.samples.googlesheets.web.oauth;

import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import ru.emdev.samples.googlesheets.web.oauth.model.OAuth2State;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Optional;

public class OAuth2StateUtil {

    private static final String SESSION_ATTRIBUTE_NAME_GOOGLE_OAUTH_2_STATE = "googlesheets-oauth2-state";

    public static void cleanUp(HttpServletRequest httpServletRequest) {
        HttpSession httpSession = httpServletRequest.getSession();

        httpSession.removeAttribute(SESSION_ATTRIBUTE_NAME_GOOGLE_OAUTH_2_STATE);
    }

    public static Optional<OAuth2State> getOAuth2StateOptional(HttpServletRequest httpServletRequest) {
        HttpSession httpSession = httpServletRequest.getSession();
        Object attribute = httpSession.getAttribute(SESSION_ATTRIBUTE_NAME_GOOGLE_OAUTH_2_STATE);

        if (!(attribute instanceof OAuth2State)) {
            cleanUp(httpServletRequest);

            return Optional.empty();
        }

        return Optional.of((OAuth2State) attribute);
    }

    public static String getRedirectURI(String portalURL) {
        return portalURL + Portal.PATH_MODULE + "/sheets/oauth2";
    }

    public static boolean isValid(OAuth2State oAuth2State, HttpServletRequest httpServletRequest) {
        if (Validator.isNotNull(ParamUtil.getString(httpServletRequest, "error"))) {
            return false;
        }

        String state = ParamUtil.getString(httpServletRequest, "state");

        return oAuth2State.isValid(state);
    }

    public static void save(HttpServletRequest httpServletRequest, OAuth2State oAuth2State) {
        HttpSession httpSession = httpServletRequest.getSession();

        httpSession.setAttribute(SESSION_ATTRIBUTE_NAME_GOOGLE_OAUTH_2_STATE, oAuth2State);
    }

    private OAuth2StateUtil() {
        throw new UnsupportedOperationException("Suppresses default constructor, ensuring non-instantiability");
    }
}

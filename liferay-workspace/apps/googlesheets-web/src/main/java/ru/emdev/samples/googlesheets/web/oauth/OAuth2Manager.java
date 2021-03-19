package ru.emdev.samples.googlesheets.web.oauth;

import com.google.api.client.auth.oauth2.ClientParametersAuthentication;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.StoredCredential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeRequestUrl;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.DataStore;
import com.google.api.client.util.store.MemoryDataStoreFactory;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import ru.emdev.samples.googlesheets.web.configuration.GoogleSheetsPortletConfiguration;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component(
        immediate = true,
        service = OAuth2Manager.class
)
public class OAuth2Manager {

    private static final Log LOG = LogFactoryUtil.getLog(OAuth2Manager.class);

    private final Map<String, GoogleAuthorizationCodeFlow> authorizationCodeFlows = new ConcurrentHashMap<>();

    public String getAuthorizationURL(GoogleSheetsPortletConfiguration configuration, String state, String redirectUri)
            throws PortalException {
        GoogleAuthorizationCodeFlow googleAuthorizationCodeFlow = getGoogleAuthorizationCodeFlow(configuration);

        if (googleAuthorizationCodeFlow == null) {
            throw new PortalException("No Google authorization code flow found");
        }

        GoogleAuthorizationCodeRequestUrl requestUrl = googleAuthorizationCodeFlow.newAuthorizationUrl();

        return requestUrl.setState(state)
                .setRedirectUri(redirectUri)
                .setScopes(Collections.singleton(SheetsScopes.SPREADSHEETS))
                .build();
    }

    public Credential getCredential(GoogleSheetsPortletConfiguration configuration, long userId) throws PortalException {
        try {
            GoogleAuthorizationCodeFlow googleAuthorizationCodeFlow = getGoogleAuthorizationCodeFlow(configuration);

            if (googleAuthorizationCodeFlow == null) {
                return null;
            }

            return googleAuthorizationCodeFlow.loadCredential(String.valueOf(userId));
        } catch (IOException e) {
            throw new PortalException(e);
        }
    }

    public void revokeCredential(GoogleSheetsPortletConfiguration configuration, long userId) throws PortalException {
        try {
            GoogleAuthorizationCodeFlow googleAuthorizationCodeFlow = getGoogleAuthorizationCodeFlow(configuration);

            if (googleAuthorizationCodeFlow != null) {
                DataStore<StoredCredential> credentialDataStore = googleAuthorizationCodeFlow.getCredentialDataStore();

                credentialDataStore.delete(String.valueOf(userId));
            }
        } catch (IOException e) {
            throw new PortalException(e);
        }
    }

    public void setAccessToken(GoogleSheetsPortletConfiguration configuration, long userId, String accessToken)
            throws IOException, PortalException {
        GoogleAuthorizationCodeFlow googleAuthorizationCodeFlow = getGoogleAuthorizationCodeFlow(configuration);

        if (googleAuthorizationCodeFlow != null) {
            DataStore<StoredCredential> credentialDataStore = googleAuthorizationCodeFlow.getCredentialDataStore();
            StoredCredential storedCredential = new StoredCredential();

            storedCredential.setAccessToken(accessToken);

            credentialDataStore.set(String.valueOf(userId), storedCredential);
        }
    }

    public void requestAuthorizationToken(GoogleSheetsPortletConfiguration configuration, long userId, String code, String redirectUri)
            throws IOException, PortalException {
        GoogleAuthorizationCodeFlow googleAuthorizationCodeFlow = getGoogleAuthorizationCodeFlow(configuration);

        if (googleAuthorizationCodeFlow == null) {
            throw new PortalException("No Google Authorization Code Flow found");
        }

        GoogleTokenResponse googleTokenResponse = googleAuthorizationCodeFlow.newTokenRequest(code)
                        .setRedirectUri(redirectUri)
                        .execute();

        LOG.info("=>> User: "+ userId + " TokenResponse: " + googleTokenResponse.getAccessToken());

        googleAuthorizationCodeFlow.createAndStoreCredential(googleTokenResponse, String.valueOf(userId));
    }

    public boolean isConfigured(GoogleSheetsPortletConfiguration configuration) {
        return (Validator.isNotNull(configuration.clientId()) && Validator.isNotNull(configuration.clientSecret()));
    }

    private GoogleAuthorizationCodeFlow getGoogleAuthorizationCodeFlow(GoogleSheetsPortletConfiguration configuration)
            throws PortalException {
        try {
            if (!isConfigured(configuration)) {
                return null;
            }

            String configurationKey = configuration.clientId();

            GoogleAuthorizationCodeFlow googleAuthorizationCodeFlow = authorizationCodeFlows.get(configurationKey);

            if (googleAuthorizationCodeFlow != null) {
                ClientParametersAuthentication currentParameters =
                        (ClientParametersAuthentication) googleAuthorizationCodeFlow.getClientAuthentication();

                if (StringUtil.equals(currentParameters.getClientId(), configuration.clientId()) &&
                        StringUtil.equals(currentParameters.getClientSecret(), configuration.clientSecret())) {

                    return googleAuthorizationCodeFlow;
                }

                DataStore<StoredCredential> credentialDataStore = googleAuthorizationCodeFlow.getCredentialDataStore();

                credentialDataStore.clear();
            }

            googleAuthorizationCodeFlow = new GoogleAuthorizationCodeFlow.Builder(
                    GoogleNetHttpTransport.newTrustedTransport(),
                    JacksonFactory.getDefaultInstance(),
                    GetterUtil.getString(configuration.clientId()),
                    GetterUtil.getString(configuration.clientSecret()),
                    Collections.singleton(SheetsScopes.SPREADSHEETS))
                    .setDataStoreFactory(MemoryDataStoreFactory.getDefaultInstance())
                    .build();

            authorizationCodeFlows.put(configurationKey, googleAuthorizationCodeFlow);

            return googleAuthorizationCodeFlow;
        } catch (GeneralSecurityException | IOException e) {
            throw new PortalException(e);
        }
    }

    @Deactivate
    protected void deactivate() {
        authorizationCodeFlows.clear();
    }

}

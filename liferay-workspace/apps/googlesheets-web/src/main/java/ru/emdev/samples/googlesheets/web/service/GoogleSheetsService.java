package ru.emdev.samples.googlesheets.web.service;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.Validator;
import org.osgi.service.component.annotations.Component;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;

@Component(
        immediate = true,
        service = GoogleSheetsService.class
)
public class GoogleSheetsService {

    private static final Log LOG = LogFactoryUtil.getLog(GoogleSheetsService.class);

    private static final String DEFAULT_SPREADSHEET_RANGE = "A0:Z";

    public List<List<Object>> readTable(Credential credential, String spreadsheetId, String sheetRange)
            throws IOException, GeneralSecurityException {
        LOG.info("=>> Get Sheet ID " + spreadsheetId + ", Range: " + sheetRange);
        Sheets service = getSheetsService(credential);

        if (Validator.isNull(spreadsheetId)) {
            LOG.warn("Unable to load table because spreadsheetId is empty");

            return Collections.emptyList();
        }

        sheetRange = Validator.isNull(sheetRange) ? DEFAULT_SPREADSHEET_RANGE : sheetRange;

        return readTable(service, spreadsheetId, sheetRange);
    }

    private Sheets getSheetsService(Credential credential) throws IOException, GeneralSecurityException {
        return new Sheets.Builder(GoogleNetHttpTransport.newTrustedTransport(),
                JacksonFactory.getDefaultInstance(), credential)
                .setApplicationName("GoogleSheets Sample")
                .build();
    }

    private List<List<Object>> readTable(Sheets service, String spreadsheetId, String sheetName) throws IOException {
        ValueRange table = service.spreadsheets().values().get(spreadsheetId, sheetName)
                .execute();

        return table.getValues();
    }

}

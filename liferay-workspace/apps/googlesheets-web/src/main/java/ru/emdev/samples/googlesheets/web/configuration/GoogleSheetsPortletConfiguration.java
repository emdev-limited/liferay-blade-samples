package ru.emdev.samples.googlesheets.web.configuration;

import aQute.bnd.annotation.metatype.Meta;
import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

@ExtendedObjectClassDefinition(
        scope = ExtendedObjectClassDefinition.Scope.PORTLET_INSTANCE
)
@Meta.OCD(
        id = "ru.emdev.samples.googlesheets.web.configuration.GoogleSheetsPortletConfiguration",
        localization = "content/Language",
        name = "googlesheets-portlet-configuration-name"
)
public interface GoogleSheetsPortletConfiguration {

    @Meta.AD(
            deflt = "",
            description = "google-api-client-id-description",
            name = "google-api-client-id", required = false
    )
    String clientId();

    @Meta.AD(
            deflt = "",
            description = "google-api-client-secret-description",
            name = "google-api-client-secret", required = false
    )
    String clientSecret();

    @Meta.AD(
            deflt = "",
            description = "google-spreadsheet-id-description",
            name = "google-spreadsheet-id", required = false
    )
    String spreadsheetId();

    @Meta.AD(
            deflt = "A1:E",
            description = "google-spreadsheet-range-description",
            name = "google-spreadsheet-range", required = false
    )
    String spreadsheetRange();

}

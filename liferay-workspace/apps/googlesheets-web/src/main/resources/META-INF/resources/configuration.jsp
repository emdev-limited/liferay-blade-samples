<%@ include file="/init.jsp" %>

<%
    String clientId = PrefsParamUtil
            .getString(PortletPreferencesFactoryUtil.getPortletSetup(renderRequest), renderRequest, "clientId");
    String clientSecret = PrefsParamUtil
            .getString(PortletPreferencesFactoryUtil.getPortletSetup(renderRequest), renderRequest, "clientSecret");
    String spreadsheetId = PrefsParamUtil
            .getString(PortletPreferencesFactoryUtil.getPortletSetup(renderRequest), renderRequest, "spreadsheetId");
    String spreadsheetRange = PrefsParamUtil
            .getString(PortletPreferencesFactoryUtil.getPortletSetup(renderRequest), renderRequest, "spreadsheetRange");

%>

<liferay-portlet:actionURL portletConfiguration="<%= true %>" var="configurationActionURL"/>
<liferay-portlet:renderURL portletConfiguration="<%= true %>" var="configurationRenderURL"/>

<div class="portlet-configuration-body-content">
    <aui:form action="<%= configurationActionURL %>" method="post" name="fm">
        <aui:input name="<%= Constants.CMD %>" type="hidden" value="<%= Constants.UPDATE %>"/>
        <aui:input name="redirect" type="hidden" value="<%= configurationRenderURL %>"/>

        <div class="container-fluid-1280">
            <aui:fieldset-group markupView="lexicon">
                <aui:fieldset>
                    <div class="sheet sheet-lg">

                        <aui:input name="preferences--clientId--" label="google-api-client-id" type="text" value="<%= clientId %>"/>

                        <aui:input name="preferences--clientSecret--" label="google-api-client-secret" type="text" value="<%= clientSecret %>"/>

                        <aui:input name="preferences--spreadsheetId--"  label="google-spreadsheet-id" type="text" value="<%= spreadsheetId %>"/>

                        <aui:input name="preferences--spreadsheetRange--"  label="google-spreadsheet-range" type="text" value="<%= spreadsheetRange %>"/>

                    </div>
                </aui:fieldset>
            </aui:fieldset-group>
        </div>

        <aui:button-row>
            <aui:button cssClass="btn-lg" type="submit"/>
        </aui:button-row>
    </aui:form>
</div>

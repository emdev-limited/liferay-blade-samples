<%@ include file="/init.jsp" %>

<%
    List<List<Object>> spreadsheetTable = (List<List<Object>>) request.getAttribute("spreadsheetTable");

%>

<portlet:renderURL var="viewSpreadsheetURL">
    <portlet:param name="mvcRenderCommandName" value="/sheets/get_spreadsheet" />
</portlet:renderURL>

<div class="sheet">
    <h4 class="sheet-title">Google Spreadsheet</h4>

    <div class="sheet-section">
        <c:choose>
            <c:when test="${spreadsheetTable != null && spreadsheetTable.size() > 0}">
                <div class="table-responsive">
                    <table class="table table-autofit">
                        <c:forEach items="${spreadsheetTable}" var="row">
                            <tr>
                                <c:forEach items="${row}" var="item">
                                    <td class="table-no-border">${item}</td>
                                </c:forEach>
                            </tr>
                        </c:forEach>
                    </table>
                </div>
            </c:when>
            <c:otherwise>
                <div style="margin:10px auto;">
                    <aui:button cssClass="btn btn-primary" href="${viewSpreadsheetURL}" value="google-spreadsheet-load" type="button"/>
                </div>
            </c:otherwise>
        </c:choose>
    </div>
</div>

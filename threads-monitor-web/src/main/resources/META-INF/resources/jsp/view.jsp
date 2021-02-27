<%@include file="/jsp/init.jsp"%>
<%@page contentType="text/html;charset=UTF-8" language="java" pageEncoding="utf-8"%>

<%
    String threadsMonitorDataJsonString = ActionUtil.threadsMonitorDataJsonString(themeDisplay.getUserId(), themeDisplay.getLocale());
    String selTabulatorGrouping = ActionUtil.getGroupingColumn(themeDisplay.getUserId());
%>

    <div id="<portlet:namespace/>javaThreadsMonitorPortletWrapper">
        <div>
            Java Threads Monitor Portlet <br> <br>
            namespace = <%= renderResponse.getNamespace() %> <br> <br>
            <div class="btn-wrapper">
                <aui:button type="button" name="loadThreadsData" value="load-threads-data" cssClass="load-threads-data-btn" />
            </div>
        </div>

        <%-- выпадающий список для задания группировки данных в табуляторе --%>
        <div class="tabulator-grouping-wrapper">
            <aui:select autocomplete="off" name="tabulatorGrouping" label="tabulator-grouping">
                <%
                    for (String group : ThreadsMonitorConst.getGroupColumns()) {
                        boolean selected = group.equalsIgnoreCase(selTabulatorGrouping);
                        %>
                            <aui:option value="<%= group %>" selected="<%= selected %>" label="<%= ThreadsMonitorConst.getGroupColumnName(group) %>" />
                        <%
                    }
                %>
            </aui:select>
        </div>

        <%-- табулятор --%>
        <liferay-util:include servletContext="<%= this.getServletContext() %>" page="/jsp/tabulator.jsp">
        </liferay-util:include>
    </div>

    <%@ include file="/jsp/js.jspf" %>

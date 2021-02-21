<%@include file="/jsp/init.jsp"%>
<%@page contentType="text/html;charset=UTF-8" language="java" pageEncoding="utf-8"%>

<%
    String threadsMonitorDataJsonString = ActionUtil.threadsMonitorDataJsonString(themeDisplay.getUserId(), themeDisplay.getScopeGroupId(), themeDisplay.getLocale());
%>

    <div id="<portlet:namespace/>javaThreadsMonitorPortletWrapper">
        <div>
            Java Threads Monitor Portlet <br> <br>
            namespace = <%= renderResponse.getNamespace() %> <br> <br>
            <div class="btn-wrapper">
                <aui:button type="button" name="loadThreadsData" value="load-threads-data" cssClass="load-threads-data-btn" />
            </div>
        </div>


        <%-- табулятор --%>
        <liferay-util:include servletContext="<%= this.getServletContext() %>" page="/jsp/tabulator.jsp">
        </liferay-util:include>
    </div>

    <%@ include file="/jsp/js.jspf" %>

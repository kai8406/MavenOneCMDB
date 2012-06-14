<%-- 
   -      Renders a URL navigating to the passed action
   --%>
<%@ attribute name="operation" required="true" %>
<%@ attribute name="text" required="true" %>
<%@ include file="/WEB-INF/jsp/include.jsp" %>
<a href="<c:url value="${pageUrl}">
		<c:param name="change:${site.action.name}:${operation}"></c:param></c:url>">${text}</a>
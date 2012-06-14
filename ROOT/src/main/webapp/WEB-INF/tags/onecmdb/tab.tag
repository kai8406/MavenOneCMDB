<%@ taglib prefix="form" tagdir="/WEB-INF/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ attribute name="action" required="true" %>
<%@ attribute name="text" required="true" %>
<c:choose><c:when test="${pageData.action == action}"><span class="selected">${text}</span></c:when>
<c:otherwise><span class="unselected"><form:action text="${text}" type="${action}" /></span></c:otherwise></c:choose>
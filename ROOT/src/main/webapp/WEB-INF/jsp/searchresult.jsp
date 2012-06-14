<%-- 
	${site.action} evaluates to a org.onecmdb.web.EditCiAction; look in javadoc
	for what to expect from the object.
--%>
<%@ include file="include.jsp" %>
<%@ taglib prefix="form" tagdir="/WEB-INF/tags/form" %>
<%@ taglib prefix="onecmdb" tagdir="/WEB-INF/tags/onecmdb" %>
<onecmdb:actionheading action="${site.action}" />

<c:forEach var="ci" items="${site.action.searchResult.items}" varStatus="ciStatus">
<c:if test="${ciStatus.first}"><ol></c:if>

<li><form:cilink ci="${ci}" /></li>

<c:if test="${ciStatus.last}"></ol></c:if>
</c:forEach>
<c:if test="${site.action.searchResult.size==0}">
<p><em>No result found.</em></p>
</c:if>








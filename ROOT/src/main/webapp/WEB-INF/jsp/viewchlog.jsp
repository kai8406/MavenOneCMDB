<%@ include file="include.jsp" %>
<%@ taglib prefix="form" tagdir="/WEB-INF/tags/form" %>
<%@ taglib prefix="onecmdb" tagdir="/WEB-INF/tags/onecmdb" %>

<onecmdb:actionheading action="${site.action}" />
<table border="0" summary="RFC List">
<thead>
<tr>
<th>ID</th>
<th>Time</th>
<th>Summary</th>
</tr>
</thead>
<c:forEach var="rfc" items="${site.action.rfcs}" varStatus="st">
	<tr <c:choose>
		<c:when test="${st.index % 2 == 0}">
			class="odd"
		</c:when>
		<c:otherwise>
			class="even"
		</c:otherwise>
	</c:choose>>
	<td>#<c:out value="${rfc.id}"/></td>
	<td><c:out value="${rfc.ts}"/></td>
	<td><c:out value="${rfc.summary}"/></td>
	</tr>
</c:forEach>
<tr>
<td colspan="3" align="right">

<form:navigate to="-1" text="Back" />

</table>
	




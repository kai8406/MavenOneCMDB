<%@ include file="include.jsp" %>
<%@ taglib prefix="form" tagdir="/WEB-INF/tags/form" %>
<%@ taglib prefix="onecmdb" tagdir="/WEB-INF/tags/onecmdb" %>

<div class="navcontainer">

<c:import url="search.jsp" /> 

<div class="heading">
<table summary="Navigational History header"><tr>
<td><h4><onecmdb:toggle name="History" />History</h4><td>
</tr>
</table>
</div>
<div class="navigation">
<c:if test="${site.globals['showHistory']}">
<table summary="Navigational History"><tbody><c:remove var="prev"/><c:forEach var="action" 
items="${site.history}" varStatus="st"><c:if test="${action.name == 'viewci'}"><c:if 
test="${prev != action}"><tr>
	    <td><form:cilink ci="${action.ci}" history="#${st.index}" />
	    </td>
	</tr><c:set var="prev" value="${action}" /></c:if>
</c:if>
</c:forEach><c:remove var="prev"/></tbody></table>
</c:if>
</div>

<div class="heading">
<table summary="Actions"><tr>
<td><h4><onecmdb:toggle name="Actions" />Actions</h4><td>
</tr>
</table>
</div>
<div class="navigation">
<c:if test="${site.globals['showActions']}">
<table summary="Global Actions">
	<tbody><c:forEach var="job" items="${site.jobs}">
		<tr>
			<td>
				<form:cilink ci="${job}" />
			</td>
		</tr>
	</c:forEach></tbody>
</table>
</c:if>
</div>

<c:if test="${ site.mode == 'design'}">
<c:import url="${site.mode}nav.jsp" /> 
</c:if>

</div>





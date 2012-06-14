<%@ tag
    display-name="Offsprings"
    description="Outputs offspring information" 
%>
<%@ include file="/WEB-INF/jsp/include.jsp" %>
<%@ taglib prefix="form" tagdir="/WEB-INF/tags/form" %>
<%@ taglib prefix="onecmdb" tagdir="/WEB-INF/tags/onecmdb" %>
<%@ taglib prefix="debug" tagdir="/WEB-INF/tags/debug" %>


<!-- {{{ referrers -->

<div class="section">
<h3><onecmdb:toggle name="References" />&nbsp;References</h3>
<spring:bind path="site.globals['showReferences']">
<c:if test="${status.value}">

<table border="0" summary="References to">
<thead>
	<tr>
	<th align="left">Source</th>
	<th align="center"></th>
	<th align="right">Target</th>
	</tr>
</thead>
<tbody>
<c:set var="row" value="0"/>
	<c:forEach var="ref" items="${site.action.inboundReferences}">
	<tr class="<onecmdb:stripe row="${row}" />">
		<c:set var="srcs" value="0"/>
		<c:forEach var="src" items="${ref.sourceCis}" varStatus="srcStatus">
		<c:choose>
			<c:when test="${not srcStatus.first}">
				<tr class="<onecmdb:stripe row="${row}" />">
				<td colspan="2">
			</c:when>
			<c:otherwise>
				<td>
			</c:otherwise>
		</c:choose>

			<form:cilink ci="${src}" />
		<c:if test="${srcStatus.first}">
			<td><onecmdb:ci ci="${ref}" />
			    <onecmdb:ciicon ci="${ref}" size="SMALL" />
		</c:if>
		</td>
		<td><form:cilink ci="${site.action.ci}"/></td>
		<c:if test="${not srcStatus.last}">
		</tr>
		</c:if>
		<c:set var="srcs" value="${srcs+1}"/>
		</c:forEach>
		<c:if test="${srcs==0}">
		
		<td><em>Source not set</em></td><td><onecmdb:ci ci="${ref.derivedFrom}" /></td>
		</c:if>
		
		<c:remove var="srcss"/>
	</tr>

<c:set var="row" value="${row + 1}"/>
</c:forEach>
<c:if test="${row==0}"><tr class="<onecmdb:stripe row="${row}" />">
<td>List is empty</em></td><td></td><td></td></tr></c:if>
<c:remove var="row"/>
</tbody>
</table>
</c:if>
</spring:bind>
</div>
<!-- }}} -->





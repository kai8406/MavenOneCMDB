<%@ include file="/WEB-INF/jsp/include.jsp" %>
<%@ taglib prefix="form" tagdir="/WEB-INF/tags/form" %>
<%@ taglib prefix="onecmdb" tagdir="/WEB-INF/tags/onecmdb" %>
<h3><a name="job${action.ci.id}">Job Handling</a></h3>

<table border="0">
<tbody><tr>
<td>State:</td>
<td><c:out value="${site.action.attributeMap.state.meta.value.asString}" /></td>
</tr></tbody>
</table>

<table border="0"><tbody>
<c:set var="percentage" value="${site.action.attributeMap.progressPercentage.meta.value.asString}" />
<tr>
<td>Progress:</td>
<td>
<c:out value="${percentage}"/>%
</td>
<td>
<!-- {{{ progress indicator -->
<c:if test="${percentage gt 0}"><img src="<c:url value="/images/progress.gif"/>"
height="20" border="1" width="${percentage}" ></c:if><c:if 
test="${percentage ne 100}"><img src="<c:url value="/images/transparent16.gif"/>"
height="20" border="1" width="${100-percentage}"></c:if>
<!--  }}} --->
</td>
<td><tt><c:out value="${site.action.attributeMap.progress.meta.value.asString}"/></tt></td>
</tr>
<c:remove var="percentage" />

</tbody></table>

<table><tbody>
<tr>
<td>Status:</td>
<td><c:out value="${site.action.attributeMap.status.meta.value.asString}"/></td>
</tr>
</tbody></table>
<p>
<form:change 
	text="Start"
	operation="jobctl(${action.ci.id},start)"
	hash="job${action.ci.id}"
	disabled="${site.action.attributeMap.state.meta.value.asString!='' && site.action.attributeMap.state.meta.value.asString!='IDLE'}"  
/>
<form:change 
	text="Stop/Abort" 
	operation="jobctl(${action.ci.id},stop)"
	hash="job${action.ci.id}"
	disabled="${site.action.attributeMap.state.meta.value.asString=='' || site.action.attributeMap.state.meta.value.asString=='IDLE'}" 
/>
<form:change 
	text="Refresh" 
	operation="jobctl(${action.ci.id},refresh)" 
	hash="job${action.ci.id}" 
/>
</p>



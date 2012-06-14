<%@ tag 
    display-name="Offsprings"
    description="Outputs offspring information" 
%>
<%@ include file="/WEB-INF/jsp/include.jsp" %>
<%@ attribute name="ci" required="true" type="org.onecmdb.core.ICi" %>
<%@ taglib prefix="form" tagdir="/WEB-INF/tags/form" %>
<%@ taglib prefix="onecmdb" tagdir="/WEB-INF/tags/onecmdb" %>
<%@ taglib prefix="debug" tagdir="/WEB-INF/tags/debug" %>

<table border="0" summary="Based on ${ci.displayName}">
<thead>
	<tr>
	<th colspan="2"><a name="offsprings">Item</a></th>
	</tr>
</thead>
<tbody>
	<c:set var="row" value="0"/>
	<c:forEach var="offspring" items="${site.action.ci.offsprings}" varStatus="st">
	<tr class="<onecmdb:stripe row="${row}" />">
	
		<td><c:if test="${true || site.action.name=='editci'}">
		<form:input property="CIMARK${offspring.id}" type="checkbox"/>|</c:if>
		
		<form:cilink ci="${offspring}" /></td>
		<td><c:if test="${true || site.action.name=='editci'}">
		<form:change 
		operation="deleteCi(${offspring.id})" 
			text="Delete" img="trashcan" alt="Delete Instance"
			prompt="Deletes \\'${offspring.displayName}\\' and all items based on it." 
			hash="offsprings" /><onecmdb:error
			property="deleteCi(${offspring.id})" />
		 </c:if></td>
	</tr>
	<c:set var="row" value="${row + 1}"/>
	</c:forEach>
	<c:if test="${row==0}"><tr class="<onecmdb:stripe row="${row}" />"><td><em>${ci.blueprint ? 'List is empty' : 'N/A'}</em></td></tr></c:if>
	<c:remove var="row"/>
	<c:if test="${true || (site.action.name=='editci' && site.mode=='design')}">
	<tr>
		<td>
		<form:change 
		operation="deleteMarked(${site.action.ci.id})" 
			text="Delete" img="trashcan" alt="Delete Marked"
			prompt="Deletes all marked items and all items based on them." 
			hash="offsprings" /><onecmdb:error
			property="deleteMarked(${offspring.id})" /></td>
	
		<td><c:if test="${site.action.ci.blueprint}">
		<form:actionurl navigate="addci"
		action="${site.actionMap['addci']}" params="${site.action.params}" 
		returnTo="${site.currentHistory}" returnHash="offsprings" 
		cssclass="linka">[New...]</form:actionurl>
		</c:if></td>
	</tr>
	</c:if>
</tbody>
</table>





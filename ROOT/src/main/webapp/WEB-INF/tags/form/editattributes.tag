<%-- 
   -      Renders a form submission field:
   -      text: Text to be used
   -      type: A real submission is indcated of a type `submit', other types
   -            reflect a form change request
   -       
   --%>
<%@ include file="/WEB-INF/jsp/include.jsp" %>
<%@ taglib prefix="onecmdb" tagdir="/WEB-INF/tags/onecmdb" %>
<%@ taglib prefix="form" tagdir="/WEB-INF/tags/form" %>
<%@ attribute name="ci" required="true" type="org.onecmdb.core.ICi" %>


<div class="topsection">
<form:change operation="rclCi()" text="RCL" noErrorFeedback="true" disabled="${!site.action.recallable}"  
title="Recalls values from the CI in memory to this CI"
/>
</div>
<div class="section">
<h3>Identification</h3>

<form:attrtable category="idents" noheader="true" nostripe="false"
startRow="${site.mode=='design'?5:4}">
<c:set var="row" value="0"/><tr class="<onecmdb:stripe row="${row}" />">
<td></b>Id</td>
<td colspan="${(site.mode=="design") ? 4 : 2}">${ci.id}</td>
</tr>
<c:set var="row" value="${row+1}"/><tr class="<onecmdb:stripe row="${row}" />">
<td></b>Alias</td>
<td colspan="${(site.mode=="design") ? 4 : 2}"><form:alias ci="${ci}" /></td>
</tr>
<c:set var="row" value="${row+1}"/><tr class="<onecmdb:stripe row="${row}" />">
<td>Display Name</td>
<td colspan="${(site.mode=="design") ? 4 : 2}"><form:displaynameexpr ci="${ci}" />
</td>
</tr>
<c:set var="row" value="${row+1}"/><tr class="<onecmdb:stripe row="${row}" />">
<td>Description</td>
<td colspan="${(site.mode=="design") ? 4 : 2}"><form:description ci="${ci}" />
</td>
</tr>

<c:if test="${site.mode=='design'}">
<c:set var="row" value="${row+1}"/><tr class="<onecmdb:stripe row="${row}" />">
<td>Template</td>
<td colspan="${(site.mode=="design") ? 4 : 2}"><form:input property="BLUEPRINT" type="checkbox" /></td>
</tr>
</c:if>
<tr>
<td colspan="${(site.mode=="design") ? 5 : 3}"></td>
</tr>


</form:attrtable>
</div>

<div class="section">
<h3>Attributes</h3>

<form:attrtable category="uncategorized" />
</div>

<p align="right">
<form:cancel text="Cancel" /><form:apply text="Apply" />&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</p>

<c:if test="${site.mode=='design'}">
<c:if test="${site.mode=='design' && site.action.ci.blueprint}">
<!-- {{{ Add Attribute -->
<%-- 

	Add an attribute to this item

--%><div class="section">
<h3><a name="newattr">Define New Attribute</a></h3>

<onecmdb:error property="addAttr(${site.action.ci.id})" />

<p>Defines and adds a new attribute to this <strong>${site.action.ci.displayName}</strong>. 
Fill in the form and press the <strong>add</strong> button to register the attribute.</p>
<p>By using the <em>prefix</em> field one can control the sort order, that is 
attributes with prefix "A" will always be listed before attributes with a prefix
"B". If no prefix is used, sorting is based on the attribute's name. Note that 
the prefix will never be displayed.</p>

<table border="0">
<tr>
	<td><b>Prefix</b></td>
	<td><b>Name</b></td>
	<td><b>&nbsp;</b></td>
	<td><b><spring:message code="dict.template" /></b></td>
	<td><b><spring:message code="dict.multiplicity" /></b></td>
</tr>
<tr>	
	<td valign="top">
		<form:input size="2" property="addAttr(${site.action.ci.id}):prefix" /></td>
	<td valign="top">
		<form:input property="addAttr(${site.action.ci.id}):dispExpr" /></td>
	<td valign="top">
		<form:input property="addAttr(${site.action.ci.id}):type" type="type" >
		</td><td></form:input>
	</td>
	<td valign="top" rowspan="4">
		<form:input type="multiplicity" property="addAttr(${site.action.ci.id}):mult" />	
	</td>
</tr>
<tr>
	<td valign="top" colspan="3"><b>Description</b><br>
	<form:input property="addAttr(${site.action.ci.id}):description" type="textarea" /></td>
</tr>
</table>
<p align="right"><strong>Note:</strong> The attribute will be added on 
<strong>${site.action.ci.displayName}</strong>, and any other item based on it.
&nbsp;&nbsp;<form:change operation="addAttr(${site.action.ci.id})" text="Add" 
hash="newattr" noErrorFeedback="true" />
</p>
<spring:bind path="site.action.formParams[addAttr(${site.action.ci.id})]">
<c:if test="${not empty status.value}">
${status.displayValue}
</c:if>
</spring:bind>

</div>
<!--  }}} -->
</c:if>
</c:if>
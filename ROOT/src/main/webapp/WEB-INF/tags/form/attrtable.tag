<%@ tag display-name="AttributeTable"
	description="Renders a table, expsoing editable controls for all attributes from a certain category."
%><%@
include file="/WEB-INF/jsp/include.jsp" %><%@
taglib prefix="onecmdb" tagdir="/WEB-INF/tags/onecmdb" %><%@
taglib prefix="form" tagdir="/WEB-INF/tags/form" %><%@
attribute name="category" required="true" type="java.lang.String"%><%@
attribute name="noheader" required="false"%><%@
attribute name="nostripe" required="false"%><%@
attribute name="startRow" required="false" type="java.lang.Integer" 
%><table border="0" summary="${category} attributes">
<c:if test="${not noheader}">
<thead><tr>
<th>Name</th>
<c:choose><c:when test="${site.action.ci.blueprint}">
<th>Default value</th>
</c:when><c:otherwise>
<th>Value</th>
</c:otherwise>
</c:choose>	
<c:if test="${site.mode=='design'}">
	<th><spring:message code="dict.template"/></th>
	<th><spring:message code="dict.multiplicity" /></th>
</c:if>
	<td>&nbsp;</td>
</tr>
</thead>
</c:if>
<tbody>
<c:set var="columns" value="${3 + (site.mode=='design' ? 2 : 0) }" />
<c:set var="count" value="0"/>

<jsp:doBody /><c:forEach var="attrList" 
items="${site.action.categorizedAttributes[category]}" 
varStatus="attrListSt"><c:set var="stripeClass"><c:if test="${not nostripe}"
>class="<onecmdb:stripe row="${startRow + attrListSt.index}" />"</c:if
	></c:set><tr ${stripeClass}>
		<td	<c:choose>
	<c:when test="${attrList.meta.maxOccurs==1}">
		>
		<onecmdb:popupattrname attribute="${attrList.meta}"/>
		</td>		
		<td><c:forEach var="value" items="${attrList.values}" varStatus="valueStatus">
		<c:if test="${valueStatus.first}">
		
		<form:attrwidget attr="${value}" deletable="false" returnHash="${attrList.meta.alias}">
		</td>
		<c:if test="${site.mode=='design'}">
		<td><onecmdb:ci ci="${attrList.meta.valueType}" /></td>
		<td><onecmdb:multiplicity attr="${attrList.meta}" /></td>
		</c:if>
		<td align="right">
			<onecmdb:error property="deleteAttr(${attrList.meta.alias})"/>				
		</form:attrwidget>

		<c:if test="${site.mode=='design' && site.action.ci.blueprint}">
			<!-- {{{ delete attribute -->
			<onecmdb:error property="deleteAttr(${attrList.meta.alias})"/>				
			<form:change text="Trash" img="trashcan" alt="Delete Attribute"
			operation="deleteAttr(${attrList.meta.alias})" hash="${category}" 
			prompt="About to delete the \\'${attrList.meta.alias}\\' attribute from \\'${site.action.ci.displayName}\\'."
			/><!-- }}} -->
			</c:if>
		</c:if>

		<c:if test="${valueStatus.index==1}"><br><small><em>(more than one value exists)</em></small></c:if>


		</c:forEach>
		</td>
		</tr>

	</c:when><c:otherwise>
		><onecmdb:popupattrname attribute="${attrList.meta}"/>
		</td>
		<td></td>
		<c:if test="${site.mode=='design'}">
			<td><onecmdb:ci ci="${attrList.meta.valueType}" /></td>
			<td><onecmdb:multiplicity attr="${attrList.meta}" /></td>
		</c:if>
		<td align="right">
			<c:if test="${site.mode=='design' && site.action.ci.blueprint}">
			<onecmdb:error property="deleteAttr(${attrList.meta.alias})"/>				
			<form:change text="Trash" img="trashcan" alt="Delete Attribute" 
			operation="deleteAttr(${attrList.meta.alias})" hash="${category}" 
			prompt="About to delete the \\'${attrList.meta.alias}\\' attribute, and all its values, from \\'${site.action.ci.displayName}\\'."
			/></c:if>
		</td>
		</tr>

		<!--  {{{ attribute's values -->
		<c:forEach var="attr" items="${attrList.values}" varStatus="valueStatus">
		<tr ${stripeClass}>
			<td align="right"><small>[${valueStatus.index + 1}]</small></td>
			
			<td class="<onecmdb:stripe row="${valueStatus.index}" level="2" />">
			<c:if test="${site.debugEnabled}">
			<small><em>alias</em>:${attr.alias} <em>owner</em>:${attr.owner.id}</small><br>
			</c:if>
				<form:attrvalue attr="${attr}" value="${attr.value}" />
			</td>
			<c:if test="${site.mode=='design'}">
				<td class="<onecmdb:stripe row="${valueStatus.index}" level="2" />" colspan="2">&nbsp;</td>
			</c:if>	
			<td class="<onecmdb:stripe row="${valueStatus.index}" level="2" />" align="right">
			<c:if test="${ not site.action.ci.blueprint }">
			<form:change text="Delete" img="delete" alt="Delete Value" operation="removeValue(${attr.id})" hash="${attrList.meta.alias}"  
			disabled="${attrList.size <= attrList.meta.minOccurs}" />
			</c:if>
			</td>
			
		</tr>	
		</c:forEach>

		<!-- {{{ add value -->
		<c:if test="${ not site.action.ci.blueprint || site.debugEnabled }">
		<tr ${stripeClass}>
		<td></td>
		<td>
			<c:if test="${site.debugEnabled}">
			<small><em>alias</em>:${attrList.meta.alias} <em>owner</em>:${attrList.meta.owner.id}</small><br>
			</c:if>
		
			<form:attrwidget propertyAttr="${attrList.meta.alias}"
    	   	  attr="${attrList.meta}" returnHash="${attrList.meta.alias}" >
    	   	</td>
    	   	
			<c:if test="${site.mode=='design'}"><td colspan="2"></td></c:if>
    	   	
    	   	<td align="right">
    	   	  
    	   	 </form:attrwidget>
    	   	  
		
	    	<form:change text="Add" operation="addValue(${attrList.meta.alias})" hash="${attrList.meta.alias}"
	    	  disabled="${attrList.meta.maxOccurs != -1 && attrList.size >= attrList.meta.maxOccurs}" />
		</td>
		</tr>		
		</c:if>
		<!-- }}} -->
		
		<!-- }}} -->
	</c:otherwise>	
	</c:choose>	
<c:set var="count" value="${count+1}"/>
<c:remove var="stripeClass"/>
</c:forEach>
<c:if test="${count==0}"><tr class="<onecmdb:stripe row="${count}"/>">
<td colspan="${columns}"><em>List is empty</em></td>
</tr></c:if>
<c:remove var="count"/><c:remove var="columns"/>
</tbody>
</table>

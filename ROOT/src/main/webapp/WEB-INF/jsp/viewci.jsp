<%-- 
		Views a CI in ``datacenre'' mode:
		* folders
		* actions

	${site.action} evaluates to a org.onecmdb.web.ViewCiAction; look in javadoc
	for what to expect from the object.

--%><%@
  include file="include.jsp" %><%@ 
  taglib prefix="form" tagdir="/WEB-INF/tags/form" %><%@
  taglib prefix="onecmdb" tagdir="/WEB-INF/tags/onecmdb" %><%@
  taglib prefix="debug" tagdir="/WEB-INF/tags/debug" 
 
%><onecmdb:actionheading action="${site.action}" />

<!--  {{{ special -->
<onecmdb:special ci="${site.action.ci}" />
<!-- }}}} -->

<c:if test="${site.action.predicates.hasCi}">
<!-- {{{ graph -->
<div  class="section">
<h3><onecmdb:toggle name="Graph" />&nbsp;Graph</h3>

<spring:bind path="site.globals['showGraph']">
<c:if test="${status.value}">

<div align="right">
<table cellspacing="0" cellpadding="0" bgcolor="silver" style="">
<tr>
<td>
<spring:bind path="site.globals['graphData'].depth"><small>Depth:&nbsp;</small><select name="${status.expression}"
><c:forEach var="i" begin="2" end="20"><option<c:if test="${i==status.value}"> selected</c:if>
value="${i}">${i}</option> 
</c:forEach></select>
</spring:bind>
</td>
<td>
<spring:bind path="site.globals['graphData'].relationType"
><small>References:&nbsp;</small><select name="${status.expression}"
><c:forEach var="v" items="${site.globals['graphData'].allRelationTypes}"
><option<c:if test="${v==status.value}"> selected</c:if>
value="${v.name}"><spring:message code="refs.${v.name}" text="${v}" /></option> 
</c:forEach></select>
</spring:bind>
</td>
<td>
<input type="submit" value="Update">
</td>
</tr></table>
</div>
<p align="center"><a href="<c:url value="/graphs/imagemap"> 
<c:param name="ciid">${site.action.ci.id}</c:param>
</c:url>">
<img border="0" ismap src="<c:url value="/graphs/generate"> 
<c:param name="ciid">${site.action.ci.id}</c:param>
<c:param name="format">png</c:param>
<c:param name="depth">${site.globals['graphData'].depth}</c:param>
<c:param name="relationType">${site.globals['graphData'].relationType}</c:param>
</c:url>"></a>
</p>

</c:if>
</spring:bind>
</div>
<!-- }}} -->
</c:if>


<!-- {{{ attributes -->
<div id="attributes" class="section">
<h3><onecmdb:toggle name="Attributes" />&nbsp;Attributes</h3>

<spring:bind path="site.globals['showAttributes']">
<c:if test="${status.value}">
<c:set var="cols" value="2"/>

<table border="0" summary="Attribute List">
<thead>
	<tr>
	<th>Name</th>
	<th>Value</th><c:if test="${site.mode == 'design'}" >
	<c:set var="cols" value="${cols+2}"/>
	<th><spring:message code="dict.template"/></th>
	<th><spring:message code="dict.multiplicity"/></th>
	</c:if>
	</tr>
</thead>
<tbody>
<c:set var="row" value="0"/>
<c:forEach var="attrList" items="${site.action.categorizedAttributes['uncategorized']}" 
varStatus="attrListStatus" >
	<c:choose>
	<c:when test="${attrList.meta.maxOccurs != 1}">
		<!-- {{{ multivalued attribute -->
		<tr class="<onecmdb:stripe row="${row}" />">
			<td>
				<onecmdb:popupattrname attribute="${attrList.meta}"/>
			</td>
			<td></td>
			<c:if test="${site.mode == 'design'}" >
				<td><form:cilink ci="${attrList.meta.valueType}"/></td>
				<td><onecmdb:multiplicity attr="${attrList.meta}" /></td>
			</c:if>
		</tr>
		<c:forEach var="attr" items="${attrList.values}" varStatus="attrStatus">
		<tr class="<onecmdb:stripe row="${row}" />">
			<td align="right"><small>[${attrStatus.index + 1}]</small></td>
			<td class="<onecmdb:stripe row="${attrStatus.index}"
			level="2" />"><form:attrvalue attr="${attrList.meta}" value="${attr.value}" /></td>

			<c:if test="${site.mode == 'design'}" >
			<td class="<onecmdb:stripe row="${attrStatus.index}"
			level="2" />" ></td><td class="<onecmdb:stripe row="${attrStatus.index}"
			level="2" />" ></td>
			</c:if>
		</tr>
		</c:forEach>
		<!-- }}} -->
	</c:when>
	<c:otherwise>
			<!-- {{{ single valued -->
			<tr class="<onecmdb:stripe row="${row}" />">
				<td>
					<onecmdb:popupattrname attribute="${attrList.meta}"/>
				</td>
				<td>
					<form:attrvalue attr="${attrList.meta}" value="${attrList.meta.value}" />
				</td>
				<c:if test="${site.mode == 'design'}" >
				<td>
					<form:cilink ci="${attrList.meta.valueType}"/>
				</td>
				<td>
					<onecmdb:multiplicity attr="${attrList.meta}" />
				</td>
				</c:if>
			</tr>
			<!-- }}} -->
	</c:otherwise>
	</c:choose>
<c:set var="row" value="${row + 1}"/>
</c:forEach>
<c:if test="${row==0}"><tr class="<onecmdb:stripe row="${row}" />"><td colspan="${cols}"><em>List is empty</em></td></tr></c:if>
<c:remove var="row"/><c:remove var="colspan"/>
 </tbody>
 </table>
</c:if>
</spring:bind>
</div>
<!-- }}} -->


<onecmdb:referrers />




<c:if test="${site.mode == 'design' || site.debugEnabled}" >
<!-- {{{ Offsprings -->


<div class="section">
<h3><onecmdb:toggle name="Offsprings" />&nbsp;Descendants</h3>
<spring:bind path="site.globals['showOffsprings']">
<c:if test="${status.value}">
<onecmdb:offsprings ci="${site.action.ci}" />
</c:if>
</spring:bind>
</div>
<!-- }}} -->
</c:if>





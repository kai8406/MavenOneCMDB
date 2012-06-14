<%@ 
include file="include.jsp" %><%@ 
taglib prefix="form" tagdir="/WEB-INF/tags/form" %><%@ 
taglib prefix="onecmdb" tagdir="/WEB-INF/tags/onecmdb" %><%@
taglib prefix="debug" tagdir="/WEB-INF/tags/debug" %><%@ 
page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!--  {{{ template chain -->
<div class="heading">
<table summary="Navigational History header"><tr>
<td><h4><onecmdb:toggle name="TemplateChain" />Template Chain</h4><td>
</tr>
</table>
</div>

<div class="navigation">
<c:if test="${site.globals['showTemplateChain']}">
<c:if test="${site.action.predicates.hasCi}">
<table summary="Template Chain"><tbody>
<c:forEach varStatus="status" var="template" items="${site.action.ci.offspringPath.list}" >
	<c:if test="${not status.first}"><tr><td>&nbsp;
	<img alt="Template relation" src="<c:url value='/images/conec16.gif'/>" border="0" >
	</td></tr></c:if>
      <tr><td><form:cilink ci="${template}" /></td></tr>
</c:forEach>
<tr><td>${site.action.ci.alias} contains attributes from all templates in the chain.</td></tr>
</tbody></table>
</c:if>
<c:if test="${not site.action.predicates.hasCi}">
<p><em>N/A</em></p>
</c:if>
</c:if>
</div>
<!-- }}} --> 

 



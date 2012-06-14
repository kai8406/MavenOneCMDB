<%@ 
include file="include.jsp" %><%@ 
taglib prefix="form" tagdir="/WEB-INF/tags/form" %><%@ 
taglib prefix="onecmdb" tagdir="/WEB-INF/tags/onecmdb" %>
<!--  {{{ search -->
<div class="heading">
<table summary="Navigational History header"><tr>
<td><h4><onecmdb:toggle name="Search" />Search</h4><td>
</tr>
</table>
</div>

<div class="navigation">
<c:if test="${site.globals['showSearch']}">
<spring:bind path="site.globals[searchText]">
<p><input type="text" name="${status.expression}" value="${status.value}" onKeyPress="return disableEnterKey(event)">
<form:navigate text="Go" method="get" to="searchresult" /></p>
</spring:bind>
</c:if>
</div>
<!-- }}} --> 

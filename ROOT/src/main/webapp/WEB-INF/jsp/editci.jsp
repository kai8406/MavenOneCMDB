<%-- 
	${site.action} evaluates to a org.onecmdb.web.EditCiAction; look in javadoc
	for what to expect from the object.
--%>
<%@ include file="include.jsp" %>
<%@ taglib prefix="form" tagdir="/WEB-INF/tags/form" %>
<%@ taglib prefix="onecmdb" tagdir="/WEB-INF/tags/onecmdb" %>
<onecmdb:actionheading action="${site.action}" />

<onecmdb:formerror />

<form:editattributes ci="${site.action.ci}" />

<onecmdb:referrers />

<c:if test="${site.mode == 'design' || site.debugEnabled}" >
<!-- {{{ Offsprings -->

<div class="section">
<h3>Decendants</h3>

<onecmdb:offsprings ci="${site.action.ci}" />

</div>
<!-- }}} -->
</c:if>






<%@ include file="include.jsp" %>
<%@ taglib prefix="onecmdb" tagdir="/WEB-INF/tags/onecmdb" %>

<c:if test="${site.debugEnabled}">
<onecmdb:actionheading action="${site.action}" />
</c:if>

<div class="heading">
<table summary="Help"><tr>
<td><h4>Help</h4></td>
</tr></table>
</div>

<div class="instruction">
<table>
<tr><td>
Visit the online documentation at <a href="http://www.onecmdb.org/wiki/index.php/Documentation">http://www.onecmdb.org/wiki/index.php/Documentation</a>
<br><br><br><br><br><br>
</td></tr>
</table>
</div>


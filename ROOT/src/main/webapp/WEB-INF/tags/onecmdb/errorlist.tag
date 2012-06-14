<%@ tag display-name="error"
    description="" %><%@
	attribute name="status" required="true" type="org.springframework.web.servlet.support.BindStatus" %><%@
	include file="/WEB-INF/jsp/include.jsp" %><%@
	taglib prefix="form" tagdir="/WEB-INF/tags/form" %><%@
	taglib prefix="onecmdb" tagdir="/WEB-INF/tags/onecmdb" %><c:if 
	
	test="${status.error}">
<c:choose>
<c:when test="${status.errorCode != 'SUCCESS'}">
<div class="error"><table><tbody><tr><td valign="top"><onecmdb:icon
id="error" size="LARGE" /></td>
<td>
<c:forEach var="msg" items="${status.errorMessages}" varStatus="msgStatus">
<c:choose>
<c:when test="${msgStatus.first && msgStatus.last}">
<p><c:out value="${msg}"/></p>
</c:when>
<c:otherwise>
<c:if test="${msgStatus.first}"><ul></c:if>
<li><c:out value="${msg}"/></li>
<c:if test="${msgStatus.last}"></ul></c:if>
</c:otherwise>
</c:choose>
</c:forEach>
</td>
</tr></tbody></table>
</div>

</c:when>
<c:otherwise>

<div class="info"><table><tbody><tr><td valign="top"><onecmdb:icon
id="info" size="LARGE" /></td>
<td><p>Success!</p>
<c:forEach var="msg" items="${status.errorMessages}" varStatus="msgStatus">
<c:choose>
<c:when test="${msgStatus.first && msgStatus.last}">
<p><c:out value="${msg}"/></p>
</c:when>
<c:otherwise>
<c:if test="${msgStatus.first}"><ul></c:if>
<li><c:out value="${msg}"/></li>
<c:if test="${msgStatus.last}"></ul></c:if>
</c:otherwise>
</c:choose>
</c:forEach>
</td>
</tr></tbody></table>
</div>

</c:otherwise>
</c:choose>


</c:if>













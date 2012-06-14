<%@
tag display-name="displayNameExprWidget"
    description="Renders a form input field for editing the display name expression." %><%@
attribute name="ci" required="true" type="org.onecmdb.core.ICi" %><%@
include file="/WEB-INF/jsp/include.jsp" %><%@
taglib prefix="form" tagdir="/WEB-INF/tags/form" %><%@
taglib prefix="onecmdb" tagdir="/WEB-INF/tags/onecmdb" %><spring:bind
path="site.action.formParams[ALIAS]"><!--  {{{ displayNameExpr -->

<c:choose>
<c:when test="${site.mode=='design'}">
<input type="text" name="${status.expression}" value="<c:out value="${status.value}" />" onKeyPress="return disableEnterKey(event)"></c:when>
<c:otherwise>
${status.value}
</c:otherwise>
</c:choose>
<c:if test="${status.error}"><div class="error"><c:out value="${status.errorMessage}"/></div></c:if>

<!--  }}} --></spring:bind>	
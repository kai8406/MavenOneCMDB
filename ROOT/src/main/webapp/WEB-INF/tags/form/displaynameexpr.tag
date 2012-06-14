<%@
tag display-name="displayNameExprWidget"
    description="Renders a form input field for editing the display name expression." %><%@
attribute name="ci" required="true" type="org.onecmdb.core.ICi" %><%@
include file="/WEB-INF/jsp/include.jsp" %><%@
taglib prefix="form" tagdir="/WEB-INF/tags/form" %><%@
taglib prefix="onecmdb" tagdir="/WEB-INF/tags/onecmdb" %><spring:bind
path="site.action.formParams[DISPEXPR]"><!--  {{{ displayNameExpr -->
<input type="text" name="${status.expression}" value="${status.value}" onKeyPress="return disableEnterKey(event)">
<!--  }}} --></spring:bind>	
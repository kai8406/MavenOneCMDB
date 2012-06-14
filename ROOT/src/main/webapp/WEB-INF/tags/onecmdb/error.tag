<%@ tag display-name="error"
    description="" %><%@
attribute name="property" required="true" %><%@
include file="/WEB-INF/jsp/include.jsp" %><%@
taglib prefix="form" tagdir="/WEB-INF/tags/form" %><%@
taglib prefix="onecmdb" tagdir="/WEB-INF/tags/onecmdb" %>
<spring:bind path="site.action.formParams['${property}']">
<onecmdb:errorlist status="${status}" />
</spring:bind>













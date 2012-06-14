<%@ tag display-name="FormError"
    description="Produces an overall form error message, based on the whole form" %><%@
include file="/WEB-INF/jsp/include.jsp" %><%@
taglib prefix="form" tagdir="/WEB-INF/tags/form" %><%@
taglib prefix="onecmdb" tagdir="/WEB-INF/tags/onecmdb" 
%><spring:bind path="site">
<onecmdb:errorlist status="${status}" />
</spring:bind>














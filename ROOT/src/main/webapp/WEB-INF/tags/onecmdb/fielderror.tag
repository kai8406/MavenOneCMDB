<%@ tag display-name="fielderror"
    description="Simple error message for entered data, using 
    the property name as input." %><%@
attribute name="property" required="true" 
	type="java.lang.String"%><%@
include file="/WEB-INF/jsp/include.jsp" %><%@
taglib prefix="form" tagdir="/WEB-INF/tags/form" %><%@
taglib prefix="onecmdb" tagdir="/WEB-INF/tags/onecmdb" %>
<spring:bind path="site.action.formParams['${property}']">
<c:if test="${status.error}">
<div class="error">!! ${status.errorMessage} !! </div>
</c:if>
</spring:bind>













<%@ tag 
    display-name="xstime"
    description="Renders a form input field for editing a time attribute" %>
<%@ attribute name="property" required="true" type="java.lang.String" %>
<%@ include file="/WEB-INF/tags/include.tagf" %>
<%@ taglib prefix="form" tagdir="/WEB-INF/tags/form" %>
<%@ taglib prefix="onecmdb" tagdir="/WEB-INF/tags/onecmdb" %>
<spring:bind path="site.action.formParams[${property}]">
<!--  {{{ xs:time -->
<input type="text" name="${status.expression}" value="${status.displayValue}">
<!--  }}} -->
</spring:bind>
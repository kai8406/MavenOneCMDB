<%@ tag 
    display-name="xsdateWidget"
    description="Renders a form input field for editing a time attribute." %>
<%@ attribute name="property" required="true" type="java.lang.String" %>
<%@ include file="/WEB-INF/jsp/include.jsp" %>
<%@ taglib prefix="form" tagdir="/WEB-INF/tags/form" %>
<%@ taglib prefix="onecmdb" tagdir="/WEB-INF/tags/onecmdb" %>
<%--
	The attribute is bound to the current action's form paramters, letting Spring's
	bind mechanism do the job of transforming the passed value to a model specific
	value, reachable from the action.
	
	Be looking at the attribute's type, a specific widget is generated.
	
	Stylesheet classes used:
	
 --%>
<spring:bind path="site.action.formParams[${property}]">
<!--  {{{ xs:date -->
<input type="text" name="${status.expression}" value="${status.displayValue}">
<!--  }}} -->
</spring:bind>


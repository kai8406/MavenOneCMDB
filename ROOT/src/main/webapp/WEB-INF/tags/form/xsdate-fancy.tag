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

<spring:bind path="site.action.formParams[${property}].year">
<sup><small>Year</small></sup><input type="text" size="2" maxlength="4" name="${status.expression}" 
	value="${site.action.formParams[property].nullValue
	|| site.action.formParams[property].year==-2147483648
	? '' : status.displayValue}">

<c:if test="${status.error}">
<span class="error">${status.errorMessage}</span>
</c:if>
</spring:bind>

<spring:bind path="site.action.formParams[${property}].month">
<sup><small>Month</small></sup><select name="${status.expression}">
<option value=""></option>
<c:forEach var="m" begin="1" end="12">
<option value="${m}"<c:if test="${m==status.value}"> selected</c:if> >${m<10 ? '0' :''}${m}</option>
</c:forEach>
</select>
<c:if test="${status.error}">
<span class="error">${status.errorMessage}</span>
</c:if>
</spring:bind>

<spring:bind path="site.action.formParams[${property}].day">
<sup><small>Day</small></sup><select name="${status.expression}">
<option value=""></option>
<c:forEach var="d" begin="1" end="31">
<option value="${d}"<c:if test="${d==status.value}"> selected</c:if> >${d<10 ? '0' :''}${d}</option>
</c:forEach>
</select>
<c:if test="${status.error}">
<span class="error">${status.errorMessage}</span>
</c:if>
</spring:bind>

<spring:bind path="site.action.formParams[${property}].nullValue">
	<small>Clear<input type="checkbox" value="true" name="${status.expression}"
	<c:if test="${status.value}">checked</c:if>></small>
</spring:bind>


<!--  }}} -->
</spring:bind>


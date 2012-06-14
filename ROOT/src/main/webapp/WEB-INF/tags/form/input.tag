<%@	tag
	display-name="inputWidget"
    description="Renders a simple form input field, bound to a certain property" 
%><%@
attribute name="property" required="true" type="java.lang.String" %><%@
attribute name="size" required="false" %><%@
attribute name="type" required="false" type="java.lang.String" %><%@
attribute name="value" required="false" type="java.lang.String" %><%@
attribute name="options" required="false" type="java.util.Collection" %><%@
attribute name="selectedOption" required="false" type="java.lang.String" %><%@
attribute name="firstOption" required="false" type="java.lang.String" %><%@
attribute name="onchange" required="false" type="java.lang.String" %><%@
include file="/WEB-INF/jsp/include.jsp" %><%@
taglib prefix="form" tagdir="/WEB-INF/tags/form" %><%@
taglib prefix="onecmdb" tagdir="/WEB-INF/tags/onecmdb" %><spring:bind
path="site.action.formParams[${property}]"><c:if 
test="${site.debugEnabled}">${property}</c:if><c:choose><%-- 

Textarea

--%>
<c:when test="${type=='textarea'}">
<textarea style="width:100%" name="${status.expression}" <c:if test="${not empty size}">cols="${size}"</c:if> type="_moz">${status.value}</textarea>
</c:when><%-- 

Checkbox

--%>
<c:when test="${type=='checkbox'}"><input type="hidden" name="_${status.expression}"
value="false"><input type="${type}" name="${status.expression}" value="true"<c:if 
test="${status.value=='true' || status.displayValue=='true'}"> checked</c:if>>
</c:when><%-- 

Select

--%><c:when test="${type=='select'}">
<select name="${status.expression}"><c:if test="{not empty onchange}"> onchange="${onchange}"</c:if>
<c:if test="${not empty firstOption}"><option value="">--- ${firstOption} ---</option></c:if>
<c:forEach var="option" items="${options}">
<c:set var="optval">${empty option.id ? '' : 'ID:'}${empty option.id ? option.alias : option.id}</c:set>
<option value="${optval}"<c:if test="${status.value==optval}"> selected</c:if>>
${ (empty option.displayName) ? option.alias : option.displayName }</option></c:forEach>
</select>
</c:when><%-- 

Multiplicity

--%><c:when test="${type=='multiplicity'}"
><table class="control">
<tr><td nowrap>
	<spring:bind path="site.action.formParams[${property}.min]">
	<input name="${status.expression}" type="text" size="1" style="width:1em"
	value="${empty status.displayValue && !status.error ? '1' : status.displayValue}"></spring:bind
	>..<spring:bind path="site.action.formParams[${property}.max]"><input name="${status.expression}"
	type="text" size="1" style="width:1em"  ${status.displayValue == 'n' ? "disabled" : ""}
	value="${empty status.displayValue && !status.error ? '1' : status.displayValue}">
	</spring:bind>
</td></tr>
<tr><td align="left"nowrap>
	<spring:bind path="site.action.formParams[${property}.maxInf]">
	<label><small><input type="checkbox" name="${status.expression}" type="checkbox" value="n"
		<spring:bind path="site.action.formParams[${property}Max]">
		 ${status.displayValue == 'n' ? "checked" : ""}
		</spring:bind>
		onclick="if (this.checked) { 
			this.form['action.formParams[${property}.max]'].value='n';
			this.form['action.formParams[${property}.max]'].disabled=true;
			} else { 
				this.form['action.formParams[${property}.max]'].disabled=false;
				this.form['action.formParams[${property}.max]'].focus();
				this.form['action.formParams[${property}.max]'].select();
		}">Unlimited</small>
	</label>
	</spring:bind>
	<onecmdb:fielderror property="${property}Min"/>
	<onecmdb:fielderror property="${property}Max"/>
</td></tr>
</table>
</c:when><%-- 

Type

--%><c:when test="${type=='type'}">
<!-- {{{ type selection -->

<form:input property="${property}ref" type="select" options="${site.allRefTypes}" />
<jsp:doBody />

<select name="${status.expression}"
onchange="handleTypeChange(this.form['${status.expression}'],this.form['action.formParams[${property}ref]'])" 

><option value="">--- Type ---</option>
<c:forEach var="category" items="${site.typeMap}">

<optgroup label="${category.key}">

<c:forEach var="option" items="${category.value}">
<c:set var="optval">${empty option.id ? '' : 'ID:'}${empty option.id ? option.alias : option.id}</c:set>

<option value="<c:out value="${optval}"/>" label="" <c:if 
test="${status.value==optval}"> selected</c:if>><onecmdb:ciname ci="${option}" /></option></c:forEach>

</optgroup>
</c:forEach>

</select>

<!-- }}} -->
</c:when>

<c:otherwise>
<input type="text" name="${status.expression}"<c:if 
test="${not empty size}"> size="${size}"</c:if> value="${ (empty status.value) ? value : status.value}" onKeyPress="return disableEnterKey(event)"></c:otherwise></c:choose>
<c:if test="${status.error}"><div class="error">${status.errorMessage}</div></c:if>
</spring:bind>	
<%@ tag 
    display-name="Attribute Widget"
    description="Renders a form input field for editing a specific attribute,
    using a switch-like approach" %><%@
attribute name="attr" required="true" type="org.onecmdb.core.IAttribute" %><%@
attribute name="propertyAttr" required="false" %><%@
attribute name="returnHash" required="false" %><%@
attribute name="label" required="false" %><%@
attribute name="deletable" required="false" type="java.lang.Boolean" %><%@
attribute name="table" required="false" %><%@
include file="/WEB-INF/jsp/include.jsp" %><%@
taglib prefix="form" tagdir="/WEB-INF/tags/form" %><%@
taglib prefix="onecmdb" tagdir="/WEB-INF/tags/onecmdb" %><%--

	The attribute is bound to the current action's form parameters, letting Spring's
	bind mechanism do the job of transforming the passed value to a model specific
	value, reachable from the action.
	
	By looking at the attribute's type, a specific widget is generated.
	
	Stylesheet classes used:

		To be written

 --%>
<c:set var="property">
	<c:choose>
		<c:when test="${empty propertyAttr}">
			ATTR${attr.id}
		</c:when>
		<c:otherwise>
			${propertyAttr}
		</c:otherwise>
	</c:choose>
</c:set>
<c:set var="bindPath">site.action.formParams[${property}]</c:set>
<spring:bind path="${bindPath}">
<c:set var="errmsg"><c:if test="${status.error}">
<div class="error">!! ${status.errorMessage} !!</div>
</c:if>
</c:set>
<c:if test="${label == 'true'}">
	<c:if test="${table == 'true'}"><td></c:if>
	<span class="label">${status.expression}</span>:
	<c:if test="${table == 'true'}"></td></c:if>
</c:if>
<c:if test="${table=='true'}"><td></c:if>

<onecmdb:ciicon ci="${empty attr.value ? attr.valueType : attr.value}" 
nodecorations="true" size="SMALL" alt="Template"/>
<c:choose>
<c:when test="${attr.valueType.alias == 'xs:string'}">
<!--  {{{ xs:string -->
<c:choose>
<c:when test="${attr.alias=='Description' || attr.alias=='description'}">
<textarea name="${status.expression}"><c:out value="${status.value}"/></textarea>
</c:when>
<c:when test="${attr.alias=='icon'}">
<select name="${status.expression}">
<c:import url="/icons/options">
	<c:param name="selected" value="${status.value}"/>
</c:import>
<c:set var="newurl">
<form:actionurl nohref="true" navigate="addci" 
	action="${site.actionMap['addicon']}" 
	returnTo="${site.currentHistory}"
	returnParam="${returnParam}" 
	returnHash="${returnHash}">New...</form:actionurl>
</c:set>
</select>
</c:when>
<c:otherwise>
<input type="text" name="${status.expression}" value="<c:out 
value="${status.value}"/>" onKeyPress="return disableEnterKey(event)">
</c:otherwise>
</c:choose>${errmsg}
<jsp:doBody />
<!--  }}} -->
</c:when>


<c:when test="${attr.valueType.alias == 'xs:integer'}">
<!--  {{{ xs:integer -->
<input type="text" value="<c:out value="${status.value}"/>" name="${status.expression}" onKeyPress="return disableEnterKey(event)">
${errmsg}
<jsp:doBody />
<!--  }}} -->
</c:when>

<c:when test="${attr.valueType.alias == 'xs:boolean'}">
<!--  {{{ xs:boolean -->
<input type="hidden" value="false" name="_${status.expression}">
<input type="checkbox" value="true" name="${status.expression}"<c:if test="${status.value}"> checked</c:if>>
${errmsg}
<jsp:doBody />
<!--  }}} -->
</c:when>

<c:when test="${attr.valueType.alias == 'xs:time'}">
<!--  {{{ xs:time -->
<form:xstime property="${property}" />
${errmsg}
<jsp:doBody />
<!--  }}} -->
</c:when>
<c:when test="${attr.valueType.alias == 'xs:date'}">
<!--  {{{ xs:date -->
<form:xsdate property="${property}" />
${errmsg}
<jsp:doBody />
<!--  }}} -->
</c:when>
<c:when test="${attr.valueType.alias == 'xs:dateTime'}">
<!--  {{{ xs:dateTime -->
<form:xsdatetime property="${property}" />
${errmsg}
<jsp:doBody />
<!--  }}} -->
</c:when>
<c:when test="${attr.valueType.alias == 'xs:anyURI'}">
<!--  {{{ xs:anyURI -->
<input type="input" value="${status.displayValue}" name="${status.expression}">
${errmsg}
<jsp:doBody />
<!--  }}} -->
</c:when>


<c:when test="${not fn:startsWith(attr.valueType.alias, 'xs:')}">
<!--  {{{ other type (reference) -->
<c:set var="_selected" value="false" />
<select size="1" name="${status.expression}" onchange="setLocation(this.form, this.options[this.selectedIndex].value)<c:if 
test="${not empty propertyAttr}">;setFormMessage(this.options[this.selectedIndex].value!='','Please take action attribute \'${attr.alias}\', before applying.')</c:if>"><option value="">-- available ---</option>
<c:forEach varStatus="st" var="opt" items="${attr.valueSelector.set}">
<option <c:if test="${opt.asString==site.globals['mem'].ci.asString}">
<c:set var="_recallable" value="true"/>
</c:if>value="${opt.asString}"<c:if test="${not empty status.value and status.value 
== opt.asString}"> selected<c:set var="_selected" value="true"/></c:if>><c:out 
value="${opt.displayName}"/></option></c:forEach>

<c:forEach var="tpl" items="${attr.typeSelector.set}" varStatus="tplStatus">
<c:if test="${tplStatus.first}"><optgroup label="Create New"></c:if>

<c:set var="newurl">
<c:choose>
	<c:when test="${empty attr.valueType.id}"></c:when>
	<c:otherwise>
		<c:choose>
			<c:when test="${empty returnHash}">
<form:actionurl nohref="true" 
	action="${site.actionMap['addci']}" 
	ci="${tpl.id}" 
	returnTo="${site.currentHistory}"
	returnParam="${property}">$(tpl.displayName}</form:actionurl>
			</c:when>
			<c:otherwise>
<form:actionurl nohref="true"
	action="${site.actionMap['addci']}" 
	ci="${tpl.id}" 
	returnTo="${site.currentHistory}"
	returnParam="${property}" 
	returnHash="${returnHash}">$(tpl.displayName}</form:actionurl>
			</c:otherwise>
		</c:choose>
	</c:otherwise>
</c:choose>
</c:set>
<option value="loc:${newurl}">${tpl.displayName}</option>

<c:if test="${tplStatus.last}"></optgroup></c:if>
</c:forEach>
</select>
<c:if test="${_recallable}"><span class="mem">&nbsp;<a title="Recalls object from memory" 
href="javascript:setSelection('${status.expression}','${site.globals['mem'].ci.asString}'<c:if 
test="${not empty propertyAttr}">, 'Please take action attribute \'${attr.alias}\', before applying.'
</c:if>)"><b>RCL</b></a>&nbsp;</span></c:if>


${errmsg}
<jsp:doBody />
<c:if test="${_selected && not empty propertyAttr}">
<script>
setFormMessage(true, 'Take action for attribute "${attr.alias}", before applying.');
</script>
</c:if>



<c:remove var="returnParam" />
<c:remove var="_selected"/>


<c:if test="${deletable}">
<form:change text="(X)" operation="remove(${attr.id})"  />
</c:if>

<!--  }}} -->
</c:when>
<c:otherwise>
<!--  {{{ default -->
<input type=text" value="<c:out value="${status.value}"/>" name="${status.expression}">
${errmsg}
<jsp:doBody />
<!--  }}} -->

</c:otherwise>
</c:choose>

<c:if test="${table == 'true'}"></td></c:if>
<c:if test="${site.debugEnabled}"><br><small>${status.editor.value.class}

${status.expression}=${status.value}</small><br></c:if>


</spring:bind>
<c:remove var="bindPath" />
	
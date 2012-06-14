<%-- 
   -      Formats a simple value according to its type
   --%><%@ 
attribute name="attr" required="true" type="org.onecmdb.core.IType" %><%@ 
attribute name="value" required="true" type="org.onecmdb.core.IValue" %><%@
include file="/WEB-INF/tags/include.tagf" %><%@
taglib  prefix="onecmdb" tagdir="/WEB-INF/tags/onecmdb" %>
<c:choose>
<c:when test="${empty value}">
	<em>empty</em>
</c:when>
<c:when test="${''==value.displayName}">
	<em>empty</em>
</c:when>
<c:when test="${value.nullValue}">
	<em>null</em>
</c:when>
<c:when test="${attr.valueType.alias=='xs:date'}">
${value}

</c:when>
<c:when test="${attr.valueType.alias=='xs:time'}">

${value}


</c:when>
<c:when test="${attr.valueType.alias=='xs:dateTime'}">

${value}

</c:when>

<c:when test="${attr.valueType.alias=='xs:boolean'}">
	<c:choose><c:when test="${value.asJavaObject}">
	<onecmdb:icon id="checked" alt="true"/></c:when>
	<c:otherwise><onecmdb:icon id="unchecked" alt="false"/></c:otherwise>
	</c:choose>
</c:when>

<c:when test="${attr.valueType.alias=='xs:anyURI'}">

<a href="<c:out value="${value.asString}"/>" target="_blank"><c:out value="${value.asString}"/></a>

</c:when>

<c:otherwise>
	<c:out value="${value.displayName}"/>
</c:otherwise>
</c:choose>
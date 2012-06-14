<%-- 
   -      An attribute value
   -       
   --%><%@ 
attribute name="attr" required="true" type="org.onecmdb.core.IType" %><%@ 
attribute name="value" required="true" type="org.onecmdb.core.IValue" %><%@ 
attribute name="history" required="false" %><%@ 
include file="/WEB-INF/tags/include.tagf" %><%@
taglib prefix="form" tagdir="/WEB-INF/tags/form" %><%@
taglib prefix="onecmdb" tagdir="/WEB-INF/tags/onecmdb" %><c:set
var="icon"><onecmdb:ciicon ci="${empty value ? attr : value}" nodecorations="true" /></c:set><c:choose><c:when
test="${empty value.id}">${icon}&nbsp;<onecmdb:simplevalue attr="${attr}" value="${value}" />
</c:when><c:otherwise>${icon}&nbsp;<c:set target="${scratch}" property="ci" 
value="${value.id}"/><form:actionurl
cssclass="linka${(site.action.predicates.hasCi && site.action.ci == value) ? 'selected' : ''}" 
navigate="${(empty history) ? 'viewci' : history}" 
action="${site.actionMap['viewci']}" 
params="${scratch}"
title="${attr.referenceType.displayName}"
><c:out value="${value.displayName}"/></form:actionurl>&nbsp;<onecmdb:mem ci="${value}" /><c:if 
test="${site.debugEnabled}"><sub>(<c:out
value="${value.alias}" />)</sub></c:if
><%-- <sup>
<onecmdb:ciicon nodecorations="true" ci="${attr.referenceType}" 
title="${attr.referenceType.displayName}" size="SMALL"/></sup>  
--%></c:otherwise></c:choose>
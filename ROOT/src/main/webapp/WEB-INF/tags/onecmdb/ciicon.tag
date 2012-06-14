<%@
tag 
    display-name="CI Icon"
    description="Creates an image tag, displaying the passed CI's icon" 
%><%@
include file="/WEB-INF/jsp/include.jsp" %><%@ 
taglib prefix="onecmdb" tagdir="/WEB-INF/tags/onecmdb"%><%@ 
attribute name="ci" required="true" type="org.onecmdb.core.IType" %><%@ 
attribute name="nodecorations" required="false" type="java.lang.Boolean" %><%@ 
attribute name="size" required="false" %><%@ 
attribute name="alt" required="false" %><%@ 
attribute name="title" required="false" %><sup><tt>${(ci.blueprint 
&& !nodecorations) ? '<b>T</b>' :'&nbsp;'}</tt></sup><img
border="0" alt="${alt}" title="${title}" src="<c:set var="url"><c:url 
value="/icons/generate"><c:param name="iconid" value="${ci.icon}" 
/><c:choose><c:when test="${empty size}"><c:param name='size' 
value="SMALL" /></c:when><c:otherwise><c:param name="size" 
value="${size}" /></c:otherwise></c:choose></c:url></c:set><c:out 
value="${url}"/>">
<%@ tag 
	display-name="attrname"
	description="Genereates an attribute's display name. May take the current
	context into account ${site.mode}"
%><%--

This tag evalautes, and outputs, an attribute's display name. 

--%><%@ 

include file="/WEB-INF/jsp/include.jsp" %><%@ 
attribute name="attribute" type="org.onecmdb.core.IAttribute" %>

<c:choose>
<c:when test="${site.mode=='user'}">
<c:out value="${empty attribute.displayName || ''==attribute.displayName ? attribute.alias : attribute.displayName}" />
</c:when>
<c:when test="${site.mode=='design' && attribute.blueprint}">
<c:out value="${attribute.alias}" />
</c:when>
<c:otherwise>
<c:out value="${empty attribute.displayName ? attribute.alias : attribute.displayName}" />
</c:otherwise>
</c:choose>



<%@ tag 
	display-name="ciname"
	description="Genereates an CI's display name. May take the current
	context into account, i.e. ${site.mode}"
%><%--

This tag evaluates, and outputs, a CI's display name. 

--%><%@ 

include file="/WEB-INF/jsp/include.jsp" %><%@ 
attribute name="ci" type="org.onecmdb.core.IType" 
%><c:choose><c:when test="${site.mode=='user'}"><%--

Output the name in user mode 

--%><c:out 
value="${empty ci.displayName || ''==ci.displayName ? ci.alias : ci.displayName}" 
/></c:when><c:when test="${site.mode=='design'}"><%--

Output the name in design mode

--%><c:out 
value="${empty ci.displayName || ''==ci.displayName ? ci.alias : ci.displayName}" 
/>[<c:out 
value="${ci.alias}" />]</c:when><c:otherwise><%--

(Should never occur) 

--%><c:out 
value="${empty ci.displayName || ''==ci.displayName ? ci.alias : ci.displayName}" /></c:otherwise></c:choose>
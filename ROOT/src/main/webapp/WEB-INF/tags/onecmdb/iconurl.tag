<%@ tag 
    display-name="IconUrl"
    description="Creates an url accessing generated icons" %><%@ 
include file="/WEB-INF/jsp/include.jsp" %><%@ 
attribute name="id" required="true" %><%@ 
attribute name="size" required="false" %><%@ 
attribute name="alt" required="false" 
%><c:set var="url"><c:url value="/icons/generate" 
><c:param name="iconid" value="${id}"  
/><c:choose><c:when test="${empty size}"><c:param name="size" 
value="SMALL" /></c:when><c:otherwise><c:param name="size" 
value="${size}" /></c:otherwise></c:choose></c:url></c:set><c:out 
value="${url}"/><c:remove var="url"/>
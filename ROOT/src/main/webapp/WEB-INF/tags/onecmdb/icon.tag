<%@ tag 
    display-name="Icon"
    description="Creates an image, using the image's id" 
%><%@ 
include file="/WEB-INF/jsp/include.jsp" %><%@ 
attribute name="id" required="true" %><%@ 
attribute name="size" required="false" %><%@ 
attribute name="alt" required="false" %><img border="0" <c:if 
test="${not empty alt}"> alt="${alt}" </c:if>src="<c:set var="url"><c:url 
value="/icons/generate"><c:param name="iconid" value="${id}" 
/><c:choose><c:when test="${empty size}"><c:param name='size' 
value="SMALL" /></c:when><c:otherwise><c:param name="size" 
value="${size}" /></c:otherwise></c:choose></c:url></c:set><c:out value="${url}" />">
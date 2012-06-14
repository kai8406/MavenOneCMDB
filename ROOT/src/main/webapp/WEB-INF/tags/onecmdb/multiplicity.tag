<%@ tag 
    display-name="RFC output"
    description="Renders RFC output" 
%>
<%@ include file="/WEB-INF/jsp/include.jsp" %>
<%@ attribute name="attr" required="true" type="org.onecmdb.core.IAttribute" %>

<c:choose><c:when 
test="${attr.minOccurs==attr.maxOccurs}">${attr.minOccurs}</c:when><c:when
test="${attr.minOccurs==-1 && attr.maxOccurs==-1}">n</c:when><c:when
test="${attr.minOccurs!=-1 && attr.maxOccurs==-1}">${attr.minOccurs}..n</c:when><c:when
test="${attr.minOccurs==-1 && attr.maxOccurs!=-1}">0..${attr.maxOccurs}</c:when><c:otherwise>${attr.minOccurs}..${attr.maxOccurs}</c:otherwise></c:choose>






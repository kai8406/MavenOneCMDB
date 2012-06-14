<%@ page contentType="text/plain" %><%@ include file="include.jsp"
%><option value="">&lt;icons&gt;</option>
<c:forEach var="imageList" items="${data.images}"><option<c:if test="${imageList.key==data.selected}"> selected</c:if>><c:out value="${imageList.key}"/></option>
</c:forEach>
<%@ include file="include.jsp" 
%><%@ taglib prefix="onecmdb" tagdir="/WEB-INF/tags/onecmdb" 
%><h1>Image Upload</h1>

<c:if test="${!empty imageAdd.successful}">
<c:choose>
<c:when test="${imageAdd.successful}">
<p>Image added.</p>
</c:when>
<c:otherwise>
<p>Failed to add image.</p>
</c:otherwise>
</c:choose>
</c:if>


<form action="<c:url value="/icons/add"/>"
enctype="multipart/form-data" method="post">

<p>Use this form to upload a new icon to OneCMDB.</p>
<p>Key (name) <input type="text" name="iconid"><br>
Image <input type="file" name="iconData">&nbsp;<input type="submit"></p>
</form>

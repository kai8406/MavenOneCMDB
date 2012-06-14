<%@ include file="include.jsp" 
%><%@ taglib prefix="onecmdb" tagdir="/WEB-INF/tags/onecmdb" %><h1>Image List</h1>

<form action="<c:url value="/icons/add"/>"
enctype="multipart/form-data" method="post">

<p>Use this form to upload a new icon to OneCMDB.</p>
<p>Key (name) <input type="text" name="iconid"><br>
Image <input type="file" name="iconData">&nbsp;<input type="submit"></p>
</form>


<table>
	<c:forEach var="imageList" items="${data.images}">
	<tr>
		<td>
			<c:out value="${imageList.key}"/>
		</td>
		<td>
			<onecmdb:icon id="${imageList.key}" size="SMALL"/>
		</td>
		<td>
		<c:forEach var="image" items="${imageList.value}">
			<c:out value="${image.width}x${image.height}"/>
		</c:forEach>
		</td>
	</tr>
</c:forEach>
</table>


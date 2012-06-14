<%@ include file="include.jsp" %>
<%@ taglib prefix="form" tagdir="/WEB-INF/tags/form" %>
<%@ taglib prefix="debug" tagdir="/WEB-INF/tags/debug" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<div class="debug"><tt>
<b>History Index:</b> ${site.currentHistory}(${site.historyLength})<br>
<b>Action:</b>        (${site.action.id}) ${site.action}<br>
<c:forEach var="p" items="${site.action.formParams}" varStatus="pStatus">
<c:if test="${pStatus.first}"><b>Form Params:</b> {<ol></c:if
><li><b>${p.key}</b> &lt;<i>${p.value.class.name}</i>&gt; <c:out value="${p.value.asString}"/></li><c:if
test="${pStatus.last}"></ol>}</c:if></c:forEach>
<b>Previous:</b><c:if test="${ not empty site.previous}">(${site.previous.id}) ${site.previous}
<c:forEach var="p" items="${site.previous.formParams}" varStatus="pStatus">
<c:if test="${pStatus.first}"><br><b>Form Params:</b> {<ol></c:if>
<li><b>${p.key}</b> &lt;<i>${p.value.class.name}</i>&gt; <c:out value="${p.value.asString}"/></li>

<c:if test="${pStatus.last}"></ol>}</c:if>

</c:forEach>



</c:if> 

</tt>






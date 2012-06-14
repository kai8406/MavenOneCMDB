<%-- 
   -      Renders a text input field according to the path provided
   --%>
<%@ include file="/WEB-INF/tags/include.tagf" %>
<%@ attribute name="path" required="true" %>
<%@ attribute name="label" required="false" %>
<%@ attribute name="table" required="false" %>
<spring:bind path="${path}">

<c:if test="${label == 'true'}">
	<c:if test="${table == 'true'}"> <td> </c:if>
<span class="label">${status.expression}</span>:
	<c:if test="${table == 'true'}"></td></c:if>
</c:if>

<c:if test="${table == 'true'}"> <td> </c:if>

<textarea name="${status.expression}">${status.value}</textarea>
<c:if test="${status.error}"><div class="error">${status.errorMessage}</div></c:if>

<c:if test="${table == 'true'}"></td></c:if>
	
</spring:bind>
	
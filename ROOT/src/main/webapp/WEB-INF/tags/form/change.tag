<%-- 
   -      Renders a form submission field:
   -      text: Text to be used
   -      type: A real submission is indcated of a type `submit', other types
   -            reflect a form change request
   -       
   --%><%@
include file="/WEB-INF/jsp/include.jsp" %><%@
attribute name="operation" required="true" %><%@
attribute name="text" required="true" %><%@
attribute name="title" required="false" %><%@
attribute name="img" required="false" %><%@
attribute name="alt" required="false" %><%@
attribute name="disabled" required="false" type="java.lang.Boolean" %><%@
attribute name="prompt" required="false" type="java.lang.String" %><%@
attribute name="hash" required="false" %><%@
attribute name="noErrorFeedback" required="false" %><%@ 
taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %><%@
taglib prefix="onecmdb" tagdir="/WEB-INF/tags/onecmdb" %>
<input<c:if test="${disabled}"> disabled</c:if><c:if test="${not empty title}"> title="${title}"</c:if> 
class="actionurl" name="change:${site.action.name}:${operation}" 
<c:choose>
<c:when test="${empty img}">
type="submit" 
</c:when>
<c:otherwise>
type="image" src="<onecmdb:iconurl id="${img}" size="SMALL"/>" 
alt="${empty alt ? img : alt}"
</c:otherwise>
</c:choose>

value="${text}"<c:set var="onclick">cancelForm()<c:if 
test="${not empty hash}">;setAction(this.form, '${hash}')</c:if>
<c:if test="${not empty prompt}">;return confirm('${prompt}\n\nContinue?')</c:if>
</c:set>
<c:if test="${not empty onclick}">onclick="${onclick}"</c:if>><c:if test="${!noErrorFeedback}">
<onecmdb:error property="${operation}" /></c:if>



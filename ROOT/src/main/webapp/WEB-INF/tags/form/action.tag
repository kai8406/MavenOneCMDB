<%-- 
   -      Renders a form submission field:
   -      text: Text to be used
   -      type: A real submission is indcated of a type `submit', other types
   -            reflect a form change request
   -       
   --%>
<%@ attribute name="text" required="true" %>
<%@ attribute name="type" required="false" %>
<%@ attribute name="data" required="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<input class="action" type="submit" value="${text}" onClick="doAction('${type}'<c:if
 test="${data != ''}">,'${data}'</c:if>)">


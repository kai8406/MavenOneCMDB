<%-- 
   -      Renders a form submission field:
   -      text: Text to be used
   -      type: A real submission is indcated of a type `submit', other types
   -            reflect a form change request
   -       
   --%><%@ attribute name="text" required="true" %><%@ taglib
prefix="c" uri="http://java.sun.com/jsp/jstl/core" %><input class="actionurl"
type="submit" name="apply:${site.action.name}" value="${text}"
<c:if test="${not empty site.action.params['RETURNHASH']}">onclick="setAction(this.form,'${site.action.params['RETURNHASH']}')"</c:if>
<c:if test="${site.debugEnabled}"
> onclick="alert("Apply ${site.action.name}""</c:if>>


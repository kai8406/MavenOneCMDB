<%-- 
   -      Renders a form submission field:
   -      text:   Text to be used.
   -      to:     The name of an action, e.g. 'help'.
   -      hash:   On optional named anchor to move to.       
   -      method: POST or GET used for the navigation.       
   --%><%@ attribute name="text" required="true" %><%@ 
   attribute name="to" required="true" %><%@ 
   attribute name="hash" required="false" %><%@ 
   attribute name="method" required="false" %><%@ taglib
prefix="c" uri="http://java.sun.com/jsp/jstl/core" %><input class="actionurl"
type="submit" value="${text}" onclick="setNavigate(this.form, '${to}')<c:if 
test="${not empty hash}">;setAction(this.form, '${hash}')</c:if><c:if 
test="${not empty method}">;setMethod(this.form, '${method}')</c:if>">




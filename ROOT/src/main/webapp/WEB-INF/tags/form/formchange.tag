<%@ attribute name="path" required="true" %>
<%@ attribute name="text" required="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>   
<%@ taglib prefix="spring" uri="/spring" %>   

<spring:bind path="${path}">
<input type="submit" name="${status.expression}" value="${text}">
</spring:bind>




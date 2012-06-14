<%@ page session="false" %>
<%@ include file="/WEB-INF/jsp/include.jsp" %>
<%-- Redirected because we can't set the welcome page to a virtual URL. --%>

LOGIN


<form method="post" action="j_acegi_security_check">
<input name="j_username">
<input name="j_password">
<input type="submit">
</form>


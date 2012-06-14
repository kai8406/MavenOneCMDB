<%-- 
    Add a new offspring from current CI (site.action.ci)

	${site.action} evaluates to a org.onecmdb.web.ViewCiAction; look in javadoc
	for what to expect from the object.

--%>
<%@ include file="include.jsp" %>
<%@ taglib prefix="form" tagdir="/WEB-INF/tags/form" %>
<%@ taglib prefix="onecmdb" tagdir="/WEB-INF/tags/onecmdb" %>

<onecmdb:actionheading action="${site.action}" />

<onecmdb:formerror />


<p>Lets us upload a new icon, to the system.</p> 

<input name="icon" type="file">


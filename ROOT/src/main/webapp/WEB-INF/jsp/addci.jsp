<%-- 
    Add a new offspring from current CI (site.action.ci)

	${site.action} evaluates to a org.onecmdb.web.ViewCiAction; look in javadoc
	for what to expect from the object.

--%>
<%@ include file="include.jsp" %>
<%@ taglib prefix="form" tagdir="/WEB-INF/tags/form" %>
<%@ taglib prefix="onecmdb" tagdir="/WEB-INF/tags/onecmdb" %>
<%@ taglib prefix="debug" tagdir="/WEB-INF/tags/debug" %>

<onecmdb:actionheading action="${site.action}" />

<onecmdb:formerror />


<p>Creating a new instance, that is an <em>offspring</em>, using 
<strong>${site.action.ci.derivedFrom.displayName}</strong> as template.</p> 
<p>Please fill in the form, setting the default values, on the instance
to create.</p>

<form:editattributes ci="${site.action.ci}" />

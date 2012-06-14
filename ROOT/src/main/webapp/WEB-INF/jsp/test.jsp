<%@ include file="include.jsp" %>
<%@ taglib prefix="onecmdb" tagdir="/WEB-INF/tags/onecmdb" %>

<onecmdb:actionheading action="${site.action}" />
<p>Test Page</p>

<p>This generates an error for non-admins:</p>

${site.session.dateCreated}



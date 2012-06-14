<%@ include file="include.jsp" %>
<%@ taglib prefix="form" tagdir="/WEB-INF/tags/form" %>
<%@ taglib prefix="onecmdb" tagdir="/WEB-INF/tags/onecmdb" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Welcome</title>
<link rel="stylesheet" title="Clean" type="text/css" href="stylesheet.css">
</head>
<body>
<!-- the whole page is set to belong to one form -->

<form:begin />

<!-- {{{ Header -->
<jsp:include page="header.jsp" />
<!-- }}} -->

<!-- {{{ Navigation -->
<jsp:include page="navigation.jsp" />
<!-- }}} -->

<!-- {{{ Content  -->
<div class="content">

<!-- {{{ Current ``Path'' (stripped) -->

<!--  }}} -->
Action: ${pageData.contentView}<br>

<c:import url="${pageData.contentView}.jsp" /> 

</div>
<!-- }}} -->


<!-- {{{ Footer -->
<jsp:include page="footer.jsp" />
<!-- }}} -->

<jsp:include page="debug.jsp" />

<form:end />
</body>
</html>
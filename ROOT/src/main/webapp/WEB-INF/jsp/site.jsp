<%@ page language="java"  
	contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"
	session="true" %><%@
	include file="include.jsp" %><%@
	taglib prefix="form" tagdir="/WEB-INF/tags/form" %><%@
	taglib prefix="onecmdb" tagdir="/WEB-INF/tags/onecmdb" 
%><!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html;charset=UTF-8">
<title>OneCMDB | ${site.action.displayName} (${site.mode})</title>
<link rel="stylesheet" title="Clean" type="text/css" href="stylesheet.css">
<script src="site.js" type="text/javascript"></script>
<script src="utility.js" type="text/javascript"></script>
<script src="popup.js" type="text/javascript"></script>
<script src="enter.js" type="text/javascript"></script>
</head>
<body bgcolor="white">
<!-- the whole page is set to belong to one form -->
<form:begin />

<!-- {{{ Header -->
<jsp:include page="header.jsp" />
<!-- }}} -->

<table width="100%" border="0" bordercolor="#808080" cellpadding="0" 
cellspacing="1"><tbody valign="top">
<tr>
<c:choose>
<c:when test="${site.session.anonymous}">
<td></td>
</c:when>
<c:otherwise>
<td width="20%">

<!-- {{{ Navigation -->
<jsp:include page="navigation.jsp" />
<!-- }}} -->

</td>
</c:otherwise>

</c:choose>
<td>
<!-- {{{ Content  -->
<div class="content">
<c:choose>
<c:when test="${!site.session.anonymous}">


<%-- simple mapping via the action's name --%>
<c:import url="${site.action.name}.jsp" /> 

</c:when>
<c:otherwise>

<div class="heading">
<table summary="Action Header"><tr>
<td><h4>Login</h4></td>
</tr></table>
</div>

<p>Please, provide your credentials to access OneCMDB.</p>

<form:login />

</c:otherwise>
</c:choose>
</div>
<!-- }}} -->
</td>
</tr></tbody></table>



<!-- {{{ Footer -->
<jsp:include page="footer.jsp" />
<!-- }}} -->
<!-- <hr>
<spring:bind path="site.debugEnabled">Debug <input type="hidden" value="false" 
  name="_${status.expression}"><input type="checkbox" name="${status.expression}" 
  value="true" <c:if test='${status.value}'>checked</c:if>>
</spring:bind><input type="submit" value="Go"><c:if test="${site.debugEnabled}"><jsp:include
page="debug.jsp" /></c:if>-->
<form:end />
</body>
</html>
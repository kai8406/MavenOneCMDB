<%-- 
   -      Login promt
   -       
   --%><%@
include file="/WEB-INF/jsp/include.jsp" %><%@
taglib prefix="form" tagdir="/WEB-INF/tags/form" %><%@
taglib prefix="onecmdb" tagdir="/WEB-INF/tags/onecmdb" %>



<onecmdb:error property="login()" />

<div class="login">

<c:out value="${err}" />
<table>
<tr>
	<td>Username:</td>
	<td><spring:bind path="site.session.authentication.username">
<input name="${status.expression}" value="${status.value}" onKeyPress="return disableEnterKey(event)">
</spring:bind>
	</td>
</tr>	
<tr>
 <td>Password:</td>
 <td><spring:bind path="site.session.authentication.password">
  <input type="password" name="${status.expression}" onKeyPress="return disableEnterKey(event)">
  </spring:bind>
 </td>
</tr>
<tr>
 <td colspan="2" align="right">
  <form:change operation="login()" text="Login" noErrorFeedback="true" />
 </td>
</tr>
</table>
</div>


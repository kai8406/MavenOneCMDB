<%@ include file="include.jsp" %>
<%@ taglib prefix="form" tagdir="/WEB-INF/tags/form" %>
<%@ taglib prefix="onecmdb" tagdir="/WEB-INF/tags/onecmdb" %>
<div class="header">
  <table summary="Header" border="0">
    <tr>
      <td rowspan="2">
        <form:actionurl navigate="instructions" action="${site.actionMap['instructions']}" >
        <img alt="OneCMDB logo" src="<c:url value='/images/onecmdblogo.jpg'/>" border="0" >
        </form:actionurl>
      <td align="right" valign="top">
	      <span class="login">
			<c:choose>
			
				<c:when test="${!site.session.anonymous}">
					Logged in as <strong><authz:authentication operation="username"/></strong>
		 	    	<form:change operation="logout()" text="Logout" />
				</c:when>
			<c:otherwise>
				Anonymous
			</c:otherwise>
			</c:choose>
		</span>
      </td>
    </tr>
    <tr>
      <td align="right">
        <!--<spring:bind path="site.debugEnabled">Debug <input type="hidden" value="false" 
          name="_${status.expression}"><input type="checkbox" name="${status.expression}" 
          value="false" <c:if test='${status.value}'>checked</c:if>>
        </spring:bind> |-->
	Your feedback to onecmdb.org:<a  href="" onClick="window.open('<c:url
		value="http://www.onecmdb.org/feedback/good.html">
		<c:param name="version" value="${site.version}" />
		<c:param name="action" value="${not empty site.action ? site.action.name : ''}" />
	</c:url>','','height=500,width=500');return false">[Good]</a>&nbsp;<a href=""
	 onClick="window.open('<c:url
		value="http://www.onecmdb.org/feedback/bad.html">
		<c:param name="version" value="${site.version}" />
		<c:param name="action" value="${not empty site.action ? site.action.name : ''}" />
	</c:url>','','height=500,width=500');return false">[Bad]</a>&nbsp;&nbsp;&nbsp;
	<a href="" onClick="window.open('<c:url
		value="http://sourceforge.net/project/showfiles.php?group_id=176340">
	</c:url>');return false">[Check for updates]</a>&nbsp;&nbsp;&nbsp;
        Mode <spring:bind path="site.mode">
        <select name="mode" onchange="this.form.submit()">
          <option<c:if test='${status.value == "user"}'> selected</c:if> value="user">user</option>
          <option<c:if test='${status.value == "design"}'> selected</c:if> value="design">designer</option>
        </select></spring:bind>&nbsp;&nbsp;<form:actionurl navigate="instructions" cssclass="linka" action="${site.actionMap['instructions']}"><onecmdb:icon 
        id="home" alt="Home" /></form:actionurl><form:actionurl 
        navigate="help" cssclass="linka" action="${site.actionMap['help']}"><onecmdb:icon id="help" 
        alt="Help" /></form:actionurl>        
        </td>
    </tr>
  </table>
  
</div>
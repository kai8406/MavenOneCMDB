<%@ tag 
    display-name="toggle"
    description="Generates a toggle element" 
    
%><%@ 
  include file="/WEB-INF/jsp/include.jsp" %><%@ 
  attribute name="name" required="true" 

%>
<spring:bind path="site.globals['show${name}']">
<a name="${name}" href="<c:url value="">
<c:param name="${status.expression}">${status.value == 'true' ? 'false' : 'true' }</c:param></c:url>#${name}"><img 
border="0" src="<c:url value="/images/menu_${status.value == 'true' ? 'open' : 'closed' }.gif" />"></a>
</spring:bind>


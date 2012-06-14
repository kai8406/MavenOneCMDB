<%@ tag 
    display-name="RFC output"
    description="Renders RFC output" 
%>
<%@ include file="/WEB-INF/jsp/include.jsp" %>
<%@ attribute name="rfc" required="true" type="org.onecmdb.core.IRFC" %>
<%@ attribute name="table" required="false" %>
${rfc.id} ${rfc}




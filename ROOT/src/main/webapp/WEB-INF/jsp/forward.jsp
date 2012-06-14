<%@ include file="include.jsp" %>
<%@ taglib prefix="form" tagdir="/WEB-INF/tags/form" %>
<c:set var="url"><form:actionurl nohref="true" navigate="viewci" action="${action}" 
params="${params}" /></c:set>

<c:redirect url="/index.mvc${url}" />








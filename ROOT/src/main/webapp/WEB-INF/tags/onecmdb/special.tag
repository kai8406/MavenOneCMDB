<%@ tag 
    display-name="Special"
    description="Outputs the special section" 
%>
<%@ include file="/WEB-INF/jsp/include.jsp" %>
<%@ taglib prefix="onecmdb" tagdir="/WEB-INF/tags/onecmdb" %>
<%@ taglib prefix="debug" tagdir="/WEB-INF/tags/debug" %>
<%@ attribute name="ci" required="true" type="org.onecmdb.core.ICi" %>
<c:if test="${site.action.predicates['hasCi'] && !site.action.ci.blueprint}">
<c:choose>
<c:when test="${site.action.predicates['derivedFrom(DataCenterContainer)']}">
<div id="special" class="section">
<c:import url="/WEB-INF/jsp/special/datacentre.jsp" />
</div>

</c:when>
<c:when test="${site.action.predicates['derivedFrom(JobProcess)']}">
<div id="special" class="section">
<c:import url="/WEB-INF/jsp/special/jobprocess.jsp" />
</div>
</c:when>
</c:choose>
</c:if>
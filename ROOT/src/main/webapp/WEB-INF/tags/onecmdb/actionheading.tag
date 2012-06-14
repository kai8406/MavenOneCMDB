<%@ tag 
    display-name="CI Header"
    description="Outputs a CI header inluding sub actions" 
%>
<%@ include file="/WEB-INF/jsp/include.jsp" %>
<%@ attribute name="action" required="true" type="org.onecmdb.web.SiteAction" %>
<%@ taglib prefix="onecmdb" tagdir="/WEB-INF/tags/onecmdb" %>
<%@ taglib prefix="form" tagdir="/WEB-INF/tags/form" %>


<div class="heading">
<table summary="Action Header"><tr>
<td>
<h4><c:choose>
<c:when test="${action.predicates.hasCi}">
<onecmdb:ci ci="${action.ci}" size="SMALL" /><c:if test="${site.debugEnabled}"><small>{${action.ci.id}}</small></c:if>
</c:when>
<c:otherwise>
<c:out value="${action.displayName}"/>
</c:otherwise>
</c:choose>
</h4>
</td>

<td align="right">
<c:forEach var="subaction" items="${action.subActionMap}"><c:choose><c:when test="${site.action.name == subaction.value.name}"><span 
class="selected">${subaction.value.displayName}</span></c:when><c:otherwise><form:actionurl
navigate="${subaction.value.name}" action="${subaction.value}" params="${action.params}" />
</c:otherwise></c:choose>&nbsp;</c:forEach></td>
</tr></table>
</div>
<div class="heading-text">
<c:if test="${site.mode == 'design' || site.debugEnabled}" >
<p>
<c:if test="${site.action.predicates.hasCi && not empty site.action.ci.derivedFrom}">
<h4>Template</h4>
<form:cilink ci="${site.action.ci.derivedFrom}" />
</c:if>
</c:if>

<c:if test="${site.action.predicates.hasCi && not empty site.action.ci.description}">
<h4>Description</h4>
<c:out value="${site.action.ci.description}"/>
</c:if>
</p>
</div>
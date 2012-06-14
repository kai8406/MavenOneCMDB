<%@ tag display-name="ActionUrl" 
	description="Creates a URL navigating to the passed action."%><%@ 
attribute name="nohref" required="false" type="java.lang.Boolean" %><%@ 
attribute name="action" required="true" type="org.onecmdb.web.SiteAction" %><%@ 
attribute name="navigate" required="false" type="java.lang.String" %><%@ 
attribute name="params" required="false" type="java.util.Map" %><%@ 
attribute name="cssclass" required="false" %><%@ 
attribute name="title" required="false" %><%@ 
attribute name="anchor" required="false" %><%@ 
attribute name="returnTo" required="false" %><%@ 
attribute name="returnParam" required="false" %><%@ 
attribute name="returnHash" required="false" %><%@
tag dynamic-attributes="dynparams"%><%@ 
taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" 
%><c:if test="${!nohref}"><a <c:if test="${not empty title}">title="<c:out 
value="${title}"/>"</c:if><c:choose><c:when test="${not empty cssclass}"> class="${cssclass}"</c:when><c:otherwise
> class="linkb"</c:otherwise></c:choose> href="</c:if><c:set var="url"><c:url value="${pageUrl}"
><c:choose><c:when
test="${not empty navigate}"><c:param
name="navigate">${navigate}</c:param></c:when><c:otherwise
><c:param name="action">${action.name}</c:param></c:otherwise
></c:choose><c:forEach var="p" items="${params}"><c:param 
  name="params[${p.key}]">${p.value}</c:param></c:forEach><c:forEach 
  var="p" items="${dynparams}"><c:param  
  name="params[${p.key}]">${p.value}</c:param></c:forEach><c:if 
  test="${not empty returnTo}"><c:param 
  name="returnTo">${returnTo}</c:param></c:if><c:if 
  test="${not empty returnParam}"><c:param 
  name="returnParam">${returnParam}</c:param></c:if><c:if 
  test="${not empty returnHash}"><c:param 
  name="returnHash">${returnHash}</c:param
></c:if></c:url></c:set><c:out value="${url}" escapeXml="false"/><c:remove var="url"/><c:if test="${!nohref}">"
><jsp:doBody var="text"/><c:choose><c:when
test="${empty text}"><c:out value="${action.displayName}"/></c:when><c:otherwise>${text}</c:otherwise></c:choose></a></c:if>
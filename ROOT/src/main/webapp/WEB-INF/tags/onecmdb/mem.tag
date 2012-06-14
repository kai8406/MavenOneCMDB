<%@ tag 
	display-name="mem"
	description="Genereates a link used to save the a ci in temorary memory for 
	later retrival"
%><%--

This tag creates a M+ object

--%><%@ 

include file="/WEB-INF/jsp/include.jsp" %><%@ 
attribute name="ci" type="org.onecmdb.core.IValue" 
%><c:if test="${! empty ci.id }"><c:choose><c:when test="${site.globals['mem'].id!=ci.id}"><%--

--%><span class="mem"><a title="Save object to memory for later use in forms" href="<c:url 
	    value="${pageUrl}"><c:param name="globals['mem'].id" 
	    value="${ci.id}"/></c:url>#M[${ci.id}]">MEM</a></span></c:when><c:otherwise><%--
--%><span class="mem-current"><a name="M[${ci.id}]">MEM</a></span></c:otherwise></c:choose></c:if>
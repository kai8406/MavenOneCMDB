<%@ tag display-name="Stripe"
    description="Genertes either the class used for an ``odd'' line,  or an ``even'' line" 
%><%@
include file="/WEB-INF/jsp/include.jsp" %><%@
attribute name="row" type="java.lang.Integer" required="true" %><%@ 
attribute name="level" required="false" 
%><c:choose><c:when 
test="${row % 2 == 0}">odd${level}</c:when
><c:otherwise
>even${level}</c:otherwise
></c:choose>
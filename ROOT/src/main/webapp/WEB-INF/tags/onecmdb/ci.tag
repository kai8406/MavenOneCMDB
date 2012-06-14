<%@ tag 
    display-name="CI"
    description="Outputs a CI (the icon with its display name)" 
%><%@ 
include file="/WEB-INF/jsp/include.jsp" %><%@ 
taglib prefix="onecmdb" tagdir="/WEB-INF/tags/onecmdb" %><%@ 
attribute name="ci" required="true" type="org.onecmdb.core.IType" %><%@ 
attribute name="size" required="false"
%><onecmdb:ciicon ci="${ci}" size="${size}" />&nbsp;<onecmdb:ciname 
ci="${ci}" />&nbsp;<onecmdb:mem ci="${ci}" />




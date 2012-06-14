<%@ tag
   display-name="popupAttrName"
   description="Creates the name for an attribute and uses a popup for it, to
	show more information."
%><%@ 
 include file="/WEB-INF/tags/include.tagf" %><%@
 taglib prefix="onecmdb" tagdir="/WEB-INF/tags/onecmdb" %><%@
  
 attribute name="attribute" type="org.onecmdb.core.IAttribute" 

%><c:set var="dispname"><onecmdb:attrname
  	attribute="${attribute}" /></c:set><onecmdb:popup
  	ident="${attribute.alias}" text="${dispname}">
  	
  	  	<c:out value='${attribute.description}'/><br>
  	
  	  	Alias:<strong><c:out value='${attribute.alias}' /></strong>
  	
</onecmdb:popup>
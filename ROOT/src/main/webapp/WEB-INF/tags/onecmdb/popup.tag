<%@ tag
   display-name="popup"
   description="Creates popup effect when hovering, or clicking on the encapsuled object"
%><%@ 
 include file="/WEB-INF/tags/include.tagf" %><%@
 taglib prefix="onecmdb" tagdir="/WEB-INF/tags/onecmdb" %><%@
  
 attribute name="ident" required="true" %><%@
 attribute name="text" required="true" 

%><a class="popupLink" name="<c:out value='${ident}'/>"  
  href="#${ident}"
  onmouseover="showPopupDelayed('POP${ident}', event);"
  onclick="return !showPopup('POP${ident}', event);"
>${text}</a>
<div onclick="event.cancelBubble=true;" class="popup" id="POP${ident}">
<!-- {{{ the actual popup text goes here -->
<jsp:doBody />
<!-- }}} -->
<br><a style="font-size:smaller" href="#${ident}" 
onclick="hideCurrentPopup();return false;">Close</a>
</div>

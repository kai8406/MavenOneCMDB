
<%@ attribute name="expanded" required="true" %>
<%@ attribute name="text" required="true" %>

<input type="text" name="" value="${expanded == 'true' ? 'true' : 'false'}">
<a class="submitLink">${expanded == 'true' ? 'v' : '>'}</a>${text}





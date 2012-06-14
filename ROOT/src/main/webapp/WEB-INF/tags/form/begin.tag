<%-- 
   -      Beginning of a form
   --%>
<%@ include file="/WEB-INF/jsp/include.jsp" %>
<form name="pageForm" method="post" action="<c:url value="${pageUrl}" />" onsubmit="return validateForm(this)">
<input type="hidden" name="navigate" value="">
<script type="text/javascript">

var formMessage = "";

function validateForm(form) {
	if (formMessage != "") {
		alert("Form cannot be submitted!\n\n"+formMessage);
		return false;
	}
	return true;
}
function cancelForm() {
	formMessage="";
}

function setFormMessage(b, msg) {
	formMessage = b ? msg : "";
}

function setNavigate(form, to) {
	form.navigate.value=to;
}

function setMethod(form, method) {
	form.method=method;
}

function setAction(form, hash) {
	var newAction = "${pageUrl}#" + hash;
	form.action=newAction;
}
function setLocation(form, url) {
	if (url.length > 4) {
		var ident = url.substring(0,4);
		if (ident=="loc:") {
			url = url.substring(4);
			setNavigate(form, "addci");
			form.action=url;
			form.submit();
		}
	}
}

function setSelection(select, value, msg) {
	var field = document.forms['pageForm'].elements[select];
	field.value=value;
	if (msg !== undefined) {
		setFormMessage(true, msg);
	}
}

</script>

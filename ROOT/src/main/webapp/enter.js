
function disableEnterKey(event) 
{ 
	return ((event.keyCode ? event.keyCode : (event.which ? event.which : event.charCode)) != 13);
} 

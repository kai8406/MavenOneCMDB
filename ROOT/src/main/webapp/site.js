/**
 *	Input:
 *	
 *		target	A (text) field where to put a concatenated time value. Each time
 *		        component (hour, minute, etc) are expected to be found in the same
 *		        form as the target, using the name of the target, with a prefix, 
 *		        describing the time component held. the following prefixes are 
 *		        recognized:
 *
 *				h Hour
 *		        m Minute
 *		        s Second
 *		
 */
function updateTime(target) {
	alert('Updating');
	var dateField = target.form['date'+target.name];
	var hhField = target.form['h'+target.name];
	var mmField = target.form['m'+target.name];
	var ssField = target.form['s'+target.name];
	
    var newValue = "";
    
    if (dateField !== undefined) {
    	newValue += dateField.value;
    }
    if (hhField !== undefined) {
    	if (newValue != '') 
    		newValue += "T";
    
		newValue += hhField.value +":"+ mmField.value +":"+ ssField.value;
    }
    target.value = newValue;
}


function handleTypeChange(typeField, reftypeField) {
	if (typeField.options[typeField.selectedIndex].value.substring(0,3) == 'ID:') {
		reftypeField.disabled = false;
	} else {
		reftypeField.disabled = true;
	 }
}






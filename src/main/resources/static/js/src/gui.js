/**
 * Send model or model request to Milla
 * @returns
 */
function postToMilla() {
	var xhttp = new XMLHttpRequest(); 
	xhttp.onreadystatechange = function() {
		if (this.readyState == 4 && this.status == 201) {
			document.getElementById("modelName").value = this.responseText;
		} else if(this.readyState == 4) {
			alert("Error:" + this.responseText);
		}
	};
	xhttp.open("POST", document.getElementById("millaUrl").value, true);
	xhttp.setRequestHeader("Content-type", "application/json");
	xhttp.send(document.getElementById("payload").value);
}

/**
 * Fetches a model from Mulperi, i.e. the selectable features
 * @returns
 */
function getOptions() {
	var url = document.getElementById("mulperiUrl").value
		+ "/models/" + document.getElementById("modelName").value;
	
	var xhttp = new XMLHttpRequest(); 
	xhttp.onreadystatechange = function() {
		if (this.readyState == 4 && this.status == 200) {
			createSelectionTree("configuration", JSON.parse(this.responseText)["features"]);
		} else if(this.readyState == 4) {
			alert("Error:" + this.responseText);
		}
	};
	
	xhttp.open("GET", url, true);
	xhttp.setRequestHeader("Content-type", "application/json");
	xhttp.send("[]");
}

/**
 * Finds a valid configuration with CaaS (via Mulperi)
 * @returns
 */
function getConfiguration() {
	var url = document.getElementById("mulperiUrl").value
		+ "/models/" + document.getElementById("modelName").value + "/configurations";
	
	var xhttp = new XMLHttpRequest(); 
	xhttp.onreadystatechange = function() {
		if (this.readyState == 4 && this.status == 200) {
			deselectFeatures();
			selectFeatures("configuration", JSON.parse(this.responseText)["features"]);
		} else if(this.readyState == 4) {
			alert("Error:" + this.responseText);
		}
	};
	
	xhttp.open("POST", url, true);
	xhttp.setRequestHeader("Content-type", "application/json");
	xhttp.send(selectionsToJson());
}

/**
 * Create visual tree of possible selections
 * @param parent ID of parent div
 * @param features A feature has a name and type. 
 * 			It's attributes are in attributes-array and subfeatures are in features-array 
 * @param selected Set checkboxes checked if true
 * @returns
 */
function createSelectionTree(parent, features) {
	for (var i = 0; i < features.length; i++){
		var feature = features[i];
		var name = feature["name"];
		var type = feature["type"];
		
		var checkbox = document.createElement("input");
		checkbox.setAttribute("id", "checkbox_" + type);
		checkbox.setAttribute("type", "checkbox");
		checkbox.onchange = function(){ autosubmit(); };
		
		var element = document.createElement("div");
		element.appendChild(checkbox);
	    element.appendChild(document.createTextNode(name + "/" + type));
	    element.setAttribute("id", name + type);
	    element.setAttribute("class", "level");
	    document.getElementById(parent).appendChild(element);
	    
	    var attributes = {};
	    for (var j = 0; j < feature["attributes"].length; j++) { //dropdown for each attribute
	    	var attribute = document.createElement("select");
	    	attribute.setAttribute("id", "select_" + type + "-" + feature["attributes"][j]["name"]);
	    	attribute.onchange = function(){ autosubmit(); };
	    	attribute.options.add( new Option("Let CaaS decide", "") );
	    	attributes[ feature["attributes"][j]["name"] ] = attribute;
	    	
		}
	    
	    for (var j = 0; j < feature["attributes"].length; j++) { //values for each attribute
	    	var attribute = attributes[ feature["attributes"][j]["name"] ];
	    	attribute.options.add( new Option(feature["attributes"][j]["value"], feature["attributes"][j]["value"]) );
		}
	    
	    for (var key in attributes) {
	    	var attributeDiv = document.createElement("div");
	    	attributeDiv.appendChild(document.createTextNode(key + ": "));
	    	attributeDiv.appendChild(attributes[key]);
	    	attributeDiv.setAttribute("class", "level");
	    	element.appendChild(attributeDiv);
	    }
	    
	    createSelectionTree(name + type, feature["features"]);
	}
}

/**
 * If autosubmit is selected, submit form on selection change
 * @returns
 */
function autosubmit() {
	var e = document.getElementById("autosubmit");
	if(e.options[e.selectedIndex].value === "1") {
		getConfiguration();
	}
}

/**
 * Deselect all checkboxes
 * @returns
 */
function deselectFeatures() {
	var checkboxes = document.querySelectorAll("input[type='checkbox']");
	for (var i=0; i<checkboxes.length; i++) {
		checkboxes[i].checked = false;
	}
}

/**
 * Marks certain checkboxes as checked after configuration
 * @param parent
 * @param features
 * @returns
 */
function selectFeatures(parent, features) {
	for (var i = 0; i < features.length; i++){
		var feature = features[i];
		var name = feature["name"];
		var type = feature["type"];
		
		//select feature
		var checkbox = document.getElementById("checkbox_" + type);
		checkbox.checked = true;
		
		//select attribute values
		var attributes = feature["attributes"];
		for(var j = 0; j < attributes.length; j++) {
			var element = document.getElementById("select_" + type + "-" + attributes[j]["name"]);
			element.value = attributes[j]["value"];
		}
		
		
	    selectFeatures(name, feature["features"]);
	}
}

/**
 * Serialises selected features as JSON
 * @returns
 */
function selectionsToJson() {
	var checkboxes = document.querySelectorAll("input[type='checkbox']");
	
	//features and their attributes into featureSelections
	var featureSelections = [];
	for (var i = 0; i < checkboxes.length; i++) {
		if (checkboxes[i].checked) {
			//select feature
			var type = checkboxes[i].getAttribute("id").replace("checkbox_", "");
			var selection = { "type": type };
			
			//select attributes
			var attributes = document.querySelectorAll("select[id^='select_" + type + "-']");
			var selectedAttributes = [];
			for(var j = 0; j < attributes.length; j++) {
				var attributeName = attributes[j].id.replace("select_" + type + "-", "");
				var attributeValue = attributes[j].options[attributes[j].selectedIndex].value;
				if(attributeValue !== "") {
					selectedAttributes.push( { "name": attributeName, "value": attributeValue } );
				}
			}
			selection["attributes"] = selectedAttributes;
			
			featureSelections.push(selection);
		}
	}
	
	//sum constraints into calculationConstraints
	var calculationConstraints = [];
	for (var i = 0; i < constraintNumber; i++) {
		var attributeName = document.getElementById("constraint_name_" + i).value;
		var operatorElement = document.getElementById("constraint_operator_" + i);
		var operator = operatorElement.options[operatorElement.selectedIndex].value;
		var value = document.getElementById("constraint_value_" + i).value;
		
		if(attributeName != "" && value != "") {
			var constraint = {
					"attName": attributeName,
					"operator": operator,
					"value": value
			}
			calculationConstraints.push(constraint);
		}
	}
	
	var selections = { "featureSelections": featureSelections,
			"calculationConstraints": calculationConstraints };
	
	return JSON.stringify(selections);
}

var constraintNumber = 0;
function addConstraint() {
	var div = document.getElementById("constraints");
	
	var name = document.createElement("input");
	name.setAttribute("type", "text");
	name.setAttribute("id", "constraint_name_" + constraintNumber);
	name.setAttribute("placeholder", "Attribute name");
	name.onchange = function(){ autosubmit(); };
	div.appendChild(name);
	
	var operator = document.createElement("select");
	operator.setAttribute("id", "constraint_operator_" + constraintNumber);
	operator.onchange = function(){ autosubmit(); };
	operator.options.add( new Option(">") );
	operator.options.add( new Option("<") );
	operator.options.add( new Option(">=") );
	operator.options.add( new Option("<=") );
	operator.options.add( new Option("=") );
	operator.options.add( new Option("!=") );
	div.appendChild(operator);
	
	var value = document.createElement("input");
	value.setAttribute("type", "text");
	value.setAttribute("id", "constraint_value_" + constraintNumber);
	value.setAttribute("placeholder", "Compared value");
	value.onchange = function(){ autosubmit(); };
	div.appendChild(value);
	
	div.appendChild(document.createElement("br"));
	constraintNumber++;
}
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
	xhttp.send(JSON.stringify(selectionsToJson()));
}

/**
 * 
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
		checkbox.onchange = function(){ 
			var e = document.getElementById("autosubmit"); //check for autosubmit
			if(e.options[e.selectedIndex].value === "1") {
				getConfiguration();
			}
		};
		
		var element = document.createElement("div");
		element.appendChild(checkbox);
	    element.appendChild(document.createTextNode(name + "/" + type));
	    element.setAttribute("id", name + type);
	    element.setAttribute("class", "level");
	    document.getElementById(parent).appendChild(element);
	    
	    createSelectionTree(name + type, feature["features"]);
	}
}

function deselectFeatures() {
	var checkboxes = document.querySelectorAll("input[type='checkbox']");
	for (var i=0; i<checkboxes.length; i++) {
		checkboxes[i].checked = false;
	}
}

function selectFeatures(parent, features) {
	for (var i = 0; i < features.length; i++){
		var feature = features[i];
		var name = feature["name"];
		var type = feature["type"];
		
		var checkbox = document.getElementById("checkbox_" + type);
		checkbox.checked = true;
		
	    selectFeatures(name, feature["features"]);
	}
}

function selectionsToJson() {
	var checkboxes = document.querySelectorAll("input[type='checkbox']");
	var featureSelections = [];
	
	for (var i=0; i<checkboxes.length; i++) {
		if (checkboxes[i].checked) {
			var selection = { "type":checkboxes[i].getAttribute("id").replace("checkbox_", "") };
			featureSelections.push(selection);
		}
	}
	
	return featureSelections;
}
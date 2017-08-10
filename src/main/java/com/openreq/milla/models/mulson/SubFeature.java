package com.openreq.milla.models.mulson;

import java.util.List;

public class SubFeature {

	List<String> types;
	private String role;
	private String cardinality;
	
	public List<String> getTypes() {
		return types;
	}

	public void setTypes(List<String> types) {
		this.types = types;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getCardinality() {
		return cardinality;
	}

	public void setCardinality(String cardinality) {
		this.cardinality = cardinality;
	}
	
	public void addType(String type) {
		this.types.add(type);
	}
	
}

package com.openreq.milla.models.mulson;

import java.util.ArrayList;
import java.util.List;

public class Requirement {

	private String requirementId;
	private String name;
	private List<Relationship> relationships = new ArrayList<>();
	private List<Attribute> attributes = new ArrayList<>();
	private List<SubFeature> subfeatures = new ArrayList<>();
	
	public String getRequirementId() {
		return requirementId;
	}
	public void setRequirementId(String requirementId) {
		this.requirementId = requirementId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<Relationship> getRelationships() {
		return relationships;
	}
	public void setRelationships(List<Relationship> relationships) {
		this.relationships = relationships;
	}		
	public List<Attribute> getAttributes() {
		return attributes;
	}
	public void setAttributes(List<Attribute> attributes) {
		this.attributes = attributes;
	}
	public List<SubFeature> getSubfeatures() {
		return subfeatures;
	}
	public void setSubfeatures(List<SubFeature> subfeatures) {
		this.subfeatures = subfeatures;
	}
}

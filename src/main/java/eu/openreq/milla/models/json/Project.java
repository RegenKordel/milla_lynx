package eu.openreq.milla.models.json;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


/**
* A Project to store requirements
* 
*/
public class Project {

	/**
	* The unique identifier of a project
	* (Required)
	* 
	*/
	@SerializedName("id")
	@Expose
	private String id;
	/**
	* The name of the project
	* (Required)
	* 
	*/
	@SerializedName("name")
	@Expose
	private String name;
	/**
	* Creation timestamp
	* (Required)
	* 
	*/
	@SerializedName("created_at")
	@Expose
	private int created_at;
	/**
	* Last modification time
	* 
	*/
	@SerializedName("modified_at")
	@Expose
	private int modified_at;
	/**
	* The requirements specified in a project
	* 
	*/
	@SerializedName("specifiedRequirements")
	@Expose
	private List<Requirement> specifiedRequirements = null;
	
	public String getId() {
	return id;
	}
	
	public void setId(String id) {
	this.id = id;
	}
	
	public String getName() {
	return name;
	}
	
	public void setName(String name) {
	this.name = name;
	}
	
	public int getCreated_at() {
	return created_at;
	}
	
	public void setCreated_at(int created_at) {
	this.created_at = created_at;
	}
	
	public int getModified_at() {
	return modified_at;
	}
	
	public void setModified_at(int modified_at) {
	this.modified_at = modified_at;
	}
	
	public List<Requirement> getSpecifiedRequirements() {
	return specifiedRequirements;
	}
	
	public void setSpecifiedRequirements(List<Requirement> specifiedRequirements) {
	this.specifiedRequirements = specifiedRequirements;
	}
}
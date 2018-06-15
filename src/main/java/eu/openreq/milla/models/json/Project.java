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
	private long created_at;
	/**
	* Last modification time
	* 
	*/
	@SerializedName("modified_at")
	@Expose
	private long modified_at;
	/**
	* The requirements specified in a project
	* 
	*/
	@SerializedName("specifiedRequirements")
	@Expose
	private List<String> specifiedRequirements = null;
	
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
	
	public long getCreated_at() {
	return created_at;
	}
	
	public void setCreated_at(long created_at) {
	this.created_at = created_at;
	}
	
	public long getModified_at() {
	return modified_at;
	}
	
	public void setModified_at(long modified_at) {
	this.modified_at = modified_at;
	}
	
	public List<String> getSpecifiedRequirements() {
	return specifiedRequirements;
	}
	
	public void setSpecifiedRequirements(List<String> specifiedRequirements) {
	this.specifiedRequirements = specifiedRequirements;
	}
}
package eu.openreq.milla.models.json;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
* Dependency
* The dependency between requirements
* 
*/
public class Dependency {

	/**
	* The type of dependency between requirements
	* (Required)
	* 
	*/
	@SerializedName("dependency_type")
	@Expose
	private Dependency_type dependency_type;
	/**
	* NLP engine's estimation of the reliability of a proposed dependency
	* 
	*/
	@SerializedName("dependency_score")
	@Expose
	private float dependency_score;
	/**
	* Status of dependency that has been detected by NLP and whether or not the dependency has been approved
	* 
	*/
	@SerializedName("status")
	@Expose
	private Dependency_status status;
	/**
	* The requirement having a dependency
	* (Required)
	* 
	*/
	@SerializedName("from")
	@Expose
	private Requirement from;
	/**
	* The requirement dependent on another
	* (Required)
	* 
	*/
	@SerializedName("to")
	@Expose
	private Requirement to;
	
	/**
	* Creation timestamp
	* (Required)
	* 
	*/
	@SerializedName("created_at")
	@Expose
	private int created_at;

	public Dependency_type getDependency_type() {
		return dependency_type;
	}
	
	public void setDependency_type(Dependency_type dependency_type) {
		this.dependency_type = dependency_type;
	}
	
	public float getDependency_score() {
		return dependency_score;
	}
	
	public void setDependency_score(float dependency_score) {
		this.dependency_score = dependency_score;
	}
	
	public Dependency_status getStatus() {
		return status;
	}
	
	public void setStatus(Dependency_status status) {
		this.status = status;
	}
	
	public Requirement getFrom() {
		return from;
	}
	
	public void setFrom(Requirement from) {
		this.from = from;
	}
	
	public Requirement getTo() {
		return to;
	}
	
	public void setTo(Requirement to) {
		this.to = to;
	}
	
	public int getCreated_at() {
		return created_at;
	}
	
	public void setCreated_at(int created_at) {
		this.created_at = created_at;
	}
}
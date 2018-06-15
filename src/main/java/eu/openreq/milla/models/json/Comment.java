package eu.openreq.milla.models.json;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


/**
* Comment
* The comment that will be referenced from one entity
* 
*/
public class Comment {

	/**
	* The unique identifier of a comment
	* (Required)
	* 
	*/
	@SerializedName("id")
	@Expose
	private String id;
	/**
	* The textual description of the comment
	* (Required)
	* 
	*/
	@SerializedName("text")
	@Expose
	private String text;
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
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getText() {
		return text;
	}
	
	public void setText(String text) {
		this.text = text;
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
}
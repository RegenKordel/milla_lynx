package eu.openreq.milla.models.json;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


/**
* Classifier
* A keyword or tag that can help to classify requirements
* 
*/
public class Classifier {
	/**
	* The unique identifier of a classifier
	* (Required)
	* 
	*/
	@SerializedName("id")
	@Expose
	private String id;
	/**
	* The name of the classifier
	* (Required)
	* 
	*/
	@SerializedName("name")
	@Expose
	private String name;
	/**
	* The textual description of a classifier
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
}
package eu.openreq.milla.models.jira;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "id",
    "self",
    "label",
    "iconUrl",
    "avatarId",
    "sequence",
    "disalbled"
})

public class Platform {

	@JsonProperty("id")
	int id;
	@JsonProperty("self")
	String self;
	@JsonProperty("label")
	String label;
	@JsonProperty("iconUrl")
	String iconUrl;
	@JsonProperty("avatarId")
	int avatarId;
	@JsonProperty("sequence")
	int sequence;
	@JsonProperty("disabled")
	boolean disabled;
	
	@JsonProperty("id")
	public int getId() {
		return id;
	}
	
	@JsonProperty("id")
	public void setId(int id) {
		this.id = id;
	}
	
	@JsonProperty("self")
	public String getSelf() {
		return self;
	}
	
	@JsonProperty("self")
	public void setSelf(String self) {
		this.self = self;
	}
	
	@JsonProperty("label")
	public String getLabel() {
		return label;
	}
	
	@JsonProperty("label")
	public void setLabel(String label) {
		this.label = label;
	}
	
	@JsonProperty("iconUrl")
	public String getIconUrl() {
		return iconUrl;
	}
	
	@JsonProperty("iconUrl")
	public void setIconUrl(String iconUrl) {
		this.iconUrl = iconUrl;
	}
	
	@JsonProperty("avatarId")
	public int getAvatarId() {
		return avatarId;
	}
	
	@JsonProperty("avatarId")
	public void setAvatarId(int avatarId) {
		this.avatarId = avatarId;
	}
	
	@JsonProperty("sequence")
	public int getSequence() {
		return sequence;
	}
	
	@JsonProperty("sequence")
	public void setSequence(int sequence) {
		this.sequence = sequence;
	}
	
	@JsonProperty("disabled")
	public boolean isDisabled() {
		return disabled;
	}
	
	@JsonProperty("disabled")
	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}
}

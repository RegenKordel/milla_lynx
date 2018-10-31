package eu.openreq.milla.models.jira;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "self",
    "id",
    "description",
    "name",
    "archived",
    "released"
})

public class Version {

	@JsonProperty("self")
    private String self;
    @JsonProperty("id")
    private String id;
    @JsonProperty("name")
    private String name;
    @JsonProperty("description")
    private String description;
    @JsonProperty("archived")
    boolean archived;
    @JsonProperty("released")
    boolean released;
	
    @JsonProperty("self")
	public String getSelf() {
		return self;
	}

    @JsonProperty("self")
	public void setSelf(String self) {
		this.self = self;
	}

    @JsonProperty("id")
	public String getId() {
		return id;
	}
	
    @JsonProperty("id")
	public void setId(String id) {
		this.id = id;
	}
	
    @JsonProperty("description")
	public String getDescription() {
		return description;
	}
	
    @JsonProperty("description")
	public void setDescription(String description) {
		this.description = description;
	}
	
	@JsonProperty("name")
	public String getName() {
		return name;
	}
	
	@JsonProperty("name")
	public void setName(String name) {
		this.name = name;
	}
	
	@JsonProperty("archived")
	public boolean isArchived() {
		return archived;
	}
	
	@JsonProperty("archived")
	public void setArchived(boolean archived) {
		this.archived = archived;
	}
	
	@JsonProperty("released")
	public boolean isReleased() {
		return released;
	}
	
	@JsonProperty("released")
	public void setReleased(boolean released) {
		this.released = released;
	}
}

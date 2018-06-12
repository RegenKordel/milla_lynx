package eu.openreq.milla.models.jira;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "comments"
})
public class Comment {

    @JsonProperty("comments")
    private List<Comments> comments;
    @JsonIgnore
    private Map<String, Comments> additionalProperties = new HashMap<String, Comments>();

    @JsonProperty("comments")
    public List<Comments> getComments() {
    	
    	if (this.comments.size() != 0) {
    		System.out.println("Id: " + comments.get(0).getId() + " Body: " + comments.get(0).getBody());
    	} else {
    		System.out.println("First issue has no comments.");
    	}
    	return comments;
    }

    @JsonProperty("comments")
    public void setComments(List<Comments> comments) {
        this.comments = comments;
    }
    @JsonAnyGetter
    public Map<String, Comments> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Comments value) {
        this.additionalProperties.put(name, value);
    }

}

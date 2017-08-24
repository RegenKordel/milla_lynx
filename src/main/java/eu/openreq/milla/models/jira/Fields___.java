
package eu.openreq.milla.models.jira;

import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "summary",
    "status",
    "priority",
    "issuetype"
})
public class Fields___ {

    @JsonProperty("summary")
    private String summary;
    @JsonProperty("status")
    private Status___ status;
    @JsonProperty("priority")
    private Priority___ priority;
    @JsonProperty("issuetype")
    private Issuetype___ issuetype;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("summary")
    public String getSummary() {
        return summary;
    }

    @JsonProperty("summary")
    public void setSummary(String summary) {
        this.summary = summary;
    }

    @JsonProperty("status")
    public Status___ getStatus() {
        return status;
    }

    @JsonProperty("status")
    public void setStatus(Status___ status) {
        this.status = status;
    }

    @JsonProperty("priority")
    public Priority___ getPriority() {
        return priority;
    }

    @JsonProperty("priority")
    public void setPriority(Priority___ priority) {
        this.priority = priority;
    }

    @JsonProperty("issuetype")
    public Issuetype___ getIssuetype() {
        return issuetype;
    }

    @JsonProperty("issuetype")
    public void setIssuetype(Issuetype___ issuetype) {
        this.issuetype = issuetype;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}

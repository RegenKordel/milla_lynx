package eu.openreq.milla.models.jira;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
	"versions"
})

public class Versions {

	@JsonProperty("versions")
	Version[] versions;

	@JsonProperty("versions")
	public Version[] getVersions() {
		return versions;
	}

	@JsonProperty("versions")
	public void setVersions(Version[] versions) {
		this.versions = versions;
	}
}

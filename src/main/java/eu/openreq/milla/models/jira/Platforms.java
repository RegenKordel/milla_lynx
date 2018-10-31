package eu.openreq.milla.models.jira;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
	"platforms"
})

public class Platforms {

	@JsonProperty("platforms")
	Platform[] platforms;

	@JsonProperty("platforms")
	public Platform[] getplatforms() {
		return platforms;
	}

	@JsonProperty("platforms")
	public void setplatforms(Platform[] platforms) {
		this.platforms = platforms;
	}
}

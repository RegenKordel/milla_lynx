package eu.openreq.milla.models.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.OneToOne;

import org.springframework.data.jpa.domain.AbstractPersistable;

import com.google.gson.JsonElement;

import eu.openreq.milla.models.jira.Issue;


@Entity
public class IssueObject extends AbstractPersistable<Long>{

	private String key;
	
	private String issueId;
	
	@Lob
	@Column(name = "content")
	private String content;
	
	//timestamp when updated in Jira (Fieldsin updated?)	
	private String updated;
	
	//timestamp when added to the H2 databaseen (LocalDateTime?)
	private LocalDateTime timestamp;
	
	//component

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getIssueId() {
		return issueId;
	}

	public void setIssueId(String issueId) {
		this.issueId = issueId;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getUpdated() {
		return updated;
	}

	public void setUpdated(String updated) {
		this.updated = updated;
	}

	public LocalDateTime getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(LocalDateTime timestamp) {
		this.timestamp = timestamp;
	}
	
}

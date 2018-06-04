package eu.openreq.milla.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import eu.openreq.milla.models.entity.IssueObject;
import eu.openreq.milla.models.jira.Issue;

public interface IssueRepository extends JpaRepository<IssueObject, Long>{
	
	IssueObject findByKey(String key);	
	IssueObject findByIssueId(String issueId);

}

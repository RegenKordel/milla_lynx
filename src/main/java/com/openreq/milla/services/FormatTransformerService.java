package com.openreq.milla.services;

import java.util.ArrayList;
import java.util.List;

import com.openreq.milla.models.jira.Issue;
import com.openreq.milla.models.jira.Issuelink;
import com.openreq.milla.models.jira.Jira;
import com.openreq.milla.models.mulson.Attribute;
import com.openreq.milla.models.mulson.Relationship;
import com.openreq.milla.models.mulson.Requirement;

public class FormatTransformerService {

	/**
	 * 
	 * @param jira Class files made with http://www.jsonschema2pojo.org/
	 * @return
	 */
	public List<Requirement> convertJiraToMulson(Jira jira) {
		ArrayList<Requirement> requirements = new ArrayList<>();
		
		for(Issue issue : jira.getIssues()) {
			Requirement req = new Requirement();
			requirements.add(req);
			req.setRequirementId(issue.getKey().replace("-", "_")); //Kumbang doesn't like hyphens
			req.setName(issue.getFields().getSummary());
			
			addAttribute(req, "priority", issue.getFields().getPriority().getId());
			addAttribute(req, "status", issue.getFields().getStatus().getName());
			
			//Required-relationships
			for(Issuelink link : issue.getFields().getIssuelinks()) {
				if(!"depends on".equals(link.getType().getOutward())) {
					continue;
				}
				if(link.getOutwardIssue() == null || link.getOutwardIssue().getKey() == null) {
					continue;
				}
				Relationship rel = new Relationship();
				req.getRelationships().add(rel);
				rel.setTargetId(link.getOutwardIssue().getKey().replace("-", "_"));
				rel.setType("requires");
			}
			
			//TODO: subfeatures
		}
		
		return requirements;
	}

	private void addAttribute(Requirement req, String name, String value) {
		try {
			Attribute priority = new Attribute();
			priority.setName(name);
			ArrayList<String> priorities = new ArrayList<>();
			priorities.add(value);
			priority.setValues(priorities);
			req.getAttributes().add(priority);
		} catch (Exception e) {
			System.out.println("No " + name);
		}
	}
}

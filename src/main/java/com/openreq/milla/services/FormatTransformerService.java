package com.openreq.milla.services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import com.openreq.milla.models.jira.Issue;
import com.openreq.milla.models.jira.Issuelink;
import com.openreq.milla.models.jira.Jira;
import com.openreq.milla.models.jira.Subtask;
import com.openreq.milla.models.mulson.Attribute;
import com.openreq.milla.models.mulson.Relationship;
import com.openreq.milla.models.mulson.Requirement;
import com.openreq.milla.models.mulson.SubFeature;

public class FormatTransformerService {

	/**
	 * 
	 * @param jiras Class files made with http://www.jsonschema2pojo.org/
	 * @return
	 */
	public Collection<Requirement> convertJirasToMulson(List<Jira> jiras) {
		HashMap<String, Requirement> requirements = new HashMap<>();
		
		for(Jira jira : jiras) {
			for(Issue issue : jira.getIssues()) {
				Requirement req = new Requirement();
				req.setRequirementId(issue.getKey().replace("-", "_")); //Kumbang doesn't like hyphens
				req.setName(issue.getFields().getSummary());
				requirements.put(req.getRequirementId(), req);
				
				addAttribute(req, "priority", issue.getFields().getPriority().getId());
				addAttribute(req, "status", issue.getFields().getStatus().getName());
				
				addRequiredRelationships(issue, req);
				
				updateParentEpic(requirements, issue, req);
				
				List<Subtask> subtasks = issue.getFields().getSubtasks();
				if(subtasks != null && !subtasks.isEmpty()) {
					for(Subtask subtask : subtasks) {
						addSubtask(requirements, req, subtask);
					}
				}
			}
		}
		return requirements.values();
	}

	private void addSubtask(HashMap<String, Requirement> requirements, Requirement req, Subtask subtask) {
		Requirement req2 = new Requirement();
		req2.setRequirementId(subtask.getKey().replace("-", "_"));
		req2.setName(subtask.getFields().getSummary());
		requirements.put(req2.getRequirementId(), req2);
		
		addAttribute(req2, "priority", subtask.getFields().getPriority().getId());
		addAttribute(req2, "status", subtask.getFields().getStatus().getName());
		
		SubFeature subfeat = new SubFeature();
		ArrayList<String> types = new ArrayList<>();
		types.add(req2.getRequirementId());
		subfeat.setTypes(types);
		subfeat.setRole(req2.getRequirementId());
		subfeat.setCardinality("0-1");
		
		req.getSubfeatures().add(subfeat);
		
		//No issue links (requirements)?
	}

	private void updateParentEpic(HashMap<String, Requirement> requirements, Issue issue, Requirement req) {
		Object epicKeyObject = issue.getFields().getCustomfield10400();
		if(epicKeyObject == null) {
			return; //No parent
		}
		String epicKey = epicKeyObject.toString().replace("-", "_");
		
		Requirement epic = requirements.get(epicKey);
		
		if(epic == null) { //Parent not yet created
			epic = new Requirement();
			epic.setRequirementId(epicKey);
			epic.setName("Epic " + epicKey);
			requirements.put(epicKey, epic);
		}
		
		SubFeature subfeat = new SubFeature();
		ArrayList<String> types = new ArrayList<>();
		types.add(req.getRequirementId());
		subfeat.setTypes(types);
		subfeat.setRole(req.getRequirementId());
		subfeat.setCardinality("0-1");
		
		epic.getSubfeatures().add(subfeat);
	}

	private void addRequiredRelationships(Issue issue, Requirement req) {
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
	}

	private void addAttribute(Requirement req, String name, String value) {
		try {
			Attribute priority = new Attribute();
			priority.setName(name);
			ArrayList<String> priorities = new ArrayList<>();
			priorities.add(value.replace(" ", "_")); //Kumbang doesn't like spaces, either
			priority.setValues(priorities);
			req.getAttributes().add(priority);
		} catch (Exception e) {
			System.out.println("No " + name);
		}
	}
}

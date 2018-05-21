package eu.openreq.milla.services;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

import eu.openreq.milla.models.entity.IssueObject;
import eu.openreq.milla.models.jira.Fields;
import eu.openreq.milla.models.jira.Issue;
import eu.openreq.milla.models.jira.Issuelink;
import eu.openreq.milla.models.jira.Jira;
import eu.openreq.milla.models.jira.Priority;
import eu.openreq.milla.models.jira.Status;
import eu.openreq.milla.models.jira.Status__;
import eu.openreq.milla.models.jira.Subtask;
import eu.openreq.milla.models.mulson.Attribute;
import eu.openreq.milla.models.mulson.Relationship;
import eu.openreq.milla.models.mulson.Requirement;
import eu.openreq.milla.models.mulson.SubFeature;
import eu.openreq.milla.repositories.IssueRepository;

/**
 * Methods used to convert between formats
 * 
 * @author iivorait
 * @author tlaurinen
 */
@Service
public class FormatTransformerService {
	
	@Autowired
	private IssueRepository issueRepository;

	/**
	 * 
	 * @param jiras
	 *            Class files made with http://www.jsonschema2pojo.org/
	 * @return
	 */
	public Collection<Requirement> convertJirasToMulson(Collection<Jira> jiras) {
		HashMap<String, Requirement> requirements = new HashMap<>();

		for (Jira jira : jiras) {
			for (Issue issue : jira.getIssues()) {
				Requirement req = new Requirement();
				req.setRequirementId(issue.getKey().replace("-", "_")); // Kumbang doesn't like hyphens
				req.setName(issue.getFields().getSummary());
				requirements.put(req.getRequirementId(), req);

				addAttribute(req, "priority", issue.getFields().getPriority().getId());
				addAttribute(req, "status", issue.getFields().getStatus().getName());

				addRequiredRelationships(issue, req);

				updateParentEpic(requirements, issue, req);

				List<Subtask> subtasks = issue.getFields().getSubtasks();
				if (subtasks != null && !subtasks.isEmpty()) {
					for (Subtask subtask : subtasks) {
						addSubtask(requirements, req, subtask);
					}
				}
			}
		}
		return requirements.values();
	}

	/**
	 * Converts JsonElement objects to Issue Objects and adds "mock" issues to the
	 * Issue list to replace the issues that are linked to the project's issues, but
	 * are not in the same project
	 * 
	 * @param jsonElements
	 *            a collection of JsonElement objects
	 * @return a List of Issue objects
	 * @throws IOException
	 */
	public List<Issue> convertJsonElementsToIssues(Collection<JsonElement> jsonElements, String projectId)
			throws IOException {
		List<Issue> issues = new ArrayList<>();
		Gson gson = new Gson();

		// Printing all issues to a file for testing
//		 String fileName = "" + projectId + "_issues.txt"; // File name and path must be added if a log file of the issues is needed
//		 FileWriter fileWriter = new FileWriter(fileName);
//		 PrintWriter printWriter = new PrintWriter(fileWriter);
//		 String newLine = System.getProperty("line.separator");
		Set<String> allIssueKeys = new HashSet<>();
		Set<String> linkedProjectIssueKeys = new HashSet<>();

		for (JsonElement element : jsonElements) {
			Issue issue = gson.fromJson(element, Issue.class);
			issues.add(issue);
			allIssueKeys.add(issue.getKey());
			
			//Create a new IssueObject based on the issue and JsonElement and save
			if(issueRepository.findByKey(issue.getKey())==null) {
				IssueObject issueObject = new IssueObject();
				issueObject.setKey(issue.getKey());
				issueObject.setIssueId(issue.getId());
				issueObject.setContent(element.toString());
				issueRepository.save(issueObject);
			}
			// The following lines are here for getting all linked issues to their own sets
			// and for printing all issues to a file
			if (issue.getFields() != null) {
				if (!issue.getFields().getIssuelinks().isEmpty()) {
//					 printWriter.print(issue.getKey() + "\t" + "issue links are" + "\t");
					for (int i = 0; i < issue.getFields().getIssuelinks().size(); i++) {
						if (issue.getFields().getIssuelinks().get(i).getInwardIssue() != null) {
							String inward = issue.getFields().getIssuelinks().get(i).getInwardIssue().getKey();
							linkedProjectIssueKeys.add(inward);

//							 printWriter.print("inward issue" + "\t"
//							 + issue.getFields().getIssuelinks().get(i).getInwardIssue().getKey() + "\t"
//							 + "issueLink type" + "\t"
//							 + issue.getFields().getIssuelinks().get(i).getType().getName() + "\t");
						}
						if (issue.getFields().getIssuelinks().get(i).getOutwardIssue() != null) {
							String outward = issue.getFields().getIssuelinks().get(i).getOutwardIssue().getKey();
							linkedProjectIssueKeys.add(outward);
							
//							 printWriter.print("outward issue" + "\t"
//							 + issue.getFields().getIssuelinks().get(i).getOutwardIssue().getKey() + "\t"
//							 + "issueLink type" + "\t"
//							 + issue.getFields().getIssuelinks().get(i).getType().getName() + "\t");
						}
					}
//					 printWriter.print(newLine);
				}
//				 else {
//				 printWriter.print(issue.getKey() + "\t" + "no issue links" + newLine);
//				 }
			}
		}
		int i = 1;
		linkedProjectIssueKeys.removeAll(allIssueKeys); // This leaves to the set of linked issues only those issues
														// that are in a different project
		for (String key : linkedProjectIssueKeys) {
			Issue otherIssue = createMockIssue(key, i);
			issues.add(otherIssue);
			i++;
		}

//		 printWriter.close();

		return issues;
	}

	/**
	 * Converts a List of Issue objects into Mulson Requirements
	 * 
	 * @param issues
	 *            List of Issue objects
	 * @return a collection of Requirement objects
	 */
	public Collection<Requirement> convertIssuesToMulson(List<Issue> issues, String projectId) throws Exception {
		HashMap<String, Requirement> requirements = new HashMap<>();
		for (Issue issue : issues) {
			try {
				Requirement req = new Requirement();
				req.setRequirementId(issue.getKey().replace("-", "_")); // Kumbang doesn't like hyphens
				String name = issue.getFields().getSummary();
				String fixedName = name.replaceAll("[^\\x20-\\x7e]", ""); // TODO This is a quick fix, must be modified
																			// into a better version
				req.setName(fixedName);
				requirements.put(req.getRequirementId(), req);

				addAttribute(req, "priority", issue.getFields().getPriority().getId());
				addAttribute(req, "status", issue.getFields().getStatus().getName());

				addRequiredRelationships(issue, req);

				updateParentEpic(requirements, issue, req);

				List<Subtask> subtasks = issue.getFields().getSubtasks();
				if (subtasks != null && !subtasks.isEmpty()) {
					for (Subtask subtask : subtasks) {
						addSubtask(requirements, req, subtask);
					}
				}
			} catch (Exception e) {
				System.out.println("Error " + e);
				// e.printStackTrace();
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

		// No issue links (requirements)?
	}

	private void updateParentEpic(HashMap<String, Requirement> requirements, Issue issue, Requirement req) {
		Object epicKeyObject = issue.getFields().getCustomfield10400();
		if (epicKeyObject == null) {
			return; // No parent
		}
		String epicKey = epicKeyObject.toString().replace("-", "_");

		Requirement epic = requirements.get(epicKey);

		if (epic == null) { // Parent not yet created
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
		if (issue.getFields().getIssuelinks() != null) {
			for (Issuelink link : issue.getFields().getIssuelinks()) {
				if (!"depends on".equals(link.getType().getOutward())) {
					continue;
				}
				if (link.getOutwardIssue() == null || link.getOutwardIssue().getKey() == null) {
					continue;
				}
				Relationship rel = new Relationship();
				req.getRelationships().add(rel);
				rel.setTargetId(link.getOutwardIssue().getKey().replace("-", "_"));
				rel.setType("requires");
			}
		}
	}

	private void addAttribute(Requirement req, String name, String value) {
		try {
			Attribute priority = new Attribute();
			priority.setName(name);
			ArrayList<String> priorities = new ArrayList<>();
			priorities.add(value.replace(" ", "_")); // Kumbang doesn't like spaces, either
			priority.setValues(priorities);
			req.getAttributes().add(priority);
		} catch (Exception e) {
			System.out.println("No " + name);
		}
	}

	/**
	 * Method for creating a "mock" Issue of Issues that are mentioned in a
	 * project's Issues' issueLinks, but do not belong to the same project.
	 * 
	 * @param key
	 *            of the Issue associated with the project's issues, a real
	 *            key/identifier
	 * @param i
	 *            an index used for creating mock identifiers
	 * @return Issue with made-up fields etc
	 */
	private Issue createMockIssue(String key, int i) {
		Issue otherIssue = new Issue();
		otherIssue.setKey(key);
		otherIssue.setExpand("");
		Fields fields = new Fields();
		Priority priority = new Priority();
		priority.setId("" + i);
		Status__ status = new Status__();
		status.setName("mockstatus");
		fields.setSummary("A mock issue");
		fields.setPriority(priority);
		fields.setStatus(status);
		fields.setSubtasks(null);
		otherIssue.setFields(fields);
		otherIssue.setId("" + i + 1);
		otherIssue.setSelf("");

		return otherIssue;
	}
}

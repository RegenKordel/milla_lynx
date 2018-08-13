package eu.openreq.milla.services;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import eu.openreq.milla.models.jira.Comments;
import eu.openreq.milla.models.jira.Issue;
import eu.openreq.milla.models.jira.Component;
import eu.openreq.milla.models.jira.Issuelink;
import eu.openreq.milla.models.jira.Subtask;
import eu.openreq.milla.models.json.Classifier;
import eu.openreq.milla.models.json.Comment;
import eu.openreq.milla.models.json.Dependency;
import eu.openreq.milla.models.json.Dependency_status;
import eu.openreq.milla.models.json.Dependency_type;
import eu.openreq.milla.models.json.Project;
import eu.openreq.milla.models.json.Requirement;
import eu.openreq.milla.models.json.RequirementPart;
import eu.openreq.milla.models.json.Requirement_status;
import eu.openreq.milla.models.json.Requirement_type;

/**
 * Methods used to convert between formats
 * 
 * @author iivorait
 * @author tlaurinen
 * @author ekettu
 */
@Service
public class FormatTransformerService {

	/**
	 * List of Dependencies between Requirements
	 */
	private List<Dependency> dependencies;

	/**
	 * List of Requirement IDs that are related to a Project
	 */
	private List<String> requirementIds;

	private int epicCount;

	public int getEpicCount() {
		return epicCount;
	}

	private int subtaskCount;

	public int getSubtaskCount() {
		return subtaskCount;
	}

	/**
	 * Converts JsonElements to Jira Issues
	 * 
	 * @param jsonElements
	 *            a collection of JsonElement objects
	 * @return a List of Issue objects
	 * @throws IOException
	 */
	public List<Issue> convertJsonElementsToIssues(List<JsonElement> jsonElements, String projectId)
			throws IOException {

		long start = System.nanoTime();

		Gson gson = new Gson();
		List<Issue> issues = new ArrayList<>();

		for (int i = 0; i < jsonElements.size(); i++) {
			JsonElement element = jsonElements.get(i);

			JsonObject issueJSON = element.getAsJsonObject();
			Issue issue = gson.fromJson(issueJSON, Issue.class);
			issue.getFields().setCustomfield10400(issueJSON.getAsJsonObject("fields").get("customfield_10400"));
			issues.add(issue);
			element = null;
			issue = null;
		}
		long end = System.nanoTime();
		long durationSec = (end - start) / 1000000000;
		double durationMin = durationSec / 60.0;
		System.out.println("Lists done, it took " + durationSec + " second(s) or " + durationMin + " minute(s).");

		return issues;
	}

	/**
	 * Converts a List of Jira Issues into OpenReq Json Requirements, and creates a
	 * List of Requirement Ids (as Strings) that will be given to a Project.
	 * Requirements do not know their Project, but the Project knows the Ids of its
	 * Requirements
	 * 
	 * @param issues
	 *            List of Jira Issues
	 * @return a collection of Requirement objects
	 */
	public Collection<Requirement> convertIssuesToJson(List<Issue> issues, String projectId) throws Exception {
		dependencies = new ArrayList<>();
		HashMap<String, Requirement> requirements = new HashMap<>();

		epicCount = 0;
		subtaskCount = 0;

		requirementIds = new ArrayList<>();

		for (Issue issue : issues) {
			try {
				Requirement req = new Requirement();
				req.setId(issue.getKey()); // Murmeli doesn't mind hyphens, hopefully?
				String name = fixSpecialCharacters(issue.getFields().getSummary());
				req.setName(name);
				String text = fixSpecialCharacters(issue.getFields().getDescription());
				if (text != null && !text.equals("")) {
					req.setText(text);
				}
				requirements.put(req.getId(), req);
				requirementIds.add(req.getId());
				int priority = Integer.parseInt(issue.getFields().getPriority().getId()); // Note! This might not be
																							// actually a good idea, QT
																							// priorities not numerical,
																							// but might still work
				req.setPriority(priority);

				setStatusForReq(req, issue.getFields().getStatus().getName());
				setRequirementType(req, issue.getFields().getIssuetype().getName());

				req.setCreated_at(setCreatedDate(issue.getFields().getCreated()));
				req.setModified_at(setCreatedDate(issue.getFields().getUpdated()));

				addCommentsToReq(issue, req);
				addDependencies(issue, req);
				addClassifiers(issue, req);
				addResolutionToRequirementParts(issue, req);

				updateParentEpic(requirements, issue, req);

				List<Subtask> subtasks = issue.getFields().getSubtasks();
				if (subtasks != null && !subtasks.isEmpty()) {
					for (Subtask subtask : subtasks) {
						addSubtask(req, subtask);
					}
				}
			} catch (Exception e) {
				// System.out.println("Error in requirement creation: " + e);
				e.printStackTrace();
			}
		}

		return requirements.values();
	}

	/**
	 * Method for removing special characters from a String, a quick and dirty
	 * version to avoid UTF-8 errors.
	 * 
	 * @param name
	 *            that needs to have special characters removed
	 * @return a fixed version of the name
	 */
	private String fixSpecialCharacters(String name) { //TODO this might not be necessary anymore but is left here for demo safety 
		String fixedName = name;
		if (name != null && !name.equals("")) {
			fixedName = name.replaceAll("[^\\x20-\\x7e]", ""); // TODO This is a quick fix, must be modified into a
		} // better version
		return fixedName;
	}

	/**
	 * Creates a list of Comments for a Requirement based on the Comments of a Jira
	 * Issue
	 * 
	 * @param issue
	 *            Issue that has Comments
	 * @param req
	 *            Requirement receiving the Comment
	 */
	private void addCommentsToReq(Issue issue, Requirement req) {
		if (!issue.getFields().getComment().getComments().isEmpty()) {
			for (Comments comment : issue.getFields().getComment().getComments()) {
				Comment jsonComment = new Comment();
				jsonComment.setId(comment.getId());
				jsonComment.setText(comment.getBody());

				String date = String.valueOf(comment.getCreated());
				long created = setCreatedDate(date);
				jsonComment.setCreated_at(created);
				req.getComments().add(jsonComment);
			}
		}
	}

	/**
	 * Method for parsing date data that is received as a String from a Jira Issue
	 * 
	 * @param created
	 *            date and time data as a String
	 * @return the date and time as a Long milliseconds
	 */
	private long setCreatedDate(String created) {
		String created2 = splitString(created);
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");

		long milliseconds;
		try {
			Date date = format.parse(created2);
			milliseconds = date.getTime();
		} catch (ParseException e) {
			System.out.println("Error in parsing the date: " + e);
			return -1;
		}
		return milliseconds;
	}

	private String splitString(String word) {
		String[] parts = word.split("\\+");
		String part = parts[0];
		return part;
	}

	/**
	 * Creates OpenReq Json Dependency objects based on Jira IssueLinks and Json
	 * Requirements. Only Outward Issues are considered, since the IssueLinks go
	 * both ways.
	 * 
	 * @param issue
	 *            Issue that has IssueLinks
	 * @param req
	 */
	private void addDependencies(Issue issue, Requirement req) {
		if (issue.getFields().getIssuelinks() != null && !issue.getFields().getIssuelinks().isEmpty()) {
			for (Issuelink link : issue.getFields().getIssuelinks()) {
				// if (!"depends on".equals(link.getType().getOutward())) { //Is this necessary?
				// continue;
				// }
//				if (link.getOutwardIssue() == null || link.getOutwardIssue().getKey() == null) {
//					continue;
//				}
				if (link.getOutwardIssue() != null && link.getOutwardIssue().getKey() != null) {
					createDependency(req.getId(), link.getOutwardIssue().getKey(), link.getType().getName());
				}
				if (link.getInwardIssue() != null && link.getInwardIssue().getKey() != null) {
					createDependency(link.getInwardIssue().getKey(), req.getId(), link.getType().getName());
				}
			//	createDependency(req.getId(), link.getOutwardIssue().getKey(), link.getType().getName());
			}
		}
	}

	/**
	 * Create new Dependency and save it to a List of Dependencies
	 * 
	 * @param reqFrom
	 *            String id of the Requirement from
	 * @param reqTo
	 *            String id of the Requirement to
	 * @param type
	 *            String type of the Dependency
	 */
	private void createDependency(String reqFrom, String reqTo, String type) {
		Dependency dependency = new Dependency();
		dependency.setFromId(reqFrom);
		dependency.setToId(reqTo);
		setDependencyType(dependency, type);
		dependency.setId(reqFrom + "_" + reqTo + "_" + dependency.getDependency_type());
		setStatusForDependency(dependency, "accepted");
		dependency.setCreated_at(new Date().getTime());
		dependencies.add(dependency);
	}

	/**
	 * Adds children ("subRequirements") to a Requirement, Subtasks are received
	 * from an Issue, and converted into Requirements.
	 * 
	 * @param requirements
	 * @param req
	 * @param subtask
	 */
	private void addSubtask(Requirement req, Subtask subtask) {
		createDependency(req.getId(), subtask.getKey(), "subtask");
	}

	/**
	 * Jira Issues know their parents (Epics)
	 * (issue.getFields().getCustomfield10400()), and this method creates a Dendency
	 * between an Epic and its "child" (Dependency_type Decomposition).
	 * 
	 * @param requirements
	 * @param issue
	 * @param req
	 */
	private void updateParentEpic(HashMap<String, Requirement> requirements, Issue issue, Requirement req) {
		Object epicKeyObject = issue.getFields().getCustomfield10400();
		if (epicKeyObject.toString().equals("null")) {
			return; // No parent
		}
		String epicKey = cleanEpicKey(epicKeyObject.toString());
		createDependency(epicKey, req.getId(), "epic");

	}
	
	/**
	 * Helper method for cleaning "" marks from issue keys that belong to epics (no idea why epic issues seem to have extra "" around them)
	 * @param epicKey
	 * @return
	 */
	private String cleanEpicKey(String epicKey) {
		char [] chars = epicKey.toCharArray();
		String newEpicKey = "";
			for(int i = 1; i < chars.length-1; i++) {
				newEpicKey = newEpicKey + chars[i];
			}
		return newEpicKey;
	}

	/**
	 * Assigns a Requirement_type to a Requirement, type received as a String from a
	 * Jira Issue
	 * 
	 * @param req
	 *            Requirement needing a Requirement_type
	 * @param type
	 *            String received from an Issue
	 */
	private void setRequirementType(Requirement req, String type) {
		switch (type.toLowerCase()) {
		case "bug":
			req.setRequirement_type(Requirement_type.BUG);
			break;
		case "epic":
			req.setRequirement_type(Requirement_type.EPIC);
			break;
		case "initiative":
			req.setRequirement_type(Requirement_type.INITIATIVE);
			break;
		case "sub-task":
			req.setRequirement_type(Requirement_type.TASK);
			break;
		case "suggestion":
			req.setRequirement_type(Requirement_type.ISSUE);
			break;
		case "task":
			req.setRequirement_type(Requirement_type.TASK);
			break;
		case "technical task":
			req.setRequirement_type(Requirement_type.TASK);
			break;
		case "change request":
			req.setRequirement_type(Requirement_type.NON_FUNCTIONAL);
			break;
		case "user story":
			req.setRequirement_type(Requirement_type.USER_STORY);
			break;
		}
	}

	/**
	 * Assigns status to a Requirement according to the status (String) received
	 * from a Jira Issue
	 * 
	 * @param req
	 *            Requirement needing a status
	 * @param status
	 *            String value received from a Jira Issue
	 */
	private void setStatusForReq(Requirement req, String status) {

		switch (status.toLowerCase()) {
		case "reported":
			req.setStatus(Requirement_status.SUBMITTED);
			break;
		case "need more info":
			req.setStatus(Requirement_status.DEFERRED);
			break;
		case "open":
			req.setStatus(Requirement_status.PENDING);
			break;
		case "in progress":
			req.setStatus(Requirement_status.NEW); // ?
			break;
		case "withdrawn":
			req.setStatus(Requirement_status.REJECTED);
			break;
		case "implemented":
			req.setStatus(Requirement_status.DRAFT);
			break;
		case "verified":
			req.setStatus(Requirement_status.ACCEPTED);
			break;
		case "closed":
			req.setStatus(Requirement_status.COMPLETED);
			break;
		case "resolved":
			req.setStatus(Requirement_status.COMPLETED);
			break;
		case "reopened":
			req.setStatus(Requirement_status.PENDING);
			break;
			
			//Should have more types of statuses included?
		}
	}

	/**
	 * Assigns Dependency_type to a Dependency according to the type (String)
	 * received from a Jira IssueLink
	 * 
	 * @param dependency
	 *            Dependency needing a Dependency_type
	 * @param type
	 *            String received from an IssueLink
	 */
	private void setDependencyType(Dependency dependency, String type) {

		switch (type.toLowerCase()) {
		case "dependency":
			dependency.setDependency_type(Dependency_type.REQUIRES);
			break;
		case "relates":
			dependency.setDependency_type(Dependency_type.SIMILAR);
			break;
		case "duplicate":
			dependency.setDependency_type(Dependency_type.DUPLICATES);
			break;
		case "replacement":
			dependency.setDependency_type(Dependency_type.REPLACES);
			break;
		case "work breakdown":
			dependency.setDependency_type(Dependency_type.REFINES);
			break;
		case "test":
			dependency.setDependency_type(Dependency_type.REFINES);
			break;
		case "subtask":
			dependency.setDependency_type(Dependency_type.DECOMPOSITION);
			subtaskCount++;
			break;
		case "epic":
			dependency.setDependency_type(Dependency_type.DECOMPOSITION);
			epicCount++;
			break;
		}
	}

	/**
	 * Assigns Dependency_status to a Dependency
	 * 
	 * @param dependency
	 *            Dependency needing a Dependency_status
	 * @param status
	 */
	private void setStatusForDependency(Dependency dependency, String status) {

		switch (status.toLowerCase()) {
		case "accepted":
			dependency.setStatus(Dependency_status.ACCEPTED);
			break;
		case "proposed":
			dependency.setStatus(Dependency_status.PROPOSED);
			break;
		case "rejected":
			dependency.setStatus(Dependency_status.REJECTED);
			break;
		}
	}

	/**
	 * Assigns a Classifier (Component) to a Requirement based on the Component of
	 * an Issue
	 * 
	 * @param issue
	 *            Issue that has (a) Component(s)
	 * @param req
	 *            Requirement that needs Classifiers
	 */
	private void addClassifiers(Issue issue, Requirement req) {
		if (!issue.getFields().getComponents().isEmpty()) {
			for (Component component : issue.getFields().getComponents()) {
				Classifier classifier = new Classifier();
				classifier.setId(component.getId());
				classifier.setName(component.getName());
				classifier.setCreated_at(new Date().getTime());
				req.getClassifierResults().add(classifier);
			}
		}
	}
	
	/**
	 * Add information on the resolution of an issue to a RequirementPart object. If issue's resolution is null, create a new RequirementPart with the text "Unresolved"
	 * @param issue
	 * @param req
	 */
	private void addResolutionToRequirementParts(Issue issue, Requirement req) {
		RequirementPart reqPart = new RequirementPart();
		reqPart.setId(req.getId()+"_RESOLUTION");
		reqPart.setName("Resolution");
		
		if(issue.getFields().getResolution()!=null) {
			reqPart.setText(issue.getFields().getResolution().getName());
			reqPart.setCreated_at(new Date().getTime()); //Here issue.getFields().getResolutionDate()? 
			
		}
		else {
			reqPart.setText("Unresolved");
			reqPart.setCreated_at(new Date().getTime());
		}
		req.getRequirementParts().add(reqPart);
	}

	/**
	 * Creates an OpenReq Json Project object
	 * 
	 * @param projectId
	 *            identifier of a Qt Jira project
	 * @param requirementIds
	 *            List of Requirement ids
	 * @return a new Project
	 */
	public Project createProject(String projectId, List<String> reqIds) {
		Project project = new Project();
		project.setId(projectId);
		project.setName(projectId);
		project.setCreated_at(new Date().getTime());
		project.setSpecifiedRequirements(reqIds);
		return project;
	}

	public List<Dependency> getDependencies() {
		return dependencies;
	}

	public List<String> getRequirementIds() {
		return requirementIds;
	}
	
	

	// Below old Mulson methods

	// /**
	// * Converts a List of Issue objects into Mulson Requirements
	// *
	// * @param issues
	// * List of Issue objects
	// * @return a collection of Requirement objects
	// */
	// public Collection<Requirement> convertIssuesToMulson(List<Issue> issues,
	// String projectId) throws Exception {
	// HashMap<String, Requirement> requirements = new HashMap<>();
	// for (Issue issue : issues) {
	// try {
	// Requirement req = new Requirement();
	// req.setRequirementId(issue.getKey().replace("-", "_")); // Kumbang doesn't
	// like hyphens
	// String name = fixSpecialCharacters(issue.getFields().getSummary());
	// req.setName(name);
	// requirements.put(req.getRequirementId(), req);
	//
	// addAttribute(req, "priority", issue.getFields().getPriority().getId());
	// addAttribute(req, "status", issue.getFields().getStatus().getName());
	//
	// addRequiredRelationships(issue, req);
	// updateParentEpic(requirements, issue, req);
	//
	// List<Subtask> subtasks = issue.getFields().getSubtasks();
	// if (subtasks != null && !subtasks.isEmpty()) {
	// for (Subtask subtask : subtasks) {
	// addSubtask(requirements, req, subtask);
	// }
	// }
	// } catch (Exception e) {
	// System.out.println("Error " + e);
	// // e.printStackTrace();
	// }
	// }
	// return requirements.values();
	// }

	// private void addAttribute(Requirement req, String name, String value) {
	// try {
	// Attribute priority = new Attribute();
	// priority.setName(name);
	// ArrayList<String> priorities = new ArrayList<>();
	// priorities.add(value.replace(" ", "_")); // Kumbang doesn't like spaces,
	// either
	// priority.setValues(priorities);
	// // req.getAttributes().add(priority);
	// } catch (Exception e) {
	// System.out.println("No " + name);
	// }
	// }

	// private void addSubtask(HashMap<String, Requirement> requirements,
	// Requirement req, Subtask subtask) {
	// Requirement req2 = new Requirement();
	// req2.setRequirementId(subtask.getKey().replace("-", "_"));
	// req2.setName(subtask.getFields().getSummary());
	// requirements.put(req2.getRequirementId(), req2);
	//
	// addAttribute(req2, "priority", subtask.getFields().getPriority().getId());
	// addAttribute(req2, "status", subtask.getFields().getStatus().getName());
	//
	// SubFeature subfeat = new SubFeature();
	// ArrayList<String> types = new ArrayList<>();
	// types.add(req2.getRequirementId());
	// subfeat.setTypes(types);
	// subfeat.setRole(req2.getRequirementId());
	// subfeat.setCardinality("0-1");
	//
	// req.getSubfeatures().add(subfeat);
	//
	// // No issue links (requirements)?
	// }

	// private void updateParentEpic(HashMap<String, Requirement> requirements,
	// Issue issue, Requirement req) {
	// Object epicKeyObject = issue.getFields().getCustomfield10400();
	// if (epicKeyObject == null) {
	// return; // No parent
	// }
	// String epicKey = epicKeyObject.toString().replace("-", "_");
	//
	// Requirement epic = requirements.get(epicKey);
	//
	// if (epic == null) { // Parent not yet created
	// epic = new Requirement();
	// epic.setRequirementId(epicKey);
	// epic.setName("Epic " + epicKey);
	// requirements.put(epicKey, epic);
	// }
	//
	// SubFeature subfeat = new SubFeature();
	// ArrayList<String> types = new ArrayList<>();
	// types.add(req.getRequirementId());
	// subfeat.setTypes(types);
	// subfeat.setRole(req.getRequirementId());
	// subfeat.setCardinality("0-1");
	//
	// epic.getSubfeatures().add(subfeat);
	// }
	//
	// private void addRequiredRelationships(Issue issue, Requirement req) {
	// if (issue.getFields().getIssuelinks() != null) {
	// for (Issuelink link : issue.getFields().getIssuelinks()) {
	// if (!"depends on".equals(link.getType().getOutward())) {
	// continue;
	// }
	// if (link.getOutwardIssue() == null || link.getOutwardIssue().getKey() ==
	// null) {
	// continue;
	// }
	// Relationship rel = new Relationship();
	// req.getRelationships().add(rel);
	// rel.setTargetId(link.getOutwardIssue().getKey().replace("-", "_"));
	// rel.setType("requires");
	// }
	// }
	// }
	//
	// private void addAttribute(Requirement req, String name, String value) {
	// try {
	// Attribute priority = new Attribute();
	// priority.setName(name);
	// ArrayList<String> priorities = new ArrayList<>();
	// priorities.add(value.replace(" ", "_")); // Kumbang doesn't like spaces,
	// either
	// priority.setValues(priorities);
	// req.getAttributes().add(priority);
	// } catch (Exception e) {
	// System.out.println("No " + name);
	// }
	// }
}

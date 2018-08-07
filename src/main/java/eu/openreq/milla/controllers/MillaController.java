package eu.openreq.milla.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import eu.openreq.milla.models.jira.Issue;
import eu.openreq.milla.models.json.Dependency;
import eu.openreq.milla.models.json.Project;
import eu.openreq.milla.models.json.Requirement;
import eu.openreq.milla.services.FormatTransformerService;
import eu.openreq.milla.services.MallikasService;
import eu.openreq.milla.qtjiraimporter.ProjectIssues;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@SpringBootApplication
@Controller
@RequestMapping(value = "/")
public class MillaController {

	@Value("${milla.mulperiAddress}")
	private String mulperiAddress;

	@Value("${milla.mallikasAddress}")
	private String mallikasAddress;
	
	@Value("${milla.upcAddress}")
	private String upcAddress;

	@Autowired
	FormatTransformerService transformer;

	@Autowired
	MallikasService mallikasService;

	/**
	 * Post Requirements and Dependencies to Mulperi.
	 * 
	 * @param data
	 * @param path
	 * @return
	 * @throws IOException
	 */
	@ApiOperation(value = "Relay POST to Mulperi (obsolete relay)", notes = "Post a model or configuration request to Mulperi")
	@ResponseBody
	@PostMapping(value = "data")
	public ResponseEntity<?> postToMulperi(@RequestBody String data)
			throws IOException {

		System.out.println("PostToMulperi");
		RestTemplate rt = new RestTemplate();

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		String actualPath = "models/requirementsToChoco"; 

		String completeAddress = mulperiAddress + actualPath;

		HttpEntity<String> entity = new HttpEntity<String>(data, headers);
		ResponseEntity<?> response = null;
		try {
			response = rt.postForEntity(completeAddress, entity, String.class);
		} catch (HttpClientErrorException e) {
			return new ResponseEntity<>("Mulperi error:\n\n" + e.getResponseBodyAsString(), e.getStatusCode());
		}

		return response;
	}
	
	
	/**
	 * Fetch Requirements that are in the selected Project from Mallikas, and send to Mulperi
	 * 
	 * @param
	 * @return ResponseEntity<?>
	 * @throws IOException
	 */
	@ApiOperation(value = "Send requirements of the selected project to Mulperi", notes = "Fetch all requirements in the same project from Mallikas database and send requirements and dependencies to Mulperi")
	@ResponseBody
	@PostMapping(value = "sendProjectToMulperi")
	public ResponseEntity<?> sendProjectToMulperi(@RequestBody String projectId) throws IOException {

		System.out.println("getRequirementsProject called");

		String completeAddress = mallikasAddress + "projectRequirements";

		String reqsInProject = mallikasService.getAllRequirementsInProjectFromMallikas(projectId,
				completeAddress);

		if (reqsInProject == null) {
			return new ResponseEntity<>("Requirements not found \n\n", HttpStatus.NOT_FOUND);
		}
		return this.postToMulperi(reqsInProject);
	}
	
//	/**
//	 * Post Requirements and Dependencies to Mulperi.
//	 * 
//	 * @param data
//	 * @param path
//	 * @return
//	 * @throws IOException
//	 */
//	@ApiOperation(value = "Relay POST to Mulperi (obsolete relay)", notes = "Post a model or configuration request to Mulperi")
//	@ResponseBody
//	@PostMapping(value = "relay/{path}")
//	public ResponseEntity<?> postToMulperi(@RequestBody String data, @PathVariable("path") String path)
//			throws IOException {
//
//		System.out.println("PostToMulperi");
//		RestTemplate rt = new RestTemplate();
//
//		HttpHeaders headers = new HttpHeaders();
//		headers.setContentType(MediaType.APPLICATION_JSON);
//
//		String actualPath = getActualPath(path);
//
//		String completeAddress = mulperiAddress + actualPath;
//
//		HttpEntity<String> entity = new HttpEntity<String>(data, headers);
//		System.out.println("Data is " + data);
//		ResponseEntity<?> response = null;
//
//		try {
//			response = rt.postForEntity(completeAddress, entity, String.class);
//		} catch (HttpClientErrorException e) {
//			return new ResponseEntity<>("Mulperi error:\n\n" + e.getResponseBodyAsString(), e.getStatusCode());
//		}
//
//		return response;
//	}
//
//	/**
//	 * Method for setting the actual path to Mulperi
//	 * 
//	 * @param path
//	 * @return
//	 */
//	public String getActualPath(String path) { // Changed to public for TestingContoller
//		if (path.equals("mulson"))
//			return "models/mulson";
//		if (path.equals("reqif"))
//			return "models/reqif";
//		if (path.contains("configure:")) {
//			String modelName = path.split(":", 2)[1];
//			return "models/" + modelName + "/configurations";
//		}
//		return path;
//	}

	/**
	 * Post a Collection of OpenReq JSON Requirements to Mallikas database
	 * 
	 * @param requirements
	 *            Collection<Requirement> received as a parameter, requirements to
	 *            be saved into the database
	 * @return ResponseEntity<?>
	 * @throws IOException
	 */
	@ApiOperation(value = "Post requirements to Mallikas", notes = "Post a list of requirements to Mallikas database")
	@ResponseBody
	@PostMapping(value = "requirements")
	public ResponseEntity<?> postRequirementsToMallikas(@RequestBody Collection<Requirement> requirements)
			throws IOException {

		RestTemplate rt = new RestTemplate();

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		String completeAddress = mallikasAddress + "updateRequirements";

		Collection<Requirement> issueList = requirements;
		ResponseEntity<?> response = null;

		try {
			response = rt.postForEntity(completeAddress, issueList, Collection.class);

		} catch (HttpClientErrorException e) {
			return new ResponseEntity<>("Mallikas error:\n\n" + e.getResponseBodyAsString(), e.getStatusCode());
		}
		return response;
	}

	/**
	 * Post a Collection of OpenReq JSON Dependencies to Mallikas database
	 * 
	 * @param dependencies
	 *            Collection<Dependency> received as a parameter, requirements to be
	 *            saved into the database
	 * @return ResponseEntity<?>
	 * @throws IOException
	 */
	@ApiOperation(value = "Post dependencies to Mallikas", notes = "Post a list of dependencies to Mallikas database")
	@ResponseBody
	@PostMapping(value = "dependencies")
	public ResponseEntity<?> postDependenciesToMallikas(@RequestBody Collection<Dependency> dependencies)
			throws IOException {

		RestTemplate rt = new RestTemplate();

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		System.out.println("postDependencies called");
		String completeAddress = mallikasAddress + "updateDependencies";

		Collection<Dependency> dependencyList = dependencies;
		ResponseEntity<?> response = null;

		try {
			response = rt.postForEntity(completeAddress, dependencyList, Collection.class);

		} catch (HttpClientErrorException e) {
			return new ResponseEntity<>("Mallikas error:\n\n" + e.getResponseBodyAsString(), e.getStatusCode());
		}
		return response;
	}

	/**
	 * Post an OpenReq JSON Project to Mallikas database
	 * 
	 * @param project
	 *            Project received as a parameter
	 * @return ResponseEntity<?>
	 * @throws IOException
	 */
	@ApiOperation(value = "Post project to Mallikas", notes = "Post a Project to Mallikas database")
	@ResponseBody
	@PostMapping(value = "project")
	public ResponseEntity<?> postProjectToMallikas(@RequestBody Project project) throws IOException {

		RestTemplate rt = new RestTemplate();

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		System.out.println("postProjectToMallikas called");
		String completeAddress = mallikasAddress + "project";

		ResponseEntity<?> response = null;

		try {
			response = rt.postForEntity(completeAddress, project, Project.class);

		} catch (HttpClientErrorException e) {
			return new ResponseEntity<>("Mallikas error:\n\n" + e.getResponseBodyAsString(), e.getStatusCode());
		}
		return response;
	}
	
	/**
	 * Post a Collection of updated (or new) OpenReq JSON Dependencies to Mallikas database
	 * 
	 * @param dependencies
	 *            Collection<Dependency> received as a parameter, dependencies to be
	 *            updated
	 * @return ResponseEntity<?>
	 * @throws IOException
	 */
	@ApiOperation(value = "Post updated dependencies to Mallikas", notes = "Post a collection of updated dependencies to Mallikas database")
	@ResponseBody
	@PostMapping(value = "updateDependencies")
	public ResponseEntity<?> postUpdatedDependenciesToMallikas(@RequestBody String dependencies)
			throws IOException {

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		System.out.println("postUpdatedDependencies called");
		String completeAddress = mallikasAddress + "updateDependencies";
		
		String updated = null;

		try {
			updated = mallikasService.updateSelectedDependencies(dependencies, completeAddress);

		} catch (HttpClientErrorException e) {
			return new ResponseEntity<>("Mallikas error:\n\n" + e.getResponseBodyAsString(), e.getStatusCode());
		}
		ResponseEntity<String> response = new ResponseEntity<>(updated, HttpStatus.OK);
		return response;
	}
	
	@ApiOperation(value = "Post updated requirements to Mallikas", notes = "Post a collection of updated requirements to Mallikas database")
	@ResponseBody
	@PostMapping(value = "updateRequirements")
	public ResponseEntity<?> postUpdatedRequirementsToMallikas(@RequestBody String requirements)
			throws IOException {
	
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		System.out.println("postUpdatedRequirements called");
		String completeAddress = mallikasAddress + "updateRequirements";

		String updated = null;

		try {
			updated = mallikasService.updateSelectedRequirements(requirements, completeAddress);

		} catch (HttpClientErrorException e) {
			return new ResponseEntity<>("Mallikas error:\n\n" + e.getResponseBodyAsString(), e.getStatusCode());
		}
		
		ResponseEntity<String> response = new ResponseEntity<>(updated, HttpStatus.OK);
		return response;
	}

	/**
	 * Fetch Requirements related to the selected Component (OpenReq Classifier)
	 * 
	 * @param
	 * @return ResponseEntity<?>
	 * @throws IOException
	 */
	@ApiOperation(value = "Fetch requirements in the same component", notes = "Fetch all requirements in the same component from Mallikas database")
	@ResponseBody
	@PostMapping(value = "requirementsInComponent")
	public ResponseEntity<?> getRequirementsInSameComponent(@RequestBody String componentId) throws IOException {

		System.out.println("getRequirementsInSameComponent called");

		String completeAddress = mallikasAddress + "classifiers";

		String reqsInComponent = mallikasService.getAllRequirementsWithClassifierFromMallikas(componentId,
				completeAddress);

		System.out.println("Reqs found " + reqsInComponent);

		if (reqsInComponent == null) {
			return new ResponseEntity<>("Requirements not found \n\n", HttpStatus.NOT_FOUND);
		}
		ResponseEntity<String> response = new ResponseEntity<>(reqsInComponent, HttpStatus.FOUND);
		return response;
	}
	
	/**
	 * Fetch Requirements that are in the selected Project
	 * 
	 * @param
	 * @return ResponseEntity<?>
	 * @throws IOException
	 */
	@ApiOperation(value = "Fetch requirements in the same project", notes = "Fetch all requirements in the same project from Mallikas database")
	@ResponseBody
	@PostMapping(value = "requirementsInProject")
	public ResponseEntity<?> getRequirementsInProject(@RequestBody String projectId) throws IOException {

		System.out.println("getRequirementsProject called");

		String completeAddress = mallikasAddress + "projectRequirements";

		String reqsInProject = mallikasService.getAllRequirementsInProjectFromMallikas(projectId,
				completeAddress);

		System.out.println("Reqs found " + reqsInProject);

		if (reqsInProject == null) {
			return new ResponseEntity<>("Requirements not found \n\n", HttpStatus.NOT_FOUND);
		}
		ResponseEntity<String> response = new ResponseEntity<>(reqsInProject, HttpStatus.FOUND);
		return response;
	}

	/**
	 * Fetch a List of selected Requirements from Mallikas
	 * 
	 * @param project
	 *            Project received as a parameter
	 * @return ResponseEntity<?>
	 * @throws IOException
	 */
	@ApiOperation(value = "Fetch selected requirements from the database", notes = "Fetch selected requirements (ids as a String list) from Mallikas database")
	@ResponseBody
	@PostMapping(value = "requirementsWithIds")
	public ResponseEntity<?> getRequirementsWithIds(@RequestBody Collection<String> ids) throws IOException {
		System.out.println("getRequirementsWithIds called");
		String completeAddress = mallikasAddress + "selectedReqs";

		String reqsInComponent = mallikasService.getSelectedRequirementsFromMallikas(ids, completeAddress);

		System.out.println("Reqs found " + reqsInComponent);

		if (reqsInComponent == null || reqsInComponent.equals("")) {
			return new ResponseEntity<>("Requirements not found \n\n", HttpStatus.NOT_FOUND);
		}
		ResponseEntity<String> response = new ResponseEntity<>(reqsInComponent, HttpStatus.FOUND);
		return response;
	}
	
//	/**
//	 * 
//	 * @param type
//	 * @return
//	 * @throws IOException
//	 */
//	@ApiOperation(value = "Fetch requirements that have the selected requirement type from database", notes = "Requirement type should be given in all caps, e.g. BUG ")
//	@ResponseBody
//	@PostMapping(value = "requirementsWithType")
//	public ResponseEntity<?> getRequirementsWithType(@RequestBody String type) throws IOException {
//		System.out.println("getRequirementsWithType called");
//		String completeAddress = mallikasAddress + "reqsWithType";
//
//		String reqsWithType = mallikasService.getAllRequirementsWithTypeFromMallikas(type, completeAddress);
//
//		System.out.println("Reqs found " + reqsWithType);
//
//		if (reqsWithType == null || reqsWithType.equals("")) {
//			return new ResponseEntity<>("Requirements not found \n\n", HttpStatus.NOT_FOUND);
//		}
//		ResponseEntity<String> response = new ResponseEntity<>(reqsWithType, HttpStatus.FOUND);
//		return response;
//	}
	
	/**
	 * Fetch a list of requirements according to their type and/or status from the database.
	 * @param type
	 * @return
	 * @throws IOException
	 */
	@ApiOperation(value = "Fetch requirements that have the selected requirement type and/or status from database", notes = "Requirement type and status should be given in all caps, e.g. BUG, NEW ")
	@ResponseBody
	@PostMapping(value = "requirementsWithTypeAndStatus")
	public ResponseEntity<?> getRequirementsWithTypeAndStatus(@RequestParam String type, @RequestParam String status) throws IOException {
		System.out.println("getRequirementsWithType called");
		String completeAddress = mallikasAddress + "reqsWithType";

		String reqsWithType = mallikasService.getAllRequirementsWithTypeAndStatusFromMallikas(type, status, completeAddress);

		System.out.println("Reqs found " + reqsWithType);

		if (reqsWithType == null || reqsWithType.equals("")) {
			return new ResponseEntity<>("Search failed, requirements not found \n\n", HttpStatus.NOT_FOUND);
		}
		ResponseEntity<String> response = new ResponseEntity<>(reqsWithType, HttpStatus.FOUND);
		return response;
	}

//	Not working properly at the moment, slows Milla down too much
//
//	/**
//	 * Fetch all Requirements and Dependencies from Mallikas
//	 * 
//	 * @return
//	 * @throws IOException
//	 */
//	@ApiOperation(value = "Fetch all requirements from the database", notes = "Fetch all requirements from Mallikas database")
//	@ResponseBody
//	@RequestMapping(value = "allRequirements", method = RequestMethod.GET)
//	public ResponseEntity<?> getAllRequirements() throws IOException {
//
//		System.out.println("getAllRequirements called");
//		String completeAddress = mallikasAddress + "mallikas/all";
//
//		String allRequirements = mallikasService.getAllRequirementsFromMallikas(completeAddress);
//
//		if (allRequirements == null || allRequirements.equals("")) {
//			return new ResponseEntity<>("Requirements not found \n\n", HttpStatus.NOT_FOUND);
//		}
//		ResponseEntity<String> response = new ResponseEntity<>(allRequirements, HttpStatus.FOUND);
//		return response;
//	}

	/**
	 * Fetch one Requirement and its Dependencies from Mallikas
	 * 
	 * @return
	 * @throws IOException
	 */
	@ApiOperation(value = "Fetch one selected requirement from the database", notes = "Fetch selected requirement and its dependencies from Mallikas database")
	@ResponseBody
	@PostMapping(value = "oneRequirement")
	public ResponseEntity<?> getOneRequirement(@RequestBody String id) throws IOException {

		System.out.println("getOneRequirement called");
		String completeAddress = mallikasAddress + "one";

		String requirement = mallikasService.getOneRequirementFromMallikas(completeAddress, id);

		if (requirement == null || requirement.equals("")) {
			return new ResponseEntity<>("Requirements not found \n\n", HttpStatus.NOT_FOUND);
		}
		ResponseEntity<String> response = new ResponseEntity<>(requirement, HttpStatus.FOUND);
		return response;
	}
	
	/**
	 * Fetch one Requirement and Requirements that depend on it from Mallikas
	 * 
	 * @return
	 * @throws IOException
	 */
	@ApiOperation(value = "Fetch one selected requirement and requirements dependent on it from the database", notes = "Fetch selected requirement and requirements that depend on it together with their dependencies from Mallikas database")
	@ResponseBody
	@PostMapping(value = "requirementAndDependents")
	public ResponseEntity<?> getOneRequirementAndDependents(@RequestBody String id) throws IOException {

		System.out.println("getOneRequirementAndDependents called");
		String completeAddress = mallikasAddress + "dependents";
		
		System.out.println("Id is " + id);

		String requirement = mallikasService.getOneRequirementFromMallikas(completeAddress, id);

		if (requirement == null || requirement.equals("")) {
			return new ResponseEntity<>("Requirements not found \n\n", HttpStatus.NOT_FOUND);
		}
		ResponseEntity<String> response = new ResponseEntity<>(requirement, HttpStatus.FOUND);
		return response;
	}
	
	/**
	 * Uses QtJiraImporter to get the issues of a selected project in OpenReq JSON
	 * format and sends them to Mallikas database.
	 * 
	 * @param projectId,
	 *            ID of the selected project
	 * @return ResponseEntity if successful
	 * @throws IOException
	 */
	@ApiOperation(value = "Import QT Jira", notes = "Import a project with its Jira issues and send it as OpenReq JSON requirements and dependencies to a database", response = String.class)
	@ApiResponses(value = { @ApiResponse(code = 201, message = "Success, all requirements and dependencies downloaded"),
			@ApiResponse(code = 400, message = "Failure, ex. malformed JSON"),
			@ApiResponse(code = 500, message = "Failure, ex. invalid URLs") })
	@ResponseBody
	@PostMapping(value = "qtjira")
	public ResponseEntity<?> importFromQtJira(@RequestBody String projectId) throws IOException {

		ProjectIssues projectIssues = new ProjectIssues(projectId);

		int issueCount = projectIssues.getNumberOfIssues();
		int divided = issueCount;
		if (issueCount > 10000) {  //this is necessary for large Qt Jira projects
			divided = issueCount / 10;
		}
		int start = 1;
		int end = divided;
		
//		int epicCount = 0; //these needed for counting epics and subtask relationships in projects
//		int subtaskCount = 0; //Note! to use these, must uncomment lines in FormatTransformerService

		List<String> requirementIds = new ArrayList<>();
		List<JsonElement> projectIssuesAsJson;
		try {
			while (true) { //a loop needed for sending large projects in chunks to Mallikas
				if (end >= issueCount + divided) {
					break;
				}
				projectIssuesAsJson = projectIssues.collectIssues(start, end);
				List<Issue> issues = transformer.convertJsonElementsToIssues(projectIssuesAsJson, projectId);
				Collection<Requirement> requirements = transformer.convertIssuesToJson(issues, projectId);
				Collection<Dependency> dependencies = transformer.getDependencies();
//				epicCount = epicCount + transformer.getEpicCount();
//				subtaskCount = subtaskCount + transformer.getSubtaskCount();
				requirementIds.addAll(transformer.getRequirementIds());
				this.postRequirementsToMallikas(requirements);
				this.postDependenciesToMallikas(dependencies);
				projectIssuesAsJson.clear();
				issues.clear();
				requirements.clear();
				dependencies.clear();
				System.out.println("End is " + end);
				start = end + 1;
				end = end + divided;
			}

			Project project = transformer.createProject(projectId, requirementIds);
			this.postProjectToMallikas(project);
			
//			System.out.println("Epic count is " + epicCount);
//			System.out.println("Subtask count is " + subtaskCount);
			
			ResponseEntity<String> response = new ResponseEntity<>("All requirements and dependencies downloaded", HttpStatus.OK);
			return response;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	
	/**
	 * Post a Collection of OpenReq JSON Requirements to UPC for Similarity detection.
	 * 
	 * @param projectId
	 * @return ResponseEntity<?>
	 * @throws IOException
	 */
	@ApiOperation(value = "Post requirements to UPC Similarity Detection", notes = "Post requirements and dependencies in a project as a String to UPC for Similarity Detection")
	@ResponseBody
	@PostMapping(value = "detectSimilarity")
	public ResponseEntity<?> postRequirementsToUPCSimilarityDetection(@RequestBody String projectId)
			throws IOException {

		RestTemplate rt = new RestTemplate();

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		
		String requirements = mallikasService.getAllRequirementsInProjectFromMallikas(projectId, mallikasAddress + "projectRequirements");

		String completeAddress = upcAddress + "upc/similarity-detection/DB/AddReqs";
		
		ResponseEntity<?> response = null;

		try {
			response = rt.postForEntity(completeAddress, requirements, String.class); //at the moment not working, UPC will give error

		} catch (HttpClientErrorException e) {
			return new ResponseEntity<>("UPC error:\n\n" + e.getResponseBodyAsString(), e.getStatusCode());
		}
//		catch(Exception e) {
//			e.printStackTrace();
//			return new ResponseEntity<>("Error " + e.getMessage(), HttpStatus.BAD_REQUEST);
//		}
		return response;
	}

}

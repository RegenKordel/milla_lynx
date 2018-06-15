package eu.openreq.milla.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonElement;

import eu.openreq.milla.models.entity.IssueObject;
import eu.openreq.milla.models.jira.Issue;
import eu.openreq.milla.models.jira.Jira;
import eu.openreq.milla.models.json.Dependency;
import eu.openreq.milla.models.json.Project;
//import eu.openreq.milla.models.mulson.Requirement;
import eu.openreq.milla.models.json.Requirement;
import eu.openreq.milla.services.FormatTransformerService;
import eu.openreq.milla.qtjiraimporter.ProjectIssues;
import eu.openreq.milla.qtjiraimporter.QtJiraImporter;
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

	@Autowired
	FormatTransformerService transformer;

	/**
	 * Post Requirements and Dependencies to Mulperi.
	 * @param data
	 * @param path
	 * @return
	 * @throws IOException
	 */
	@ApiOperation(value = "Relay POST to Mulperi", notes = "Post a model or configuration request to Mulperi")
	@ResponseBody
	@RequestMapping(value = "relay/{path}", method = RequestMethod.POST)
	public ResponseEntity<?> postToMulperi(@RequestBody String data, @PathVariable("path") String path)
			throws IOException {

		RestTemplate rt = new RestTemplate();

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		String actualPath = getActualPath(path);

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
	 * Post a Collection of OpenReq JSON Requirements to Mallikas database
	 * @param requirements Collection<Requirement> received as a parameter, requirements to be saved into the database
	 * @return ResponseEntity<?>
	 * @throws IOException
	 */
	@ApiOperation(value = "Relay POST to Mallikas", notes = "Post a list of requirements to Mallikas database")
	@ResponseBody
	@RequestMapping(value = "/mallikas", method = RequestMethod.POST)
	public ResponseEntity<?> postRequirementsToMallikas(@RequestBody Collection<Requirement> requirements) throws IOException {

		RestTemplate rt = new RestTemplate();
		
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		String completeAddress = mallikasAddress + "/mallikas";

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
	 * @param dependencies Collection<Dependency> received as a parameter, requirements to be saved into the database
	 * @return ResponseEntity<?>
	 * @throws IOException
	 */
	@ApiOperation(value = "Relay POST to Mallikas", notes = "Post a list of dependencies to Mallikas database")
	@ResponseBody
	@RequestMapping(value = "/dependencies", method = RequestMethod.POST)
	public ResponseEntity<?> postDependenciesToMallikas(@RequestBody Collection<Dependency> dependencies) throws IOException {
		
		RestTemplate rt = new RestTemplate();		
		
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		
		System.out.println("posDependencies called");
		String completeAddress = mallikasAddress + "/dependencies";

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
	 * @param project Project received as a parameter
	 * @return ResponseEntity<?>
	 * @throws IOException
	 */
	@ApiOperation(value = "Relay POST to Mallikas", notes = "Post a Project to Mallikas database")
	@ResponseBody
	@RequestMapping(value = "/project", method = RequestMethod.POST)
	public ResponseEntity<?> postProjectToMallikas(@RequestBody Project project) throws IOException {
		
		RestTemplate rt = new RestTemplate();	
		
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		
		System.out.println("postProjectToMallikas called");
		String completeAddress = mallikasAddress + "/project";

		Project newProject = project;
		ResponseEntity<?> response = null;
		
		try {
			response = rt.postForEntity(completeAddress, project, Project.class);
			
		} catch (HttpClientErrorException e) {
			return new ResponseEntity<>("Mallikas error:\n\n" + e.getResponseBodyAsString(), e.getStatusCode());
		}
		return response;
	}

	/**
	 * Method for setting the actual path to Mulperi
	 * @param path
	 * @return
	 */
	public String getActualPath(String path) { // Changed to public for TestingContoller
		if (path.equals("mulson"))
			return "models/mulson";
		if (path.equals("reqif"))
			return "models/reqif";
		if (path.contains("configure:")) {
			String modelName = path.split(":", 2)[1];
			return "models/" + modelName + "/configurations";
		}
		return path;
	}

	/**
	 * Send request to Mallikas to get one Requirement with a selected key
	 * @param key
	 */
	private void getOneRequirementFromMallikas(String key) {
		
		Map<String, String> params = new HashMap<String, String>();
		params.put("key", key);
		
		System.out.println("getRequirementFromMallikas called");
		
		RestTemplate rt = new RestTemplate();	
		String completeAddress = mallikasAddress + "/mallikas/{key}";
		Requirement req = rt.getForObject(completeAddress, Requirement.class, params);
		
		System.out.println("Requirement received " + req.getId());
	}
	
	/**
	 * Send request to Mallikas to get a List of Requirements based on a List of selected Requirement ids
	 * @param ids
	 */
	private void getRequirementsFromMallikas(List<String> ids) {
		
		Map<String, List<String>> params = new HashMap<String, List<String>>();
		params.put("ids", ids);
		
		System.out.println("getRequirementsFromMallikas called " + ids);
		
		RestTemplate rt = new RestTemplate();	
		String completeAddress = mallikasAddress + "/mallikas/reqs/{ids}";
		List<Requirement> reqs = rt.getForObject(completeAddress, List.class, params);
		
		System.out.println("Selected requirements received " + reqs.size());
	}
	
	/**
	 * Send request to Mallikas to get all Requirements in the database
	 */
	private void getAllRequirementsFromMallikas() {
		
		RestTemplate rt = new RestTemplate();	
		String completeAddress = mallikasAddress + "/mallikas/all";
		List<Requirement> reqs = rt.getForObject(completeAddress, List.class);
		
		System.out.println("Requirements received " + reqs.size());
	}
	
	/**
	 * Send request to Mallikas to get a List of Requirements that share the same classifier (so they belong to the same Qt Jira component)
	 * @param classifierId
	 */
	private void getAllRequirementsWithClassifierFromMallikas(String classifierId) {
		
		Map<String, String> params = new HashMap<String, String>();
		params.put("id", classifierId);
		
		RestTemplate rt = new RestTemplate();	
		String completeAddress = mallikasAddress + "/mallikas/classifiers/{id}";
		List<Requirement> reqs = rt.getForObject(completeAddress, List.class, params);
		
		System.out.println("Requirements received " + reqs.size());
	}
	
	/**
	 * Uses QtJiraImporter to get the issues of a selected project in OpenReq JSON format and sends them to Mallikas (and eventually to Mulperi).
	 * 
	 * @param projectId,
	 *            ID of the selected project
	 * @return mulsonString to Mulperi (At the moment broken)
	 * @throws IOException 
	 */
	@ApiOperation(value = "Import QT Jira", notes = "Generate a model from a project imported from Qt Jira (return an array of issues)", response = String.class)
	@ApiResponses(value = { @ApiResponse(code = 201, message = "Success, returns the name/id of the generated model"),
			@ApiResponse(code = 400, message = "Failure, ex. malformed JSON"),
			@ApiResponse(code = 500, message = "Failure, ex. invalid URLs") })
	@ResponseBody
	@RequestMapping(value = "qtjira", method = RequestMethod.POST)
	public ResponseEntity<?> importFromQtJira(@RequestBody String projectId) throws IOException {
		
		ProjectIssues projectIssues = new ProjectIssues(projectId);
		
		int issueCount = projectIssues.getNumberOfIssues();
		int divided = issueCount;	
		if(issueCount>10000) {
			divided = issueCount/10;
		}
		int start = 1;		
		int end = divided;
		
		List<String> requirementIds = new ArrayList<>();
		List<JsonElement> projectIssuesAsJson;
		try {				
			while(true) {
				if(end>=issueCount+divided) {
					break;
				}
				projectIssuesAsJson = projectIssues.collectIssues(start, end);
				List<Issue> issues = transformer.convertJsonElementsToIssues(projectIssuesAsJson, projectId);
				Collection<Requirement> requirements = transformer.convertIssuesToJson(issues, projectId);		
				Collection<Dependency> dependencies = transformer.getDependencies();
				requirementIds.addAll(transformer.getRequirementIds());
				this.postRequirementsToMallikas(requirements);
				this.postDependenciesToMallikas(dependencies);
				projectIssuesAsJson.clear();
				issues.clear();
				requirements.clear();
				dependencies.clear();
				System.out.println("End is " + end);
				start = end+1;
				end = end+ divided;
			}


			Project project = transformer.createProject(projectId, requirementIds);
			this.postProjectToMallikas(project);
		
			//Following lines are there just for testing the methods that call Mallikas
//			List<String> testList = new ArrayList<String>();
//			testList.add("QTWB-24");
//			testList.add("QTWB-16");
//			testList.add("QTWB-9");
//			testList.add("QTWB-10");
//
//			System.out.println("At the start the testList is " + testList);
//			this.getOneRequirementFromMallikas("QTWB-24");	
//			this.getAllRequirementsFromMallikas();
//			this.getAllRequirementsWithClassifierFromMallikas("22527");			
//			this.getRequirementsFromMallikas(testList);
//
//			ObjectMapper mapper = new ObjectMapper();
//			String mulsonString = mapper.writeValueAsString(requirements);		
			
			String mulsonString = "";

			return this.postToMulperi(mulsonString, "mulson");
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}

}

package eu.openreq.milla.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonElement;

import eu.openreq.milla.models.entity.IssueObject;
import eu.openreq.milla.models.jira.Issue;
import eu.openreq.milla.models.jira.Jira;
import eu.openreq.milla.models.mulson.Requirement;
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

	@ApiOperation(value = "Relay POST to Mallikas", notes = "Post a list of issues to Mallikas database")
	@ResponseBody
	@RequestMapping(value = "/mallikas", method = RequestMethod.POST)
	public ResponseEntity<?> postToMallikas(@RequestBody List<IssueObject> issues) throws IOException {

		RestTemplate rt = new RestTemplate();
		
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		String completeAddress = mallikasAddress + "/mallikas";

	//	HttpEntity<Object> entity = new HttpEntity<Object>(issues, headers);
		List<IssueObject> issueList = issues;
		ResponseEntity<?> response = null;

		try {
			response = rt.postForEntity(completeAddress, issueList, List.class);

		} catch (HttpClientErrorException e) {
			return new ResponseEntity<>("Mallikas error:\n\n" + e.getResponseBodyAsString(), e.getStatusCode());
		}
		return response;
	}

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
	 * Uses QtJiraImporter to get the issues of a selected project in mulson format
	 * to Mulperi
	 * 
	 * @param projectId,
	 *            ID of the selected project
	 * @return mulsonString to Mulperi
	 * @throws IOException 
	 */
	@ApiOperation(value = "Import QT Jira", notes = "Generate a model from a project imported from Qt Jira (return an array of issues)", response = String.class)
	@ApiResponses(value = { @ApiResponse(code = 201, message = "Success, returns the name/id of the generated model"),
			@ApiResponse(code = 400, message = "Failure, ex. malformed JSON"),
			@ApiResponse(code = 500, message = "Failure, ex. invalid URLs") })
	@ResponseBody
	@RequestMapping(value = "qtjira", method = RequestMethod.POST)
	public ResponseEntity<?> importFromQtJira(@RequestBody String projectId) throws IOException {
	//	QtJiraImporter jiraImporter = new QtJiraImporter();
		
		ProjectIssues projectIssues = new ProjectIssues(projectId);
		
		int issueCount = projectIssues.getNumberOfIssues();
		int divided = issueCount;
		
		if(issueCount>10000) {
			divided = issueCount/10;
		}
		int start = 1;
		
		int end = divided;
		List<JsonElement> projectIssuesAsJson;
		try {
			
			while(true) {
				if(end>=issueCount+divided) {
					break;
				}
				projectIssuesAsJson = projectIssues.collectIssues(start, end);
				List<Issue> issues = transformer.convertJsonElementsToIssues(projectIssuesAsJson, projectId);
				this.postToMallikas(transformer.getIssueObjects());
				projectIssuesAsJson.clear();
				issues.clear();
				System.out.println("End is " + end);
				start = end+1;
				end = end+ divided;
			}
			

			

//			List<Issue> issues = transformer.convertJsonElementsToIssues(projectIssuesAsJson, projectId);
//			Collection<Requirement> requirements = transformer.convertIssuesToMulson(issues, projectId);
//
//			ObjectMapper mapper = new ObjectMapper();
//			String mulsonString = mapper.writeValueAsString(requirements);
//			this.postToMallikas(transformer.getIssueObjects());
			
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

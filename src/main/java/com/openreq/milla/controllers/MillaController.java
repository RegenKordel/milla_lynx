package com.openreq.milla.controllers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
import com.openreq.milla.models.jira.Jira;
import com.openreq.milla.models.mulson.Requirement;
import com.openreq.milla.services.FormatTransformerService;

@SpringBootApplication
@Controller
@RequestMapping(value = "/")
public class MillaController {

	@Value("${milla.mulperiAddress}")
    private String mulperiAddress;
	
	@ResponseBody
	@RequestMapping(value = "{path}", method = RequestMethod.GET)
	public ResponseEntity<?> getFromMulperi(@PathVariable("path") String path) {
		
		RestTemplate rt = new RestTemplate();
		
		String actualPath = getActualPath(path);
		
		String completeAddress = mulperiAddress + actualPath;
		
		return rt.getForEntity(completeAddress, String.class);
		
	}
	
	@ResponseBody
	@RequestMapping(value = "{path}", method = RequestMethod.POST)
	public ResponseEntity<?> postToMulperi(@RequestBody String data, @PathVariable("path") String path) {
		
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

	private String getActualPath(String path) {
		if (path.equals("mulson")) return "models/mulson";
		if (path.equals("reqif")) return "models/reqif";
		if (path.contains("configurate:")) {
			String modelName = path.split(":", 2)[1];			
			return "models/" + modelName + "/configurations";
		}
			
		return path;
	}
	
	/**
	 * Inputs an array of search queries (return array of issues), ex:
	 [
	    "https://bugreports.qt.io/rest/api/2/search?jql=\"Epic Link\"=QTBUG-60623",
	    "https://bugreports.qt.io/rest/api/2/search?jql=issue = QTBUG-60467"
	 ]
	 * @param paths
	 * @return
	 * @throws JsonProcessingException
	 */
	@ResponseBody
	@RequestMapping(value = "jira", method = RequestMethod.POST)
	public ResponseEntity<?> loadFromJira(@RequestBody List<String> paths) throws JsonProcessingException {
		FormatTransformerService transformer = new FormatTransformerService();
		
		RestTemplate rt = new RestTemplate();
		
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		
		ArrayList<Jira> jiras = new ArrayList<>();
		
		for(String url : paths) {
			ResponseEntity<Jira> jiraResponse = rt.getForEntity(url, Jira.class);
			jiras.add(jiraResponse.getBody());
		}
		
		Collection<Requirement> requirements = transformer.convertJirasToMulson(jiras);
		
		ObjectMapper mapper = new ObjectMapper();
		String mulsonString = mapper.writeValueAsString(requirements);

		return this.postToMulperi(mulsonString, "mulson");
	}
	
	@RequestMapping(value = "/example/gui", method = RequestMethod.GET)
	public String exampleGUI(Model model) {

		model.addAttribute("mulperiAddress", mulperiAddress);
		return "exampleGUI";
	}
}

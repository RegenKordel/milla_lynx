package com.openreq.milla.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
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
		
		return rt.postForEntity(completeAddress, entity, String.class);
		
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
	
	@ResponseBody
	@RequestMapping(value = "jira", method = RequestMethod.POST)
	public ResponseEntity<?> loadFromJira(@RequestBody String path) throws JsonProcessingException {
		
		RestTemplate rt = new RestTemplate();
		
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		
		ResponseEntity<Jira> test = rt.getForEntity(path, Jira.class);
		
		FormatTransformerService transformer = new FormatTransformerService();
		
		List<Requirement> requirements = transformer.convertJiraToMulson(test.getBody());
		
		ObjectMapper mapper = new ObjectMapper();
		return this.postToMulperi(mapper.writeValueAsString(requirements), "mulson");
	}
	
	
}

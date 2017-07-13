package com.openreq.milla.controllers;

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

@SpringBootApplication
@Controller
@RequestMapping(value = "/")
public class MillaController {

	@Value("${milla.mulperiAddress}")
    private String mulperiAddress;
	
	@ResponseBody
	@RequestMapping(value = "{path}", method = RequestMethod.POST)
	public ResponseEntity<?> sendForwardToMulperi(@RequestBody String data, @PathVariable("path") String path) {
		
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
		return null;
	}
	
}

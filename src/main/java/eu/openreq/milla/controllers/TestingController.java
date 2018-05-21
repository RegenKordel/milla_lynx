package eu.openreq.milla.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import io.swagger.annotations.ApiOperation;

@SpringBootApplication
@Controller
public class TestingController {
	
	@Value("${milla.mulperiAddress}")
    private String mulperiAddress;
	
	@Autowired
	MillaController millaController;
	
	@RequestMapping(value = "/example/gui", method = RequestMethod.GET)
	public String exampleGUI(Model model) {

		model.addAttribute("mulperiAddress", mulperiAddress);
		return "exampleGUI";
	}
	
	@ApiOperation(value = "Relay GET to Mulperi",
		    notes = "Get a configuration from Mulperi")
	@ResponseBody
	@RequestMapping(value = "relay/{path}", method = RequestMethod.GET)
	public ResponseEntity<?> getFromMulperi(@PathVariable("path") String path) {
		
		RestTemplate rt = new RestTemplate();
		
		String actualPath = millaController.getActualPath(path);
		
		String completeAddress = mulperiAddress + actualPath;
		
		return rt.getForEntity(completeAddress, String.class);
	}
	
	
//	/**
//	 * Inputs an array of search queries (return array of issues), ex:
//	 [
//	    "https://bugreports.qt.io/rest/api/2/search?jql=\"Epic Link\"=QTBUG-60623",
//	    "https://bugreports.qt.io/rest/api/2/search?jql=issue = QTBUG-60467"
//	 ]
//	 * @param paths
//	 * @return
//	 * @throws JsonProcessingException
//	 */
//	@ApiOperation(value = "Parse Jira",
//		    notes = "Generate a model from an array of Jira search queries (that return an array of issues)",
//		    response = String.class)
//	@ApiResponses(value = { 
//			@ApiResponse(code = 201, message = "Success, returns the name of the generated model"),
//			@ApiResponse(code = 400, message = "Failure, ex. malformed JSON"),
//			@ApiResponse(code = 500, message = "Failure, ex. invalid URLs")}) 
//	@ResponseBody
//	@RequestMapping(value = "jira", method = RequestMethod.POST)
//	public ResponseEntity<?> loadFromJira(@RequestBody List<String> paths) throws JsonProcessingException {
//		FormatTransformerService transformer = new FormatTransformerService();
//		
//		RestTemplate rt = new RestTemplate();
//		
//		HttpHeaders headers = new HttpHeaders();
//		headers.setContentType(MediaType.APPLICATION_JSON);
//		
//		ConcurrentHashMap<String, Jira> jiras = new ConcurrentHashMap<>();
//		
//		paths.parallelStream().forEach((url) -> {
//			ResponseEntity<Jira> jiraResponse = rt.getForEntity(url, Jira.class);
//			jiras.put(url, jiraResponse.getBody());
//		});
//		
//		Collection<Requirement> requirements = transformer.convertJirasToMulson(jiras.values());
//		
//		ObjectMapper mapper = new ObjectMapper();
//		String mulsonString = mapper.writeValueAsString(requirements);
//
//		try {
//			return this.postToMulperi(mulsonString, "mulson");
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			return null;
//		}
//	}

}

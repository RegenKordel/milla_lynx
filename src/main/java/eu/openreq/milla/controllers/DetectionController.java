package eu.openreq.milla.controllers;

import java.io.IOException;
import java.util.List;

import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import eu.openreq.milla.services.MallikasService;
import eu.openreq.milla.services.JSONParser;
import io.swagger.annotations.ApiOperation; 

import eu.openreq.milla.models.json.*;

@SpringBootApplication
@RestController
public class DetectionController {

	@Value("${milla.mallikasAddress}")
	private String mallikasAddress;

	@Value("${milla.upcSimilarityAddress}")
	private String upcSimilarityAddress;
	
	@Value("${milla.upcCrossReferrenceAddress}")
	private String upcCrossReferenceAddress;

	@Autowired
	MallikasService mallikasService;
	
	@Autowired
	MillaController millaController;

	/**
	 * Post a Collection of OpenReq JSON Requirements to UPC for Similarity
	 * detection.
	 * 
	 * @param projectId
	 * @return ResponseEntity<?>
	 * @throws IOException
	 */
	@ApiOperation(value = "Post requirements to UPC Similarity Detection", notes = "Post requirements and dependencies in a project as a String to UPC for Similarity Detection")
	@ResponseBody
	@PostMapping(value = "detectSimilarityAddReqs")
	public ResponseEntity<?> postRequirementsToUPCSimilarityDetection(@RequestBody String projectId)
			throws IOException {

		RestTemplate rt = new RestTemplate();

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		String requirements = mallikasService.getAllRequirementsInProjectFromMallikas(projectId,
				mallikasAddress + "projectRequirements");

		String completeAddress = upcSimilarityAddress + "upc/similarity-detection/DB/AddReqs";

		ResponseEntity<?> response = null;

		try {
			response = rt.postForEntity(completeAddress, requirements, String.class);

		} catch (HttpClientErrorException e) {
			return new ResponseEntity<>("UPC error:\n\n" + e.getResponseBodyAsString(), e.getStatusCode());
		}
		return response;
	}

	/**
	 * Post a Collection of OpenReq JSON Requirements and Dependencies to UPC for
	 * comparing two requirements.
	 * @param jsonString
	 * @param reqId1
	 * @param reqId2
	 * @param component
	 * @return
	 * @throws IOException
	 */
	@ApiOperation(value = "Post two requirements to UPC Similarity Detection", notes = "Post requirements and dependencies as a String to UPC for Similarity Detection. Also requires ids of two requirements being compared, and the component (use DKPro)")
	@ResponseBody
	@PostMapping(value = "detectSimilarityReqReq")
	public ResponseEntity<?> postRequirementsToUPCSimilarityDetectionReqReq(@RequestBody String jsonString,
			@RequestParam String reqId1, @RequestParam String reqId2, @RequestParam String component)
			throws IOException{

		String completeAddress = upcSimilarityAddress + "upc/similarity-detection/ReqReq?req1=" + reqId1 + "&req2=" + reqId2
				+ "&component=" + component;

		ResponseEntity<?> entity = receiveDependenciesAndSendToMallikas(jsonString, completeAddress);
		return entity;
	}

	/**
	 * Post a Collection of OpenReq JSON Requirements and Dependencies in a project to UPC for Similarity
	 * detection.
	 * @param jsonString
	 * @param projectId
	 * @param component
	 * @param threshold
	 * @param elements
	 * @return
	 * @throws IOException
	 */
	@ApiOperation(value = "Post a project to UPC Similarity Detection", notes = "Post requirements and dependencies in a project as a String to UPC for Similarity Detection. Also requires project id, component (e.g. DKPro), threshold (e.g. 0.3) and number of element (e.g. 5)")
	@ResponseBody
	@PostMapping(value = "detectSimilarityProject")
	public ResponseEntity<?> postRequirementsToUPCSimilarityDetectionProject(@RequestBody String jsonString, @RequestParam String projectId,@RequestParam String component, @RequestParam String threshold, @RequestParam String elements)
			throws IOException {

		String completeAddress = upcSimilarityAddress
				+ "upc/similarity-detection/Project?project="+ projectId + "&component=" + component + "&threshold="+threshold + "&num_elements="+elements;

		ResponseEntity<?> entity = receiveDependenciesAndSendToMallikas(jsonString, completeAddress);
		return entity;
	}
	
	
	
	/**
	 * Post a Collection of OpenReq JSON Requirements and Dependencies in a project to UPC for Cross-Reference
	 * detection.
	 * 
	 * @param projectId
	 * @return ResponseEntity<?>
	 * @throws IOException
	 */
	@ApiOperation(value = "Post a project to UPC Cross-Reference Detection", notes = "Post requirements and dependencies in a project as a String to UPC for Cross-Reference Detection. Requires projectId")
	@ResponseBody
	@PostMapping(value = "detectCrossReferenceProject")
	public ResponseEntity<?> postRequirementsToUPCCrossReferenceDetectionProject(@RequestBody String jsonString, @RequestParam String projectId)
			throws IOException {

		String completeAddress = upcCrossReferenceAddress
				+ "upc/cross-reference-detection/json/"+ projectId;

		ResponseEntity<?> entity = receiveDependenciesAndSendToMallikas(jsonString, completeAddress);
		return entity;
	}
	
	/**
	 * Method that sends requirements and dependencies to UPC services, parses the response and sends received dependencies (new ones have status "proposed" to Mallikas)
	 * @param jsonString
	 * @param url
	 * @return
	 * @throws IOException
	 */
	private ResponseEntity<?> receiveDependenciesAndSendToMallikas(String jsonString, String url) throws IOException {
		RestTemplate rt = new RestTemplate();
		String response = null;
		ResponseEntity<?> entity = null;

		try {
			response = rt.postForObject(url, jsonString, String.class);
			if(response!=null) {
				JSONParser.parseToOpenReqObjects(response);
				List<Dependency> dependencies = JSONParser.dependencies;
				entity = millaController.postDependenciesToMallikas(dependencies);
			}		

		} catch (HttpClientErrorException e) {
			return new ResponseEntity<>("UPC error:\n\n" + e.getResponseBodyAsString(), e.getStatusCode());
		}
		catch (JSONException e) {
			e.printStackTrace();
		}
		return entity;
	}
}

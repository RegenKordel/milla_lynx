package eu.openreq.milla.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import eu.openreq.milla.models.json.Requirement;

@Service
public class MallikasService {
	
	
	/**
	 * Send request to Mallikas to get all Requirements in the database
	 * @param url the address in Mallikas
	 * @return
	 */
	public String getAllRequirementsFromMallikas(String url) {
		
		RestTemplate rt = new RestTemplate();	
		String reqsAndDependencies = null; 
		
		try {
			reqsAndDependencies=rt.getForObject(url, String.class);
			System.out.println("Requirements received " + reqsAndDependencies);
		}
		catch (HttpClientErrorException e) { //Probably a different exception here? 
			System.out.println("Error " + e);
			e.printStackTrace();
		}
		
		return reqsAndDependencies;
	}
	
	/**
	 * Send request to Mallikas to get one Requirement with a selected id
	 * @param url the address in Mallikas
	 * @param id String identifier of the Requirement
	 * @return
	 */
	public String getOneRequirementFromMallikas(String url, String id) {
	
		RestTemplate rt = new RestTemplate();	
		String req = null;
		
		try {
			req = rt.postForObject(url, id, String.class);	
			System.out.println("Requirement received " + req);
		}
		catch (HttpClientErrorException e) { //Probably a different exception here? 
			System.out.println("Error " + e);
			e.printStackTrace();
		}
		return req;
	}
	
	/**
	 * Send request to Mallikas to get a List of Requirements and their Dependecies as a String (based on a List of selected Requirement IDs) 
	 * @param ids List<String> containing selected Requirement IDs
	 * @param url the address in Mallikas
	 * @return
	 */
	public String getSelectedRequirementsFromMallikas(List<String> ids, String url) {
		
		RestTemplate rt = new RestTemplate();	//Same line of code in every method, perhaps create a constructor?
		String reqs = null;
		
		try {
			reqs= rt.postForObject(url, ids, String.class);
			System.out.println("Selected requirements received " + reqs);
		}
		catch (HttpClientErrorException e) { //Probably a different exception here? 
			System.out.println("Error " + e);
			e.printStackTrace();
		}
		
		return reqs;
	}
	
	/**
	 * Send request to Mallikas to get a String (List of Requirements that share the same classifier (so they belong to the same Qt Jira component) and their Dependencies)
	 * @param classifierId id of the Component/Classifier
	 * @param url the address in Mallikas
	 * @return String containing all requirements and their dependencies in the same component
	 */
	public String getAllRequirementsWithClassifierFromMallikas(String classifierId, String url) {

		RestTemplate rt = new RestTemplate();	
		String reqs = null;
		
		try {
			reqs = rt.postForObject(url, classifierId, String.class);
			System.out.println("Requirements received " + reqs);
			
		} catch (HttpClientErrorException e) { //Probably a different exception here? 
			System.out.println("Error " + e);
			e.printStackTrace();
		}
		return reqs;
	}

}

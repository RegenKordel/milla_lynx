package eu.openreq.milla.qtjiraimporter;

import com.google.gson.*;

import eu.openreq.milla.services.OAuthService;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;

/**
 * This class gets all the issues of a project. Since the issues are enumerated
 * continuously we can get the number of issues and iterate through ids of the
 * following form: PROJECTNAME-#issue. Since we get a higher number than the
 * actual amount of issues we get a few error messages which we ignore.
 */
public class ProjectIssues {

	// amount of issues in a project
	private int _maxIssues;
	
	private OAuthService authService;

	public ProjectIssues(OAuthService service) throws IOException, InvalidKeySpecException, NoSuchAlgorithmException {
		if (service!=null) {
			authService = service;
		} else {
			authService = new OAuthService("");
		}
		calcNumberOfIssues();
	}
	
	/**
	 * Collects issues that have keys between two numbers (start, end) from a specified project. 
	 * @param start Integer, starting point of the import
	 * @param end Integer, end point of the import
	 * @return List of JsonElements
	 * @throws IOException
	 * @throws NoSuchAlgorithmException 
	 * @throws InvalidKeySpecException 
	 * @throws InterruptedException 
	 */
	public Collection<JsonElement> collectIssues(int start, int end) throws IOException, InvalidKeySpecException, NoSuchAlgorithmException, InterruptedException {
		
		Gson issueJSON = new Gson();
		
		List<String> paths = new ArrayList<>();

		List<String> issuesToLoad = new ArrayList<>();
		issuesToLoad.add("RTFACT-10062");
		issuesToLoad.add("RTFACT-9786");
		issuesToLoad.add("RTFACT-14255");
		issuesToLoad.add("RTFACT-19053");
		issuesToLoad.add("RTFACT-16532");
		issuesToLoad.add("RTFACT-4412");
		issuesToLoad.add("RTFACT-8160");
		issuesToLoad.add("RTFACT-17799");
		issuesToLoad.add("RTFACT-14821");
		issuesToLoad.add("RTFACT-10537");
		issuesToLoad.add("RTFACT-12320");
		issuesToLoad.add("RTFACT-19442");
		issuesToLoad.add("RTFACT-20080");
		issuesToLoad.add("RTFACT-9767");
		issuesToLoad.add("RTFACT-7112");
		issuesToLoad.add("RTFACT-16477");
		issuesToLoad.add("RTFACT-12693");
		issuesToLoad.add("RTFACT-13871");
		issuesToLoad.add("RTFACT-21158");
		issuesToLoad.add("RTFACT-19153");
		issuesToLoad.add("RTFACT-12755");
		issuesToLoad.add("RTFACT-16275"); //bis hier original danach recommended
		issuesToLoad.add("RTFACT-18415");
		issuesToLoad.add("RTFACT-10026");
		issuesToLoad.add("RTFACT-24415");
		issuesToLoad.add("HAP-1023");
		issuesToLoad.add("RTFACT-18599");
		issuesToLoad.add("RTFACT-21957");
		issuesToLoad.add("RTFACT-229");
		issuesToLoad.add("RTFACT-14449");

		for (String name_of_issue : issuesToLoad) {
			// access the issue JSONs
			String requestURL = "/rest/api/2/issue/"+name_of_issue;
			paths.add(requestURL);
			requestURL = null;
		}

//		for (int i = start; i <= end; i++) {
//			// access the issue JSONs
//			String requestURL = "/rest/api/2/search?jql=&orderBy=-created&startAt="+i+"&maxResults=1&project=RTFACT";
//			paths.add(requestURL);
//			requestURL = null;
//		}

		ConcurrentHashMap<String, JsonElement> issues = new ConcurrentHashMap<>();
		ForkJoinPool customThreadPool = new ForkJoinPool(32);
		try {
			customThreadPool.submit(
			        () -> paths.parallelStream().forEach((url) -> {
			        	String responseJSON = "";
						responseJSON = authService.authorizedJiraRequest(url);

						if (responseJSON != null) {
							JsonElement element = issueJSON.fromJson(responseJSON, JsonElement.class);
							if (element != null && element.isJsonObject()) {
								JsonObject issueElement = element.getAsJsonObject();
//								JsonObject issueArray = issueElement.get("issues").getAsJsonArray().get(0).getAsJsonObject();
//								String urlId = url.substring(url.lastIndexOf("startAt=") + 8, url.lastIndexOf("&"));
//								System.out.println(urlId);
								String responseId = issueElement.get("key").getAsString();
								System.out.println(responseId);
//								if (urlId.equals(responseId)) {
								issues.put(url, issueElement);
//								}
								issueElement = null;
							}
						}
			        })
			).get();
		} catch (ExecutionException e) {
			System.out.println(e.getMessage());
			
		}
		paths.clear();
		return issues.values();
	
	}
	private void calcNumberOfIssues()
	{
		Gson issueJSON = new Gson();

		String requestURL = "/rest/api/2/search?jql=&maxResults=0";
		String responseJSON = "";
		responseJSON = authService.authorizedJiraRequest(requestURL);
		if (responseJSON != null)
		{
			JsonElement element = issueJSON.fromJson(responseJSON, JsonElement.class);
			JsonObject issueElement = element.getAsJsonObject();
			int bla = issueElement.get("total").getAsInt();
			_maxIssues = 35;
		}
	}

	public int getNumberOfIssues() {
		return _maxIssues;
	}

}

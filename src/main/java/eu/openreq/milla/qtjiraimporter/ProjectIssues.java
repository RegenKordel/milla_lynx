package eu.openreq.milla.qtjiraimporter;

import com.google.gson.*;

import okhttp3.OkHttpClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * This class gets all the issues of a project. Since the issues are enumerated
 * continuously we can get the number of issues and iterate through ids of the
 * following form: PROJECTNAME-#issue. Since we get a higher number than the
 * actual amount of issues we get a few error messages which we ignore.
 */
public class ProjectIssues {
	// HashMap to save in the Issue identifier and the JSON of the issue
//	private HashMap<String, JsonElement> _projectIssues;
	// name of the project
	private String _project;
	// amount of issues in a project
	private int _maxProjectIssues;
	// the REST API URI
	private String _PROJECT_ISSUES_URL;
	
	private List<JsonElement> projectIssues;
	
	private int start;
	private int end;
	

	public ProjectIssues(String project) throws IOException {
//		_projectIssues = new HashMap<String, JsonElement>();
		_project = project;
		NumberOfIssuesHTML numberOfIssues = new NumberOfIssuesHTML(project);
		_maxProjectIssues = numberOfIssues.getNumberOfIssues();
		_PROJECT_ISSUES_URL = "https://bugreports.qt.io/rest/api/2/issue/" + _project + "-%d";
		projectIssues = new ArrayList<JsonElement>();
	}

//	public void collectAllIssues(int start, int end) throws IOException {
//		this.start = start;
//		this.end = end;
//		OkHttpClient client = new OkHttpClient();
//		Run run = new Run();
//
////		// create the error message JSON
////		JsonParser parser = new JsonParser();
////		JsonObject error = parser.parse("{\"errorMessages\":[\"Issue Does Not Exist\"],\"errors\":{}}")
////				.getAsJsonObject();
//
//		int j = 1;
//		Gson issueJSON = new Gson();
//		int perc10 = end / 10;
//
//		for (int i = start; i <= end; i++) {
//			// access the issue JSONs
//			String requestURL = String.format(_PROJECT_ISSUES_URL, i);
//			String responseJSON = run.run(requestURL, client);
//			
//			if (responseJSON != null) {
////				responses.add(responseJSON);
//				JsonObject issueElement = issueJSON.fromJson(responseJSON, JsonElement.class).getAsJsonObject();
//				//_projectIssues.put(_project + "-" + i, issueElement);
//				projectIssues.add(issueElement);
//				issueElement = null;
//			}
////			if (i % perc10 == 0) {
////				printProgress(i, j, perc10);
////				j++;
////			}
//			requestURL = null;
//			responseJSON = null;
//		}
//	
//	}
	
	
	public List<JsonElement> collectIssues(int start, int end) throws IOException {
		this.start = start;
		this.end = end;
		OkHttpClient client = new OkHttpClient();
		Run run = new Run();

//		// create the error message JSON
//		JsonParser parser = new JsonParser();
//		JsonObject error = parser.parse("{\"errorMessages\":[\"Issue Does Not Exist\"],\"errors\":{}}")
//				.getAsJsonObject();

		int j = 1;
		Gson issueJSON = new Gson();
		int perc10 = end / 10;

		for (int i = start; i <= end; i++) {
			// access the issue JSONs
			String requestURL = String.format(_PROJECT_ISSUES_URL, i);
			String responseJSON = run.run(requestURL, client);
			
			if (responseJSON != null) {
//				responses.add(responseJSON);
				JsonObject issueElement = issueJSON.fromJson(responseJSON, JsonElement.class).getAsJsonObject();
				//_projectIssues.put(_project + "-" + i, issueElement);
				projectIssues.add(issueElement);
				issueElement = null;
			}
//			if (i % perc10 == 0) {
//				printProgress(i, j, perc10);
//				j++;
//			}
			requestURL = null;
			responseJSON = null;
		}
		
		return projectIssues;
	
	}


	private void printProgress(long i, int j, int perc10) {
		int k = j * 10;
		System.out.print("[");
		for (int n = 1; n <= j; n++) {
			System.out.print("/");
		}
		for (int m = 10; m > j; m--) {
			System.out.print(" ");
		}
		System.out.print("] ");
		System.out.println(k + "% are done");

	}

//	public HashMap<String, JsonElement> getProjectIssues() {
//		return _projectIssues;
//	}
	
	public List<JsonElement> getProjectIssues() {
		return projectIssues;
	}
	
	public int getNumberOfIssues() {
		return _maxProjectIssues;
	}

}

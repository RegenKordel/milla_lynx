package eu.openreq.milla.qtjiraimporter;

import com.google.gson.*;

import okhttp3.OkHttpClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;

public class UpdatedIssues {

	// HashMap to save in the Issue identifier and the JSON of the issue
	private HashMap<String, JsonElement> _projectIssues;
	// name of the project
	private String _project;
//	// the REST API URI
//	private String _PROJECT_ISSUES_URL;

	private String singleIssueUrl;

	private String projectIssuesUrl;

	public UpdatedIssues(String project) throws IOException {
		_projectIssues = new HashMap<String, JsonElement>();
		_project = project;
//		_PROJECT_ISSUES_URL = "https://bugreports.qt.io/rest/api/2/search?jql=project=" + project
//				+ "&orderBy=-created&maxResults=1000&startAt=";
//		singleIssueUrl = "https://bugreports.qt.io/rest/api/2/search?jql=project=" + project
//				+ "&orderBy=-updated&maxResults=1&startAt=";
		singleIssueUrl = "https://bugreports.qt.io/rest/api/2/search?jql=project=" + project + "+order+by+updated+DESC&maxResults=1&startAt=";
//		projectIssuesUrl = "https://bugreports.qt.io/rest/api/2/search?jql=project=" + project
//				+ "&orderBy=-updated&maxResults=1&startAt=";
//		projectIssuesUrl = "https://bugreports.qt.io/rest/api/2/search?jql=project=" + project + "+order+by+updated+DESC&maxResults=100&startAt=";
		// "https://bugreports.qt.io/rest/api/2/search?jql=project=QTBUG&orderBy=-updated&maxResults=1000&startAt=1000"
	}

	/**
	 * Fetches the 100th (possibly) updated Issue from Qt Jira
	 * @param start
	 * @return
	 * @throws IOException
	 */
	public JsonElement getTheLatestUpdatedIssue(int start) throws IOException {
		OkHttpClient client = new OkHttpClient();
		Run run = new Run();

		int i = start;
		String requestURL = singleIssueUrl + i;
		String responseJSON = run.run(requestURL, client);
		Gson gson = new Gson();
		JsonObject projectJSON = null;
		JsonElement element = null;
		if (responseJSON != null) {
			projectJSON = gson.fromJson(responseJSON, JsonElement.class).getAsJsonObject();
			JsonArray issuesInProjectJSON = projectJSON.getAsJsonArray("issues");
			System.out.println("issuesInProjectJSON.size is " + issuesInProjectJSON.size());
			element = issuesInProjectJSON.get(0);
		}
		return element;
	}

	/**
	 * Fetches 100-1000 updated Issues from Qt Jira
	 * @param project
	 * @param amount
	 * @throws IOException
	 */
	public void collectAllUpdatedIssues(String project, int amount) throws IOException {
		OkHttpClient client = new OkHttpClient();
		Run run = new Run();
		projectIssuesUrl = "https://bugreports.qt.io/rest/api/2/search?jql=project=" + project
				+ "+order+by+updated+DESC&maxResults=" + amount + "&startAt=0";

		String responseJSON = run.run(projectIssuesUrl, client);
		if(responseJSON!=null) {
		Gson gson = new Gson();
		JsonObject projectJSON = gson.fromJson(responseJSON, JsonElement.class).getAsJsonObject();
		JsonArray issuesInProjectJSON = projectJSON.getAsJsonArray("issues");
		for (int i = 0; i < issuesInProjectJSON.size(); i++) {
			JsonObject issueJSON = issuesInProjectJSON.get(i).getAsJsonObject();
				String issueKey = issueJSON.get("key").getAsString();
				_projectIssues.put(issueKey, issueJSON);
	
		}
		}

	}

	public Collection<JsonElement> getProjectIssues() {
		return _projectIssues.values();
	}

	// public HashMap<String, JsonElement> getProjectIssues()
	// {
	// return _projectIssues;
	// }

}

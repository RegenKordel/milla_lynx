package eu.openreq.milla.qtjiraimporter;

import com.google.gson.*;

import okhttp3.OkHttpClient;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;

/**
 * This class gets all the issues of a project. Since the issues are enumerated
 * continuously we can get the number of issues and iterate through ids of the
 * following form: PROJECTNAME-#issue. Since we get a higher number than the
 * actual amount of issues we get a few error messages which we ignore.
 */
public class ProjectIssues {
	// HashMap to save in the Issue identifier and the JSON of the issue
	private HashMap<String, JsonElement> _projectIssues;
	// name of the project
	private String _project;
	// amount of issues in a project
	private int _maxProjectIssues;
	// the REST API URI
	private String _PROJECT_ISSUES_URL;

	public ProjectIssues(String project) throws IOException {
		_projectIssues = new HashMap<String, JsonElement>();
		_project = project;
		NumberOfIssuesHTML numberOfIssues = new NumberOfIssuesHTML(project);
		_maxProjectIssues = numberOfIssues.getNumberOfIssues();
		_PROJECT_ISSUES_URL = "https://bugreports.qt.io/rest/api/2/issue/" + _project + "-%d";
	}

	/**
	 * This method is here just for QTBUG, if one wants to import it in four parts, ignore otherwise
	 * @throws IOException
	 */
	// public void collectAllIssues() throws IOException {
	//
	// long part = _maxProjectIssues / 4;
	//
	// long sum = 0;
	//
	// long start = 1;
	//
	// Gson issueJSON = new Gson();
	//
	// // create the error message JSON
	// JsonParser parser = new JsonParser();
	// JsonObject error = parser.parse("{\"errorMessages\":[\"Issue Does
	// NotExist\"],\"errors\":{}}")
	// .getAsJsonObject();
	//
	// OkHttpClient client = new OkHttpClient();
	// //
	// // for (int index = 1; index < 4; index++) {
	// // System.out.println("HERE");
	// // // OkHttpClient client = new OkHttpClient();
	// // Run run = new Run();
	// //
	// // // create the error message JSON
	// // // JsonParser parser = new JsonParser();
	// // // JsonObject error = parser.parse("{\"errorMessages\":[\"Issue Does Not
	// // // Exist\"],\"errors\":{}}")
	// // // .getAsJsonObject();
	// //
	// // int j = 1;
	// //
	// // sum += part;
	// //
	// // for (long i = start; i <= sum; i++) {
	// // // access the issue JSONs
	// // String requestURL = String.format(_PROJECT_ISSUES_URL, i);
	// // String responseJSON = run.run(requestURL, client);
	// // // Gson issueJSON = new Gson(); //Moved this outside the loop, trying to
	// make
	// // // the code faster
	// // JsonObject issueElement = issueJSON.fromJson(responseJSON,
	// // JsonElement.class).getAsJsonObject();
	// //
	// // // filter out the error messages
	// // if (!issueElement.equals(error)) {
	// // _projectIssues.put(_project + "-" + i, issueElement);
	// // }
	// //
	// // int perc10 = _maxProjectIssues / 10;
	// //
	// // if (i % perc10 == 0) {
	// // // to see the progress
	// // int k = j * 10;
	// // System.out.print("[");
	// // for (int n = 1; n <= j; n++) {
	// // System.out.print("/");
	// // }
	// // for (int m = 10; m > j; m--) {
	// // System.out.print(" ");
	// // }
	// // System.out.print("] ");
	// // System.out.println(k + "% are done");
	// // j++;
	// // }
	// // }
	// //
	// // start = sum;
	// // System.out.println(index + " part okay");
	// // }
	// System.out.println("Last part started");
	// // OkHttpClient client = new OkHttpClient();
	// Run run = new Run();
	// // create the error message JSON
	// // JsonParser parser = new JsonParser();
	// // JsonObject error = parser.parse("{\"errorMessages\":[\"Issue Does Not
	// // Exist\"],\"errors\":{}}")
	// // .getAsJsonObject();
	//
	// int j = 1;
	// // Gson issueJSON = new Gson();
	//
	// for (long i = 50000; i <= _maxProjectIssues; i++) {
	// // access the issue JSONs
	// String requestURL = String.format(_PROJECT_ISSUES_URL, i);
	// String responseJSON = run.run(requestURL, client);
	//
	// // Gson issueJSON = new Gson(); //Moved this outside the loop, trying to make
	// // the code faster
	//
	// if (responseJSON != null) {		//This line is needed in case of Exceptions (see class Run)
	// JsonObject issueElement = issueJSON.fromJson(responseJSON,
	// JsonElement.class).getAsJsonObject();
	//
	// // filter out the error messages
	// if (!issueElement.equals(error)) {
	// _projectIssues.put(_project + "-" + i, issueElement);
	// }
	// }
	//
	// int perc10 = _maxProjectIssues / 10;
	//
	// if (i % perc10 == 0) {
	// // to see the progress
	// int k = j * 10;
	// System.out.print("[");
	// for (int n = 1; n <= j; n++) {
	// System.out.print("/");
	// }
	// for (int m = 10; m > j; m--) {
	// System.out.print(" ");
	// }
	// System.out.print("] ");
	// System.out.println(k + "% are done");
	// j++;
	// }
	// }
	//
	// }

	public void collectAllIssues() throws IOException {
		OkHttpClient client = new OkHttpClient();
		Run run = new Run();

		// create the error message JSON
		JsonParser parser = new JsonParser();
		JsonObject error = parser.parse("{\"errorMessages\":[\"Issue Does Not Exist\"],\"errors\":{}}")
				.getAsJsonObject();

		int j = 1;
		Gson issueJSON = new Gson(); //Moved this outside the loop
		int perc10 = _maxProjectIssues / 10;
		
//		 String fileName = "" + _project + "_errors.txt"; // File path must be added if a log file of the issues is needed
//		 FileWriter fileWriter = new FileWriter(fileName);
//		 PrintWriter printWriter = new PrintWriter(fileWriter);
//		 String newLine = System.getProperty("line.separator");
		 

		for (long i = 1; i <= _maxProjectIssues; i++) { //Change i=1 to i=50000 to try getting QTBUG SocketTimeOut Exceptions
			// access the issue JSONs
			String requestURL = String.format(_PROJECT_ISSUES_URL, i);
			String responseJSON = run.run(requestURL, client);
			
//			if (responseJSON != null) { //This line is needed in case of exceptions in the class Run (also eliminates NullPointers (401 responses) in FormatTransformerService)	
				JsonObject issueElement = issueJSON.fromJson(responseJSON, JsonElement.class).getAsJsonObject();

				// filter out the error messages
				if (!issueElement.equals(error)) {
					_projectIssues.put(_project + "-" + i, issueElement);
				}
////				if(issueElement.equals(error)) { 
////					 printWriter.print(responseJSON + i+ newLine);
////				}
//			}

			if (i % perc10 == 0) {
				// to see the progress
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
				j++;
			}
		}
	//	printWriter.close(); 
	}

	public HashMap<String, JsonElement> getProjectIssues() {
		return _projectIssues;
	}

}

package eu.openreq.milla.qtjiraimporter;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;

import com.google.gson.JsonElement;

/**
 * A helper class for getting ProjectIssues from a selected project.
 * Contains for now also some printing (issues, timer) for testing purposes. 
 * 
 */

/**
* Notes: Some issues link to other projects (f.e. QTPLAYGROUND-1 links to QTBUG-32034),
* since we have all the issues in JSON format, we can filter for specific keys.
*/

public class QtJiraImporter {
	
	private int numberOfIssues;
//	/**
//	 * Method for getting all issues related to one project as JsonElements
//	 * @return a HashMap containing all issues belonging to one project
//	 * @throws IOException
//	 */
//	public HashMap<String, JsonElement> getProjectIssues(String projectId) throws IOException {
//		long start = System.nanoTime();
//	
//		if(projectId==null) {
//			System.out.println("Invalid ProjectId received");
//			return null;
//		}
//
//		ProjectIssues projectIssues = new ProjectIssues(projectId);
//		projectIssues.collectAllIssues();
//		responses = projectIssues.getResponses();
//
//		// for testing
//		HashMap<String, JsonElement> allIssues = projectIssues.getProjectIssues();
//		System.out.println("got everything");
//
//		// print everything
//		String fileName = "" + projectId + "_log.txt"; //File path must be added if a log file is needed
//		FileWriter fileWriter = new FileWriter(fileName);
//		PrintWriter printWriter = new PrintWriter(fileWriter);
//		String newLine = System.getProperty("line.separator");
		
//		for (String name : allIssues.keySet()) {
//			String key = name.toString();
//			JsonElement value = allIssues.get(name);
////			printWriter.print(key + " " + value + newLine);
////			printWriter.print(" ");
////			printWriter.print(" ");
////			System.out.println(key + " " + value); //Probably a good idea to leave the printing out when dealing with large projects
//
//		}
//		printWriter.close();
//
//		long end = System.nanoTime();
//		long durationSec = (end - start) / 1000000000;
//		double durationMin = durationSec / 60.0;
//
//		System.out.println("done, it took " + durationSec + " second(s) or " + durationMin + " minute(s).");
//		return allIssues;
//	}
	
	/**
	 * AT THE MOMENT UNUSED, MAY BE MODIFIED LATER
	 * 
	 * Method for getting all issues related to one project as JsonElements
	 * @return a HashMap containing all issues belonging to one project
	 * @throws IOException
	 */
	public List<JsonElement> getProjectIssues(String projectId, int startPoint, int endPoint) throws IOException {
		long start = System.nanoTime();
	
		if(projectId==null) {
			System.out.println("Invalid ProjectId received");
			return null;
		}

		ProjectIssues projectIssues = new ProjectIssues(projectId);
		numberOfIssues = projectIssues.getNumberOfIssues();
	//	projectIssues.collectAllIssues(startPoint, endPoint);
//		responses = projectIssues.getResponses();

		// for testing
		List<JsonElement> allIssues = projectIssues.getProjectIssues();
		System.out.println("got everything");

		// print everything
//		String fileName = "" + projectId + "_log.txt"; //File path must be added if a log file is needed
//		FileWriter fileWriter = new FileWriter(fileName);
//		PrintWriter printWriter = new PrintWriter(fileWriter);
//		String newLine = System.getProperty("line.separator");
//		
//		for (JsonElement issue : allIssues) {
////			String key = name.toString();
////			JsonElement value = allIssues.get(name);
//////			printWriter.print(key + " " + value + newLine);
//////			printWriter.print(" ");
//////			printWriter.print(" ");
//////			System.out.println(key + " " + value); //Probably a good idea to leave the printing out when dealing with large projects
////
//		}
//		printWriter.close();

		long end = System.nanoTime();
		long durationSec = (end - start) / 1000000000;
		double durationMin = durationSec / 60.0;

		System.out.println("done, it took " + durationSec + " second(s) or " + durationMin + " minute(s).");
		return allIssues;
	}
	
	public int getNumberOfIssues() {
		return numberOfIssues;
	}

}

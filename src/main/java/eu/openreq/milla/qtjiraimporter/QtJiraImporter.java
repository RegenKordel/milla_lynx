package eu.openreq.milla.qtjiraimporter;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;

import com.google.gson.JsonElement;

/**
 * A helper class for getting ProjectIssues from a selected project.
 * Contains for now also some printing (issues, timer) for testing purposes. 
 * 
 */

public class QtJiraImporter {
	
	/**
	 * Method for getting all issues related to one project as JsonElements
	 * @return a HashMap containing all issues belonging to one project
	 * @throws IOException
	 */
	public HashMap<String, JsonElement> getProjectIssues(String projectId) throws IOException {
		long start = System.nanoTime();
	
		if(projectId==null) {
			System.out.println("Invalid ProjectId received");
			return null;
		}

		ProjectIssues projectIssues = new ProjectIssues(projectId);
		projectIssues.collectAllIssues();

		// for testing
		HashMap<String, JsonElement> allIssues = projectIssues.getProjectIssues();
		int i = 0;
		System.out.println("got everything");

		// print everything
//		String fileName = "" + projectId + "_log.txt"; //File path must be added if a log file is needed
//		FileWriter fileWriter = new FileWriter(fileName);
//		PrintWriter printWriter = new PrintWriter(fileWriter);
//		String newLine = System.getProperty("line.separator");
		
		for (String name : allIssues.keySet()) {
			String key = name.toString();
			JsonElement value = allIssues.get(name);
//			printWriter.print(key + " " + value + newLine);
//			printWriter.print(" ");
//			printWriter.print(" ");
			System.out.println(key + " " + value); //Probably a good idea to leave the printing out when dealing with large projects
			i++;
		}
//		printWriter.close();
		System.out.println(i);

		long end = System.nanoTime();
		long durationSec = (end - start) / 1000000000;
		double durationMin = durationSec / 60.0;

		System.out.println("done, it took " + durationSec + " second(s) or " + durationMin + " minute(s).");
		return allIssues;
	}

}

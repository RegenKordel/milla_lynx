package eu.openreq.milla.qtjiraimporter;

import java.io.IOException;
import java.util.HashMap;

import com.google.gson.JsonElement;

/**
 * A helper class for getting ProjectIssues from a selected project.
 * Contains for now also some printing (issues, timer) for testing purposes. 
 * 
 */

public class QtJiraImporter {
	
	/**
	 * 
	 * @return a HashMap containing all issues belonging to one project
	 * @throws IOException
	 */
	public HashMap<String, JsonElement> getProjectIssues() throws IOException {
		long start = System.nanoTime();

		String project = "QTWB"; // The project used for testing, this must be modified at some point

		ProjectIssues projectIssues = new ProjectIssues(project);
		projectIssues.collectAllIssues();

		// for testing
		HashMap<String, JsonElement> allIssues = projectIssues.getProjectIssues();
		int i = 0;
		System.out.println("got everything");

		// print everything

		for (String name : allIssues.keySet()) {
			String key = name.toString();
			JsonElement value = allIssues.get(name);
			System.out.println(key + " " + value);
			i++;
		}
		System.out.println(i);

		long end = System.nanoTime();
		long durationSec = (end - start) / 1000000000;
		double durationMin = durationSec / 60.0;

		System.out.println("done, it took " + durationSec + " second(s) or " + durationMin + " minute(s).");
		return allIssues;
	}

}

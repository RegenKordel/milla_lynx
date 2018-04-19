package eu.openreq.milla.qtjiraimporter;

import com.google.gson.JsonElement;
import java.io.IOException;
import java.util.*;

/**
 * Notes: Some issues link to other projects (f.e. QTPLAYGROUND-1 links to QTBUG-32034),
 * since we have all the issues in JSON format, we can filter for specific keys.
 */

public class Main
{
    // We want to select a project from a list of projects
    public static void main(String[] args) throws IOException
    {
        long start = System.nanoTime();

        ProjectNames projectNames = new ProjectNames();
        projectNames.printNames();

        Scanner reader = new Scanner(System.in);
        System.out.println("Enter the desired project from the list above: ");
        String project = reader.next();

        boolean validProjectName = false;
        for(String s : projectNames.getProjectNames())
        {
            if(project.equals(s))
            {
                validProjectName = true;
                ProjectIssues projectIssues = new ProjectIssues(project);
                projectIssues.collectAllIssues();

                // for testing
                HashMap<String, JsonElement> allIssues = projectIssues.getProjectIssues();
                int i = 0;
                System.out.println("got everything");

                //print everyhting

                for (String name: allIssues.keySet())
                {
                    String key =name.toString();
                    JsonElement value = allIssues.get(name);
                    System.out.println(key + " " + value);
                    i++;
                }
                System.out.println(i);

                long end = System.nanoTime();
                long durationSec = (end - start)/1000000000;
                double durationMin = durationSec/60.0;

                System.out.println("done, it took "+durationSec+" second(s) or " + durationMin + " minute(s).");
            }
        }
        reader.close();
        if(!validProjectName)
        {
            System.out.println("Not a valid project, please restart the application.");
        }

    }
}
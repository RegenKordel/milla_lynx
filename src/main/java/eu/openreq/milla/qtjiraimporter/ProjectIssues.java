package eu.openreq.milla.qtjiraimporter;

import com.google.gson.*;
import okhttp3.OkHttpClient;

import java.io.IOException;
import java.util.HashMap;


/**
 * This class gets all the issues of a project.
 * Since the issues are enumerated continuously we can get the number of issues
 * and iterate through ids of the following form: PROJECTNAME-#issue.
 * Since we get a higher number than the actual amount of issues we get a few
 * error messages which we ignore.
 */
public class ProjectIssues
{
    //hashmap to save in the Issue identifier and the JSON of the issue
    private HashMap<String, JsonElement> _projectIssues;
    //name of the project
    private String _project;
    //amounf of issues in a project
    private int _maxProjectIssues;
    //the REST API URI
    private String _PROJECT_ISSUES_URL;

    public ProjectIssues(String project) throws IOException
    {
        _projectIssues = new HashMap<String, JsonElement>();
        _project = project;
        NumberOfIssuesHTML numberOfIssues = new NumberOfIssuesHTML(project);
        _maxProjectIssues  = numberOfIssues.getNumberOfIssues();
        _PROJECT_ISSUES_URL = "https://bugreports.qt.io/rest/api/2/issue/" + _project +"-%d";
    }

    public void collectAllIssues() throws IOException
    {
        OkHttpClient client = new OkHttpClient();
        Run run = new Run();

        //create the error message JSON
        JsonParser parser = new JsonParser();
        JsonObject error = parser.parse("{\"errorMessages\":[\"Issue Does Not Exist\"],\"errors\":{}}").getAsJsonObject();

        int j = 1;

        for(long i = 1; i <= _maxProjectIssues; i++)
        {
            //access the issue JSONs
            String requestURL = String.format(_PROJECT_ISSUES_URL, i);
            String responseJSON = run.run(requestURL, client);
            Gson issueJSON = new Gson();
            JsonObject issueElement = issueJSON.fromJson(responseJSON, JsonElement.class).getAsJsonObject();
            //filter out the error messages
            if(!issueElement.equals(error))
            {
                _projectIssues.put(_project + "-" + i, issueElement);
            }
            int perc10 = _maxProjectIssues/10;

            if(i%perc10==0)
            {
                //to see the progress
                int k = j*10;
                System.out.print("[");
                for(int n = 1; n <= j; n++)
                {
                    System.out.print("/");
                }
                for(int m = 10; m > j; m--)
                {
                    System.out.print(" ");
                }
                System.out.print("]   ");
                System.out.println(k+"% are done");
                j++;
            }
        }
    }

    public HashMap<String, JsonElement> getProjectIssues()
    {
        return _projectIssues;
    }

}

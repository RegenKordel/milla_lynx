package eu.openreq.milla.qtjiraimporter;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import okhttp3.OkHttpClient;
import java.io.IOException;
import java.util.ArrayList;

public class ProjectNames
{
    private static final String PROJECT_NAMES_URL = "https://bugreports.qt.io/rest/api/2/project";

    private ArrayList<String> projectNames;

    public ProjectNames() throws IOException
    {
        projectNames = findProjectNames();
    }

    public ArrayList<String> findProjectNames() throws IOException
    {
        //connection
        OkHttpClient client = new OkHttpClient();
        Run run = new Run();
        String responseJSON = run.run(PROJECT_NAMES_URL, client);

        //get JSON
        Gson issueJSON = new Gson();
        JsonArray projectNamesJSON = issueJSON.fromJson(responseJSON, JsonArray.class);

        //use JSON to get names in an ArrayList
        ArrayList<String> projectNames = new ArrayList<String>();
        for (JsonElement i : projectNamesJSON)
        {
            projectNames.add(i.getAsJsonObject().get("key").getAsString());
        }

        return projectNames;
    }

    public ArrayList<String> getProjectNames()
    {
        return projectNames;
    }

    public void printNames()
    {
        for(String s : projectNames)
        {
            System.out.println(s);
        }

    }
}

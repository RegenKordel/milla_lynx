package eu.openreq.milla.qtjiraimporter;

import java.util.ArrayList;
import java.util.List;


/**
 * gets the Number of Issues from the website by fetching the newest issue and it's id
 */
public class NumberOfIssuesHTML
{
    private int _numberOfIssues;
    private String _URL;
    private String _project;

    public NumberOfIssuesHTML(String project)
    {
        _URL = "https://bugreports.qt.io/projects/"+ project +"/issues/?filter=allissues";
        _project = project;
        _numberOfIssues = detectNumberOfIssues();
    }

    private int detectNumberOfIssues()
    {
        int projectStringlength = _project.length();
        ArrayList<String> page = ImportHTML.importPage(_URL);
        for (String line : page)
        {
            if(line.contains("\\\"issueKeys\\\":"))
            {
                String text = (line.substring(line.indexOf("\\\"issueKeys\\\":")+17+projectStringlength, line.indexOf("\\\"issueKeys\\\":")+26+projectStringlength));
                int numberOfIssues = Integer.parseInt(text.replaceAll("[^0-9.]", ""));
                System.out.println(numberOfIssues);
                return numberOfIssues;
            }
        }
        return -1;
    }

    public int getNumberOfIssues()
    {
        return _numberOfIssues;
    }
}

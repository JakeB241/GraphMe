package umich.jakebock.graphme.support_classes;

import android.content.Context;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import umich.jakebock.graphme.classes.DataProject;

/**
 * Created by Jake on 12/27/2017.
 */

public class DataProjectContainer
{
    // File Suffix
    static final String GRAPHME_FILE_SUFFIX = ".g4m";

    // Context
    private Context context;

    public DataProjectContainer(Context context)
    {
        this.context = context;
    }

    public void createProject(DataProject dataProject)
    {
        try
        {
            // Create the File
            String filename = dataProject.getProjectTitle() + GRAPHME_FILE_SUFFIX;
            FileOutputStream fileOutputStream = context.openFileOutput(filename, Context.MODE_APPEND);

            // Create the XML File
            XmlHandler xmlHandler = new XmlHandler();
            xmlHandler.createXMLProject(dataProject, fileOutputStream);
        }

        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
    }

    public ArrayList<DataProject> deleteProjects(ArrayList<String> projectNames)
    {
        // Delete the Projects
        for (String projectName : projectNames)
            context.deleteFile(projectName + GRAPHME_FILE_SUFFIX);

        // Return the Files
        return loadProjects();
    }

    public ArrayList<DataProject> loadProjects()
    {
        // Return the List of File Projects
        File[] projectList = context.getFilesDir().listFiles();

        // Parse the Project List and Return the Data Projects
        XmlHandler xmlHandler = new XmlHandler();
        ArrayList<DataProject> projectArrayList = xmlHandler.parseXMLFile(projectList);

        // Sort the Data Project by Update Time
        Collections.sort(projectArrayList, new Comparator<DataProject>()
        {
            public int compare(DataProject dataProjectOne, DataProject dataProjectTwo)
            {
                return dataProjectTwo.getUpdatedTime().compareTo(dataProjectOne.getUpdatedTime());
            }
        });

        // Return the Project Array List
        return projectArrayList;
    }
}

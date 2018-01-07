package umich.jakebock.graphme.support_classes;

import android.content.Context;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;

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

    public ArrayList<DataProject> deleteProject(String projectName)
    {
        // Delete the Project
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
        return xmlHandler.parseXMLFile(projectList);
    }
}

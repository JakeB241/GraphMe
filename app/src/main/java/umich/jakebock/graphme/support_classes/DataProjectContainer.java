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

    public boolean createProject(DataProject dataProject)
    {
        try
        {
            // Create the FileName
            String filename = dataProject.getProjectTitle() + GRAPHME_FILE_SUFFIX;

            // If the File Exists, Return False
            for (File file : context.getFilesDir().listFiles())
                if (file.getName().equals(filename))
                    return false;

            FileOutputStream fileOutputStream = context.openFileOutput(filename, Context.MODE_PRIVATE);

            // Create the XML File
            new XmlHandler().createXMLProject(dataProject, fileOutputStream);
        }

        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }

        return true;
    }

    public ArrayList<DataProject> deleteProjects(ArrayList<DataProject> projects)
    {
        // Delete the Projects
        for (DataProject project : projects)
            context.deleteFile(project.getProjectTitle() + GRAPHME_FILE_SUFFIX);

        // Return the Files
        return loadProjects();
    }

    public ArrayList<DataProject> loadProjects()
    {
        // Return the List of File Projects
        File[] projectList = context.getFilesDir().listFiles();

        // Parse the Project List and Return the Data Projects
        ArrayList<DataProject> projectArrayList = new XmlHandler().parseXMLFile(projectList);

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

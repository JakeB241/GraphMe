package umich.jakebock.graphme.support_classes;

import android.content.Context;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import umich.jakebock.graphme.classes.DataProject;

/**
 * Created by Jake on 12/27/2017.
 */

public class DataProjectContainer
{
    private Context context;

    public DataProjectContainer(Context context)
    {
        this.context = context;
    }

    public void createProject(DataProject dataProject)
    {
        try
        {
            String filename = dataProject.getProjectTitle() + ".g4m";
            FileOutputStream fileOutputStream = context.openFileOutput(filename, Context.MODE_APPEND);

            //XmlSerializer serializer = Xml.newSerializer();
            //serializer.setOutput    (fileOutputStream, "UTF-8");
            //serializer.startDocument(null, true);
            //serializer.setFeature   ("http://xmlpull.org/v1/doc/features.html#indent-output", true);
            //serializer.startTag     (null, "root");
            //serializer.endTag       (null, "root");
            //serializer.endDocument();
            //serializer.flush();

            fileOutputStream.close();
        }

        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }

        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public ArrayList<DataProject> loadProjects()
    {
        // Return the List of Projects
        File[] projectList = context.getFilesDir().listFiles();

        // Initialize the ArrayList to Return
        ArrayList<DataProject> dataProjectList = new ArrayList<DataProject>();

        // Read the List of Projects
        for (File project : projectList)
        {
            // Fetch the Project Name
            String projectName = project.getName();

            // Ensure the Project File is a GraphMe File
            if (projectName.endsWith("g4m"))
            {
                // Create the Data Project Object
                dataProjectList.add(new DataProject(projectName.replace(".g4m",""), null));
            }
        }

        // Return the Data Project List
        return dataProjectList;
    }
}

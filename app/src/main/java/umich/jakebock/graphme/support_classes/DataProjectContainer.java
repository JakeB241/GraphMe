package umich.jakebock.graphme.support_classes;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import umich.jakebock.graphme.classes.DataObject;
import umich.jakebock.graphme.classes.DataProject;

/**
 * Created by Jake on 12/27/2017.
 */

public class DataProjectContainer
{
    private static final String GRAPHME_FILE_SUFFIX = ".g4m";
    private static final String DEFAULT_IMAGE_PATH  = "";

    @SuppressLint("SimpleDateFormat")
    private DateFormat dateFormat = new SimpleDateFormat("MM/dd/yy HH:mm:ss");
    private Context context;

    public DataProjectContainer(Context context)
    {
        this.context = context;
    }

    public void createProject(DataProject dataProject, ArrayList<DataObject> dataObjects)
    {
        try
        {
            // Create the File
            String filename = dataProject.getProjectTitle() + GRAPHME_FILE_SUFFIX;
            FileOutputStream fileOutputStream = context.openFileOutput(filename, Context.MODE_APPEND);

            // Start Document
            XmlSerializer serializer = Xml.newSerializer();
            serializer.setOutput    (fileOutputStream, "UTF-8");
            serializer.startDocument(null, true);
            serializer.startTag("", "dataProject");

            // Add the Project Title for the Project
            serializer.startTag     ("", "title");
            serializer.text         (dataProject.getProjectTitle());
            serializer.endTag       ("", "title");

            // Add the Updated Time for the Project
            serializer.startTag     ("", "updateTime");
            serializer.text         (dateFormat.format(new Date()));
            serializer.endTag       ("", "updateTime");

            // Add the Image for the Project
            serializer.startTag     ("", "image");
            String projectImageFilePath = dataProject.getProjectImageFilePath();
            if (projectImageFilePath == null) projectImageFilePath = DEFAULT_IMAGE_PATH;
            serializer.text         (projectImageFilePath);
            serializer.endTag       ("", "image");

            // Add the Data Objects
            for (DataObject dataObject : dataObjects)
            {
                // Add the Image for the Project
                serializer.startTag     ("", "dataObject");
                serializer.text         (dataObject.getObjectInformation());
                serializer.text         (dataObject.getObjectDate().toString());
                serializer.endTag       ("", "dataObject");
            }

            // Close the Document
            serializer.endTag("", "dataProject");
            serializer.endDocument();
            serializer.flush();
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

    public ArrayList<DataProject> deleteProject(String projectName)
    {
        // Delete the Project
        context.deleteFile(projectName + GRAPHME_FILE_SUFFIX);

        // Return the Files
        return loadProjects();
    }

    public ArrayList<DataProject> loadProjects()
    {
        // Return the List of Projects
        File[] projectList = context.getFilesDir().listFiles();

        // Initialize the ArrayList to Return
        ArrayList<DataProject> dataProjectList = new ArrayList<DataProject>();

        // Create the XML Parser
        XmlPullParser parser = Xml.newPullParser();

        // Read the List of Projects
        for (File project : projectList)
        {
            // Read the Project Name
            String projectName = project.getName();
            System.out.println("PROJECT NAME: " + projectName);
            System.out.println("LENGTH OF FILE: " + project.length());

            // Ensure the Project File is a GraphMe File
            if (projectName.endsWith(GRAPHME_FILE_SUFFIX))
            {
                // Create the Project Attributes
                String projectTitle         = null;
                String projectUpdateTime    = null;
                String projectImageFilePath = null;

                try
                {
                    // Set the Input
                    parser.setInput(new FileInputStream(project), null);
                    parser.nextTag();

                    while (parser.next() != XmlPullParser.END_DOCUMENT)
                    {
                        if (parser.getEventType() != XmlPullParser.START_TAG)
                        {
                            continue;
                        }

                        // Switch
                        String name = parser.getName();
                        System.out.println("****NAME: " + name);
                        switch (parser.getName())
                        {
                            case "title":
                                projectTitle            = readTitle(parser);
                                break;
                            case "updateTime":
                                projectUpdateTime       = readUpdateTime(parser);
                                break;
                            case "image":
                                projectImageFilePath    = readImage(parser);
                                break;
                            case "dataObject":
                                break;
                            default:
                                break;
                        }
                    }
                }

                catch (XmlPullParserException e)
                {
                    e.printStackTrace();
                }

                catch (IOException e)
                {
                    e.printStackTrace();
                }

                // Create the Data Project Object
                System.out.println("PROJECT TITLE: " + projectTitle);
                System.out.println("PROJECT UPDATE TIME: " + projectUpdateTime);
                System.out.println("PROJECT IMAGE: " + projectImageFilePath);
                dataProjectList.add(new DataProject(projectTitle, projectImageFilePath, projectUpdateTime));
            }
        }

        // Return the Data Project List
        System.out.println("SIZE OF LIST: " + dataProjectList.size());
        return dataProjectList;
    }

    // Processes title tags in the feed.
    private String readTitle(XmlPullParser parser) throws IOException, XmlPullParserException
    {
        parser.require(XmlPullParser.START_TAG, "", "title");
        String title = readText(parser);
        parser.require(XmlPullParser.END_TAG  , "", "title");
        return title;
    }

    // Processes title tags in the feed.
    private String readUpdateTime(XmlPullParser parser) throws IOException, XmlPullParserException
    {
        parser.require(XmlPullParser.START_TAG, "", "updateTime");
        String updateTime = readText(parser);
        parser.require(XmlPullParser.END_TAG  , "", "updateTime");
        return updateTime;
    }

    // Processes title tags in the feed.
    private String readImage(XmlPullParser parser) throws IOException, XmlPullParserException
    {
        parser.require(XmlPullParser.START_TAG, "", "image");
        String image = readText(parser);
        parser.require(XmlPullParser.END_TAG  , "", "image");
        return image;
    }

    // For the tags title and summary, extracts their text values.
    private String readText(XmlPullParser parser) throws IOException, XmlPullParserException
    {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT)
        {
            System.out.println("READING TEXT");
            result = parser.getText();
            parser.nextTag();
        }

        return result;
    }
}

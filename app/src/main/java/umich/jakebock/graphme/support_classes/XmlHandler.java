package umich.jakebock.graphme.support_classes;

import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import umich.jakebock.graphme.activities.MainActivity;
import umich.jakebock.graphme.classes.DataObject;
import umich.jakebock.graphme.classes.DataProject;

/**
 * Created by Jake on 1/6/2018.
 */

class XmlHandler
{
    // File Suffix
    private String GRAPHME_FILE_SUFFIX = DataProjectContainer.GRAPHME_FILE_SUFFIX;

    // Default Image Path
    private static final String DEFAULT_IMAGE_PATH  = "";

    // XML Tag Constants - Data Project
    private static final String DATA_PROJECT_TAG                = "dataProject";
    private static final String DATA_PROJECT_TITLE_TAG          = "dataProjectTitle";
    private static final String DATA_PROJECT_UPDATE_TIME_TAG    = "dataProjectUpdateTime";
    private static final String DATA_PROJECT_IMAGE_TAG          = "dataProjectImage";

    // XML Tag Constants - Data Object
    private static final String DATA_OBJECT_TAG                 = "dataObject";
    private static final String DATA_OBJECT_UPDATE_TIME_TAG     = "dataObjectUpdateTime";
    private static final String DATA_OBJECT_INFORMATION_TAG     = "dataObjectInformationTextView";

    void createXMLProject(DataProject dataProject, FileOutputStream fileOutputStream)
    {
        try
        {
            // Start Document
            XmlSerializer serializer = Xml.newSerializer();
            serializer.setOutput    (fileOutputStream, "UTF-8");
            serializer.startDocument(null, true);

            // Data Project Start Tag
            serializer.startTag("", DATA_PROJECT_TAG);

            // Add the Project Title for the Project
            writeTag(serializer, DATA_PROJECT_TITLE_TAG, dataProject.getProjectTitle());

            // Add the Updated Time for the Project
            writeTag(serializer, DATA_PROJECT_UPDATE_TIME_TAG, MainActivity.dateFormat.format(new Date()));

            // Add the Image for the Project
            String projectImageFilePath = dataProject.getProjectImageFilePath();
            if (projectImageFilePath == null) projectImageFilePath = DEFAULT_IMAGE_PATH;
            writeTag(serializer, DATA_PROJECT_IMAGE_TAG, projectImageFilePath);

            // Data Object End Tag
            serializer.endTag       ("", DATA_PROJECT_TAG);

            // Add the Data Objects
            for (DataObject dataObject : dataProject.getDataObjectList())
            {
                // Data Object Start Tag
                serializer.startTag     ("", DATA_OBJECT_TAG);

                // Data Object Information
                writeTag(serializer, DATA_OBJECT_INFORMATION_TAG, dataObject.getObjectInformation());

                // Data Object Update Time
                writeTag(serializer, DATA_OBJECT_UPDATE_TIME_TAG, dataObject.getObjectTime());

                // Data Object End Tag
                serializer.endTag       ("", DATA_OBJECT_TAG);
            }

            // Close the Document
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

    ArrayList<DataProject> parseXMLFile(File[] projectList)
    {
        // Initialize the ArrayList to Return
        ArrayList<DataProject> dataProjectList = new ArrayList<DataProject>();

        // Create the XML Parser
        XmlPullParser parser = Xml.newPullParser();

        // Read the List of Projects
        for (File project : projectList)
        {
            // Read the Project Name
            String projectName = project.getName();

            // Ensure the Project File is a GraphMe File
            if (projectName.endsWith(GRAPHME_FILE_SUFFIX))
            {
                // Create the Project Attributes
                String projectTitle         = null;
                String projectUpdateTime    = null;
                String projectImageFilePath = null;

                // Create the Object Attributes
                String objectInformation    = null;
                String objectUpdateTime     = null;

                // Create the Array List of Data Objects
                ArrayList<DataObject> dataObjects = new ArrayList<>();

                try
                {
                    // Set the Input
                    parser.setInput(new FileInputStream(project), null);
                    parser.nextTag();

                    // Loop through the Entire Document
                    while (parser.next() != XmlPullParser.END_DOCUMENT)
                    {
                        // Ensure the Event Type is the Start Tag
                        if (parser.getEventType() == XmlPullParser.START_TAG)
                        {
                            // Switch on the Tags
                            switch (parser.getName())
                            {
                                case DATA_PROJECT_TITLE_TAG:
                                    projectTitle            = readTag(parser);
                                    break;
                                case DATA_PROJECT_UPDATE_TIME_TAG:
                                    projectUpdateTime       = readTag(parser);
                                    break;
                                case DATA_PROJECT_IMAGE_TAG:
                                    projectImageFilePath    = readTag(parser);
                                    break;
                                case DATA_OBJECT_INFORMATION_TAG:
                                    objectInformation       = readTag(parser);
                                    break;
                                case DATA_OBJECT_UPDATE_TIME_TAG:
                                    objectUpdateTime        = readTag(parser);

                                    // Create the Data Object
                                    dataObjects.add(new DataObject(objectInformation, objectUpdateTime));
                                    break;
                                default:
                                    break;
                            }
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
                dataProjectList.add(new DataProject(projectTitle, projectImageFilePath, projectUpdateTime, dataObjects));
            }
        }

        // Return the Data Project List
        return dataProjectList;
    }

    private void writeTag(XmlSerializer serializer, String tag, String information) throws IOException
    {
        serializer.startTag     ("", tag);
        serializer.text         (information);
        serializer.endTag       ("", tag);
    }

    private String readTag(XmlPullParser parser) throws IOException, XmlPullParserException
    {
        // Ensure the Start Tag
        parser.require(XmlPullParser.START_TAG, "", parser.getName());

        // Go to Next
        parser.next();

        // Fetch the Text
        String text = parser.getText();

        // Go to the Next Tag
        parser.nextTag();

        // Ensure the End Tag
        parser.require(XmlPullParser.END_TAG  , "", parser.getName());

        // Return the Text
        return text;
    }

}

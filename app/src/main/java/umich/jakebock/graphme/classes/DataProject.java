package umich.jakebock.graphme.classes;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

/**
 * Created by Jake on 12/10/2017.
 */

public class DataProject implements Serializable
{
    private String                   projectTitle;
    private String                   updatedTime;
    private String                   projectImageFilePath;
    private ArrayList<DataObject>    dataObjectList;
    private HashMap<String, Setting> dataProjectSettings;

    // Date Formatting
    public static DateFormat dateFormat = new SimpleDateFormat("M/d/yy h:mm a", Locale.US);

    public DataProject(String projectTitle, String projectImageFilePath)
    {
        this.projectTitle           = projectTitle;
        this.projectImageFilePath   = projectImageFilePath;
        this.updatedTime            = prependUpdatedLabel(returnCurrentTime());
        this.dataObjectList         = new ArrayList<>();
    }

    public DataProject(String projectTitle, String projectImageFilePath, HashMap<String, Setting> dataProjectSettings)
    {
        this.projectTitle           = projectTitle;
        this.projectImageFilePath   = projectImageFilePath;
        this.dataProjectSettings    = dataProjectSettings;
        this.updatedTime            = prependUpdatedLabel(returnCurrentTime());
        this.dataObjectList         = new ArrayList<>();
    }

    public DataProject(String projectTitle, String projectImageFilePath, ArrayList<DataObject> dataObjectList)
    {
        this.projectTitle           = projectTitle;
        this.projectImageFilePath   = projectImageFilePath;
        this.updatedTime            = prependUpdatedLabel(returnCurrentTime());
        this.dataObjectList         = dataObjectList;
    }

    public DataProject(String projectTitle, String projectImageFilePath, String updatedTime, ArrayList<DataObject> dataObjectList)
    {
        this.projectTitle           = projectTitle;
        this.projectImageFilePath   = projectImageFilePath;
        this.updatedTime            = prependUpdatedLabel(updatedTime);
        this.dataObjectList         = dataObjectList;
    }

    private String returnCurrentTime()
    {
        return dateFormat.format(new Date());
    }

    private String prependUpdatedLabel(String updatedTime)
    {
        return "Updated: " + updatedTime;
    }

    public Bitmap returnBitmapImage()
    {
        return BitmapFactory.decodeFile(projectImageFilePath);
    }

    public String returnNumberOfDataObjectsWithLabel()
    {
        return "Entries: " + dataObjectList.size();
    }

    // Begin Getters/Setters
    public ArrayList<DataObject> getDataObjectList() {
        return dataObjectList;
    }

    public void setDataObjectList(ArrayList<DataObject> dataObjectList) {
        this.dataObjectList = dataObjectList;
    }

    public String getProjectTitle() {
        return projectTitle;
    }

    public void setProjectTitle(String projectTitle) {
        this.projectTitle = projectTitle;
    }

    public String getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(String updatedTime) {
        this.updatedTime = updatedTime;
    }

    public String getProjectImageFilePath() {
        return projectImageFilePath;
    }

    public void setProjectImageFilePath(String projectImage) {
        this.projectImageFilePath = projectImage;
    }

    public HashMap<String, Setting> getDataProjectSettings() {
        return dataProjectSettings;
    }
}

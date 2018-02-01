package umich.jakebock.trackme.classes;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.google.firebase.firestore.ServerTimestamp;

import java.io.Serializable;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

import umich.jakebock.trackme.activities.MainActivity;

/**
 * Created by Jake on 12/10/2017.
 */

public class DataProject implements Serializable
{
    private String                   projectTitle;
    private @ServerTimestamp Date    updatedTime;
    private String                   projectImageFilePath;
    private ArrayList<DataObject>    dataObjectList;
    //private HashMap<String, Setting> dataProjectSettings;

    public DataProject() {}

    public DataProject(String projectTitle, String projectImageFilePath)
    {
        this.projectTitle           = projectTitle;
        this.projectImageFilePath   = projectImageFilePath;
        this.updatedTime            = new Date();
        this.dataObjectList         = new ArrayList<>();
    }

    public String returnDateString()
    {
        return MainActivity.dateFormat.format(updatedTime);
    }

    public void setDateAsString(String date) {
        try
        {
            updatedTime = MainActivity.dateFormat.parse(date);
        }

        catch (ParseException e)
        {
            e.printStackTrace();
        }
    }

    public Bitmap returnBitmapImage()
    {
        return BitmapFactory.decodeFile(projectImageFilePath);
    }

    public String returnNumberOfDataObjectsWithLabel() {
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

    public Date getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(Date updatedTime) {
        this.updatedTime = updatedTime;
    }

    public String getProjectImageFilePath() {
        return projectImageFilePath;
    }

    public void setProjectImageFilePath(String projectImage) {
        this.projectImageFilePath = projectImage;
    }

    /*public HashMap<String, Setting> getDataProjectSettings() {
        return dataProjectSettings;
    }*/
}

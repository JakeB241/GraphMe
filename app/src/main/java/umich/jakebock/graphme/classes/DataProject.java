package umich.jakebock.graphme.classes;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Jake on 12/10/2017.
 */

public class DataProject
{
    private ArrayList<DataObject> dataObjectList;
    private String                projectTitle;
    private String                updatedTime;
    private Bitmap                projectImage;

    @SuppressLint("SimpleDateFormat")
    private DateFormat dateFormat = new SimpleDateFormat("MM/dd/yy HH:mm:ss");

    public DataProject(String projectTitle, Bitmap projectImage)
    {
        this.projectTitle   = projectTitle;
        this.projectImage   = projectImage;
        this.dataObjectList = new ArrayList<DataObject>();
        this.updatedTime    = returnCurrentTimeWithLabel();
    }

    private String returnCurrentTimeWithLabel() {
        return "Updated: " + dateFormat.format(new Date());
    }

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

    public Bitmap getProjectImage() {
        return projectImage;
    }

    public void setProjectImage(Bitmap projectImage) {
        this.projectImage = projectImage;
    }
}

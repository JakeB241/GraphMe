package umich.jakebock.graphme.classes;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Jake on 12/10/2017.
 */

public class DataObject implements Serializable
{
    private String objectInformation;
    private String objectTime;

    public DataObject()
    {
        this.objectInformation = "";
        this.objectTime        = returnCurrentTime();
    }

    public DataObject(String objectInformation)
    {
        this.objectInformation = objectInformation;
        this.objectTime        = returnCurrentTime();
    }

    public DataObject(String objectInformation, String updatedTime)
    {
        this.objectInformation = objectInformation;
        this.objectTime        = updatedTime;
    }

    private String returnCurrentTime()
    {
        return DataProject.dateFormat.format(new Date());
    }

    // Begin Getters/Setters
    public String getObjectTime() {
        return objectTime;
    }

    public String getObjectInformation() {
        return objectInformation;
    }

    public void setObjectTime(String objectTime) {
        this.objectTime = objectTime;
    }

    public void setObjectInformation(String objectInformation) {
        this.objectInformation = objectInformation;
    }
}

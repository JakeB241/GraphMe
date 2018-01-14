package umich.jakebock.graphme.classes;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Jake on 12/10/2017.
 */

public class DataObject implements Serializable
{
    private String  objectInformation;
    private String  updatedTime;

    public DataObject()
    {
        this.objectInformation = "";
        this.updatedTime       = returnCurrentTime();
    }

    public DataObject(String objectInformation)
    {
        this.objectInformation = objectInformation;
        this.updatedTime       = returnCurrentTime();
    }

    public DataObject(String objectInformation, String updatedTime)
    {
        this.objectInformation = objectInformation;
        this.updatedTime       = updatedTime;
    }

    private String returnCurrentTime()
    {
        return DataProject.dateFormat.format(new Date());
    }

    // Begin Getters/Setters
    public String getUpdatedTime() {
        return updatedTime;
    }

    public String getObjectInformation() {
        return objectInformation;
    }

    public void setUpdatedTime(String updatedTime) {
        this.updatedTime = updatedTime;
    }

    public void setObjectInformation(String objectInformation) {
        this.objectInformation = objectInformation;
    }
}

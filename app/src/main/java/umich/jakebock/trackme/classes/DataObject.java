package umich.jakebock.trackme.classes;

import java.io.Serializable;
import java.text.ParseException;
import java.util.Date;

import umich.jakebock.trackme.activities.MainActivity;

/**
 * Created by Jake on 12/10/2017.
 */

public class DataObject implements Serializable
{
    private String objectInformation;
    private Date   objectTime;

    public DataObject(String objectInformation, Date updatedTime)
    {
        this.objectInformation = objectInformation;
        this.objectTime        = updatedTime;
    }

    public DataObject(String objectInformation, String updatedTime)
    {
        this.objectInformation = objectInformation;
        setDateAsString(updatedTime);
    }

    public String returnDateString()
    {
        return MainActivity.dateFormat.format(objectTime);
    }

    public void setDateAsString(String date) {
        try
        {
            objectTime = MainActivity.dateFormat.parse(date);
        }

        catch (ParseException e)
        {
            e.printStackTrace();
        }
    }

    // Begin Getters/Setters
    public Date getObjectTime() {
        return objectTime;
    }

    public String getObjectInformation() {
        return objectInformation;
    }

    public void setObjectTime(Date objectTime) {
        this.objectTime = objectTime;
    }

    public void setObjectInformation(String objectInformation) {
        this.objectInformation = objectInformation;
    }
}

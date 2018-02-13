package umich.jakebock.trackme.classes;

import com.google.firebase.firestore.ServerTimestamp;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Jake on 12/10/2017.
 */

public class DataObject implements Serializable
{
    private                  String objectInformation;
    private @ServerTimestamp Date   objectTime;

    public DataObject() {}

    public DataObject(String objectInformation, Date objectTime)
    {
        this.objectInformation = objectInformation;
        this.objectTime        = objectTime;
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

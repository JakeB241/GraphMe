package umich.jakebock.trackme.classes;

import com.google.firebase.firestore.ServerTimestamp;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Date;

/**
 * Created by Jake on 12/10/2017.
 */

public class DataObject implements Serializable
{
    private                  String objectInformation;
    private @ServerTimestamp Date   objectTime;
    private                  String objectNote;

    public DataObject() {}

    public DataObject(String objectInformation, Date objectTime)
    {
        this.objectInformation = objectInformation;
        this.objectTime        = objectTime;
        this.objectNote        = null;
    }

    public DataObject(String objectInformation, Date objectTime, String objectNote)
    {
        this.objectInformation = objectInformation;
        this.objectTime        = objectTime;
        this.objectNote        = objectNote;
    }

    // Begin Getters/Setters
    public Date getObjectTime() {
        return objectTime;
    }

    public String getObjectInformation() {
        return objectInformation;
    }

    public String getObjectNote() {
        return objectNote;
    }

    public void setObjectTime(Date objectTime) {
        this.objectTime = objectTime;
    }

    public void setObjectInformation(String objectInformation) {
        this.objectInformation = objectInformation;
    }

    public void setObjectNote(String objectNote) {
        this.objectNote = objectNote;
    }

    // Comparators
    public static Comparator<DataObject> sortDescendingOrder = new Comparator<DataObject>()
    {
        @Override
        public int compare(DataObject dataObject1, DataObject dataObject2)
        {
            return dataObject2.getObjectTime().compareTo(dataObject1.getObjectTime());
        }
    };

    public static Comparator<DataObject> sortAscendingOrder = new Comparator<DataObject>()
    {
        @Override
        public int compare(DataObject dataObject1, DataObject dataObject2)
        {
            return dataObject1.getObjectTime().compareTo(dataObject2.getObjectTime());
        }
    };
}

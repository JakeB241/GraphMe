package umich.jakebock.graphme.classes;

import java.util.Date;

/**
 * Created by Jake on 12/10/2017.
 */

public class DataObject
{
    private Date   objectDate;
    private String objectInformation;

    public Date getObjectDate() {
        return objectDate;
    }

    public String getObjectInformation() {
        return objectInformation;
    }

    public void setObjectDate(Date objectDate) {
        this.objectDate = objectDate;
    }

    public void setObjectInformation(String objectInformation) {
        this.objectInformation = objectInformation;
    }
}

package umich.jakebock.trackme.classes;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.provider.MediaStore;

import com.google.firebase.firestore.ServerTimestamp;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
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

    // Helper Functions
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

    public Uri returnImageURI()
    {
        return Uri.parse(projectImageFilePath);
    }

    public String returnNumberOfDataObjectsWithLabel() {
        return "Entries: " + dataObjectList.size();
    }

    private static int getOrientation(Context context, Uri photoUri)
    {
        Cursor cursor = context.getContentResolver().query(photoUri, new String[] { MediaStore.Images.ImageColumns.ORIENTATION }, null, null, null);

        if (cursor.getCount() != 1)
        {
            return -1;
        }

        cursor.moveToFirst();
        return cursor.getInt(0);
    }

    public static Bitmap returnCorrectlyOrientedImage(Context context, Uri photoUri)
    {
        InputStream is = null;
        try
        {
            is = context.getContentResolver().openInputStream(photoUri);
            BitmapFactory.Options dbo = new BitmapFactory.Options();
            dbo.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(is, null, dbo);
            is.close();

            int rotatedWidth, rotatedHeight;
            int orientation = getOrientation(context, photoUri);

            if (orientation == 90 || orientation == 270) {
                rotatedWidth  = dbo.outHeight;
                rotatedHeight = dbo.outWidth;
            } else {
                rotatedWidth  = dbo.outWidth;
                rotatedHeight = dbo.outHeight;
            }

            Bitmap srcBitmap;
            is = context.getContentResolver().openInputStream(photoUri);
            if (rotatedWidth > 512 || rotatedHeight > 512)
            {
                float widthRatio = ((float) rotatedWidth) / ((float) 512);
                float heightRatio = ((float) rotatedHeight) / ((float) 512);
                float maxRatio = Math.max(widthRatio, heightRatio);

                // Create the bitmap from file
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = (int) maxRatio;
                srcBitmap = BitmapFactory.decodeStream(is, null, options);
            }

            else
            {
                srcBitmap = BitmapFactory.decodeStream(is);
            }
            is.close();

            if (orientation > 0) {
                Matrix matrix = new Matrix();
                matrix.postRotate(orientation);

                srcBitmap = Bitmap.createBitmap(srcBitmap, 0, 0, srcBitmap.getWidth(), srcBitmap.getHeight(), matrix, true);
            }

            return srcBitmap;
        }

        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }

        catch (IOException e)
        {
            e.printStackTrace();
        }

        return null;
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

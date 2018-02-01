package umich.jakebock.trackme.support_classes;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import umich.jakebock.trackme.R;
import umich.jakebock.trackme.classes.DataProject;

/**
 * Created by Jake on 12/19/2017.
 */

public class DataProjectListAdapter extends ArrayAdapter<DataProject>
{
    private Context context;

    // Data Project View Holder
    private static class DataProjectViewHolder
    {
        ImageView   projectImage;
        TextView    projectName;
        TextView    lastUpdatedTime;
        TextView    dataObjectNumber;
    }

    public DataProjectListAdapter(Context context)
    {
        // Call the Super
        super(context, R.layout.data_project_item);

        // Initialize Data
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent)
    {
        // Get the data item for this position
        DataProject dataProject = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        DataProjectViewHolder dataProjectViewHolder;

        if (convertView == null)
        {
            dataProjectViewHolder = new DataProjectViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.data_project_item, parent, false);
            dataProjectViewHolder.projectImage      = (ImageView) convertView.findViewById(R.id.project_image);
            dataProjectViewHolder.projectName       = (TextView)  convertView.findViewById(R.id.project_name);
            dataProjectViewHolder.lastUpdatedTime   = (TextView)  convertView.findViewById(R.id.updated_time);
            dataProjectViewHolder.dataObjectNumber  = (TextView)  convertView.findViewById(R.id.data_object_number);
            convertView.setTag(dataProjectViewHolder);
        }

        else
        {
            dataProjectViewHolder = (DataProjectViewHolder) convertView.getTag();
        }

        // Ensure a Data Project is Found
        if (dataProject != null)
        {
            // Create the Updated String
            String updatedTimeString = "Updated: " + dataProject.returnDateString();

            // Set the View Parameters
            dataProjectViewHolder.projectName       .setText(dataProject.getProjectTitle());
            dataProjectViewHolder.lastUpdatedTime   .setText(updatedTimeString);
            dataProjectViewHolder.dataObjectNumber  .setText(dataProject.returnNumberOfDataObjectsWithLabel());

            // Fetch the Image
            Bitmap projectImage = dataProject.returnBitmapImage();
            if (projectImage != null) dataProjectViewHolder.projectImage.setImageBitmap(projectImage);
        }

        // Return the Completed View
        return convertView;
    }
}

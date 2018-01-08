package umich.jakebock.graphme.support_classes;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import umich.jakebock.graphme.R;
import umich.jakebock.graphme.classes.DataProject;

/**
 * Created by Jake on 12/19/2017.
 */

public class ProjectListAdapter extends ArrayAdapter<DataProject> implements View.OnClickListener
{
    private ArrayList<DataProject>  dataSet;
    Context                         context;

    // View lookup cache
    private static class ProjectViewHolder
    {
        ImageView projectImage;
        TextView  projectName;
        TextView  lastUpdatedTime;
    }

    public ProjectListAdapter(ArrayList<DataProject> data, Context context)
    {
        super(context, R.layout.project_item, data);
        this.dataSet  = data;
        this.context = context;
    }

    @Override
    public void onClick(View v)
    {
        DataProject dataProject = (DataProject) getItem((Integer) v.getTag());
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent)
    {
        // Get the data item for this position
        DataProject dataProject = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        ProjectViewHolder projectViewHolder; // view lookup cache stored in tag

        if (convertView == null)
        {
            projectViewHolder = new ProjectViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.project_item, parent, false);
            projectViewHolder.projectImage      = (ImageView) convertView.findViewById(R.id.project_image);
            projectViewHolder.projectName       = (TextView)  convertView.findViewById(R.id.project_name);
            projectViewHolder.lastUpdatedTime   = (TextView)  convertView.findViewById(R.id.updated_time);
            convertView.setTag(projectViewHolder);
        }

        else
        {
            projectViewHolder = (ProjectViewHolder) convertView.getTag();
        }

        projectViewHolder.projectName.setText(dataProject.getProjectTitle());
        projectViewHolder.lastUpdatedTime.setText(dataProject.getUpdatedTime());
        projectViewHolder.projectImage.setImageBitmap(dataProject.returnBitmapImage());
        //projectViewHolder.info.setOnClickListener(this);
        //projectViewHolder.info.setTag(position);
        // Return the completed view to render on screen
        return convertView;
    }
}

package umich.jakebock.graphme.support_classes;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import java.util.ArrayList;
import umich.jakebock.graphme.R;
import umich.jakebock.graphme.classes.DataProject;

/**
 * Created by Jake on 12/11/2017.
 */

public class DataProjectListAdapter extends BaseAdapter
{
    private Context                context;
    private ArrayList<DataProject> dataProjectsList;
    private LayoutInflater         layoutInflater;

    public DataProjectListAdapter(Activity context, ArrayList<DataProject> dataProjectsList)
    {
        this.context          = context;
        this.dataProjectsList = dataProjectsList;
        layoutInflater        = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return dataProjectsList.size();
    }

    @Override
    public Object getItem(int position) {
        return dataProjectsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int i, View view, ViewGroup parent) {
        return layoutInflater.inflate(R.layout.data_project_row, parent, false);
    }
}

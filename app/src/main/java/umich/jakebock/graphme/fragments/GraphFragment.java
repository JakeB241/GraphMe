package umich.jakebock.graphme.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import umich.jakebock.graphme.R;
import umich.jakebock.graphme.activities.MainActivity;
import umich.jakebock.graphme.classes.DataProject;


public class GraphFragment extends Fragment
{
    private View        rootView;
    private DataProject currentDataProject;

    public GraphFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        rootView = inflater.inflate(R.layout.fragment_graph, container, false);

        // Fetch the Data Project
        currentDataProject = ((MainActivity)getActivity()).getCurrentDataProject();

        return rootView;
    }
}

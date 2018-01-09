package umich.jakebock.graphme.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import umich.jakebock.graphme.R;


public class StatisticsFragment extends Fragment
{
    private View rootView;

    public StatisticsFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        rootView = inflater.inflate(R.layout.fragment_statistics, container, false);
        return rootView;
    }
}
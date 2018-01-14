package umich.jakebock.graphme.support_classes;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import umich.jakebock.graphme.classes.DataProject;
import umich.jakebock.graphme.fragments.GraphFragment;
import umich.jakebock.graphme.fragments.ListFragment;
import umich.jakebock.graphme.fragments.StatisticsFragment;

/**
 * Created by Jake on 12/9/2017.
 */

public class TabFragmentPagerAdapter extends FragmentStatePagerAdapter
{
    public  static final int LIST_TAB_POSITION         = 0;
    public  static final int STATISTICS_TAB_POSITION   = 1;
    public  static final int GRAPH_TAB_POSITION        = 2;
    private static final int NUMBER_OF_TABS            = 3;

    private DataProject dataProject;

    public TabFragmentPagerAdapter(FragmentManager fragmentManager, DataProject dataProject)
    {
        super(fragmentManager);

        // Initialize the Data Project
        this.dataProject = dataProject;
    }

    // This determines the fragment for each tab
    @Override
    public Fragment getItem(int position)
    {
        switch (position)
        {
            case LIST_TAB_POSITION:         return returnFragmentWithBundle(new ListFragment());
            case STATISTICS_TAB_POSITION:   return returnFragmentWithBundle(new StatisticsFragment());
            case GRAPH_TAB_POSITION:        return returnFragmentWithBundle(new GraphFragment());
            default:                        return null;
        }
    }

    // This determines the number of tabs
    @Override
    public int getCount()
    {
        return NUMBER_OF_TABS;
    }

    private Fragment returnFragmentWithBundle(Fragment fragmentActivity)
    {
        // Create the Bundle
        Bundle bundle = new Bundle();

        // Add the Data Project to the Bundle
        bundle.putSerializable("DATA_PROJECT", dataProject);

        // Add the Data Project to the Fragments
        fragmentActivity.setArguments(bundle);

        // Return the Fragment
        return fragmentActivity;
    }
}

package umich.jakebock.trackme.support_classes;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import umich.jakebock.trackme.fragments.GraphFragment;
import umich.jakebock.trackme.fragments.ListFragment;

/**
 * Created by Jake on 12/9/2017.
 */

public class TabFragmentPagerAdapter extends FragmentStatePagerAdapter
{
    public  static final int LIST_TAB_POSITION         = 0;
    //public  static final int STATISTICS_TAB_POSITION   = 1;
    public  static final int GRAPH_TAB_POSITION        = 1;
    private static final int NUMBER_OF_TABS            = 2;

    public TabFragmentPagerAdapter(FragmentManager fragmentManager)
    {
        super(fragmentManager);
    }

    // This determines the fragment for each tab
    @Override
    public Fragment getItem(int position)
    {
        switch (position)
        {
            case LIST_TAB_POSITION:         return new ListFragment();
            //case STATISTICS_TAB_POSITION:   return new StatisticsFragment();
            case GRAPH_TAB_POSITION:        return new GraphFragment();
            default:                        return null;
        }
    }

    // This determines the number of tabs
    @Override
    public int getCount()
    {
        return NUMBER_OF_TABS;
    }
}

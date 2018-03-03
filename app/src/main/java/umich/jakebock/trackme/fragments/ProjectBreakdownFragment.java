package umich.jakebock.trackme.fragments;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import umich.jakebock.trackme.R;
import umich.jakebock.trackme.activities.MainActivity;
import umich.jakebock.trackme.support_classes.TabFragmentPagerAdapter;


public class ProjectBreakdownFragment extends Fragment
{
    private View        rootView;

    public ProjectBreakdownFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_project_breakdown, container, false);

        // Initialize the Toolbar
        initializeToolbar();

        // Initialize the Tab Layout
        initializeTabLayout();

        // Return the Root View
        return rootView;
    }

    private void initializeTabLayout()
    {
        // Create the Fragment Pager Adapter
        TabFragmentPagerAdapter adapter = new TabFragmentPagerAdapter(getChildFragmentManager());

        // Initialize the Tab Layout
        TabLayout tabLayout = rootView.findViewById(R.id.tab_layout);

        // Initialize the View Pager
        ViewPager viewPager = rootView.findViewById(R.id.view_pager);

        // Set the adapter onto the view pager
        viewPager.setAdapter(adapter);

        // Give the TabLayout the ViewPager
        tabLayout.setupWithViewPager(viewPager);

        // Set the Selected Tab Indicator Color
        tabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.colorPrimary));

        // Fetch the Tabs
        TabLayout.Tab listTab       = tabLayout.getTabAt(TabFragmentPagerAdapter.LIST_TAB_POSITION       );
        //TabLayout.Tab statisticsTab = tabLayout.getTabAt(TabFragmentPagerAdapter.STATISTICS_TAB_POSITION );
        TabLayout.Tab graphTab      = tabLayout.getTabAt(TabFragmentPagerAdapter.GRAPH_TAB_POSITION      );

        // Add the TabLayout Icons
        if (listTab         != null) listTab      .setIcon(R.drawable.list_icon      );
        //if (statisticsTab   != null) statisticsTab.setIcon(R.drawable.statistics_icon);
        if (graphTab        != null) graphTab     .setIcon(R.drawable.graph_icon     );

        // Ensure that the ListTab is Selected First
        if (listTab != null) listTab.select();
    }

    private void initializeToolbar()
    {
        // Fetch the Action Bar
        ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();

        // Set the Action Bar Title to the Current Data Project Title
        if (actionBar != null) actionBar.setTitle(((MainActivity) getActivity()).getCurrentDataProject().getProjectTitle());
    }
}

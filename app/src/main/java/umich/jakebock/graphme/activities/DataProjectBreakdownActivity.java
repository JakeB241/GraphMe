package umich.jakebock.graphme.activities;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ListView;

import umich.jakebock.graphme.R;
import umich.jakebock.graphme.support_classes.DataObjectBreakdownFragmentPagerAdapter;
import umich.jakebock.graphme.support_classes.MyNavigationDrawer;
import umich.jakebock.graphme.support_classes.MyToolBar;

public class DataProjectBreakdownActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.data_project_breakdown_activity);

        // Create the Navigation Drawer
        createNavigationDrawer();

        // Create the ToolBar
        createToolbar();

        // Create the Tab Layout
        createTabLayout();
    }

    private void createNavigationDrawer()
    {
        // Create the Custom Navigation Drawer
        MyNavigationDrawer navigationDrawer = new MyNavigationDrawer(this, (ListView) findViewById(R.id.navigation_drawer));

        navigationDrawer.initializeNavigationDrawer();
    }

    private void createToolbar()
    {
        // Set the Support Action Bar
        setSupportActionBar((Toolbar)findViewById(R.id.toolbar));

        // Create the Custom Toolbar
        MyToolBar toolbar = new MyToolBar(getSupportActionBar(), getResources().getString(R.string.toolbar_project_title));

        // Initialize the Toolbar
        toolbar.initializeToolbar();

        // Set the Title with the Current Project
        // TODO
    }

    private void createTabLayout()
    {
        // Create the Tab Layout
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);

        // Create the ViewPager
        ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager);

        // Create the Fragment Pager Adapter
        DataObjectBreakdownFragmentPagerAdapter adapter = new DataObjectBreakdownFragmentPagerAdapter(this, getSupportFragmentManager());

        // Set the adapter onto the view pager
        viewPager.setAdapter(adapter);

        // Give the TabLayout the ViewPager
        tabLayout.setupWithViewPager(viewPager);

        // Set the Selected Tab Indicator Color
        tabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.colorPrimary));

        // Fetch the Tabs
        TabLayout.Tab listTab       = tabLayout.getTabAt(DataObjectBreakdownFragmentPagerAdapter.LIST_TAB_POSITION       );
        TabLayout.Tab statisticsTab = tabLayout.getTabAt(DataObjectBreakdownFragmentPagerAdapter.STATISTICS_TAB_POSITION );
        TabLayout.Tab graphTab      = tabLayout.getTabAt(DataObjectBreakdownFragmentPagerAdapter.GRAPH_TAB_POSITION      );

        // Add the TabLayout Icons
        listTab      .setIcon(R.drawable.list_icon      );
        statisticsTab.setIcon(R.drawable.statistics_icon);
        graphTab     .setIcon(R.drawable.graph_icon);

        // Ensure that the ListTab is Selected First
        listTab.select();
    }
}

package umich.jakebock.graphme.activities;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RelativeLayout;

import umich.jakebock.graphme.R;
import umich.jakebock.graphme.fragments.ProjectEditorFragment;
import umich.jakebock.graphme.support_classes.TabFragmentPagerAdapter;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener
{
    private RelativeLayout  contentMain;
    private DrawerLayout    drawerLayout;
    private Toolbar         toolbar;
    private TabLayout       tabLayout;
    private ViewPager       viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        // Call the Constructor
        super.onCreate(savedInstanceState);

        // Set the Content View
        setContentView(R.layout.activity_main);

        // Initialize Views
        initializeViews();

        // Create the Navigation Drawer
        initializeNavigationDrawer();

        // Initialize the ToolBar
        initializeToolbar();

        // If there isn't any saved data, start on the Project Editor Page
        if (savedInstanceState == null)
        {
            // Start the Project Editor Fragment
            getSupportFragmentManager().beginTransaction().add(R.id.content_main, new ProjectEditorFragment()).commit();
        }

        else
        {
            // Create the Tab Layout
            initializeTabLayout();
        }
    }

    private void initializeViews()
    {
        // Initialize all of the Views
        drawerLayout    = findViewById(R.id.drawer_layout);
        toolbar         = findViewById(R.id.toolbar);
        tabLayout       = findViewById(R.id.tab_layout);
        viewPager       = findViewById(R.id.view_pager);
        contentMain     = findViewById(R.id.content_main);
    }

    private void initializeNavigationDrawer()
    {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void initializeToolbar()
    {
        // Set the Support Action Bar
        setSupportActionBar(toolbar);

        // Fetch the Support Action Toolbar
        ActionBar actionBar = getSupportActionBar();

        // Set the Title
        actionBar.setTitle(getResources().getString(R.string.toolbar_project_title));

        // Set the Menu Frame
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);

        // Set the Menu Frame
        actionBar.setDisplayHomeAsUpEnabled(true);

        // Set the Action Bar Toggle
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // TODO Set the Title with the Current Project
    }

    private void initializeTabLayout()
    {
        // Create the Fragment Pager Adapter
        TabFragmentPagerAdapter adapter = new TabFragmentPagerAdapter(getSupportFragmentManager());

        // Set the adapter onto the view pager
        viewPager.setAdapter(adapter);

        // Give the TabLayout the ViewPager
        tabLayout.setupWithViewPager(viewPager);

        // Set the Selected Tab Indicator Color
        tabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.colorPrimary));

        // Fetch the Tabs
        TabLayout.Tab listTab       = tabLayout.getTabAt(TabFragmentPagerAdapter.LIST_TAB_POSITION       );
        TabLayout.Tab statisticsTab = tabLayout.getTabAt(TabFragmentPagerAdapter.STATISTICS_TAB_POSITION );
        TabLayout.Tab graphTab      = tabLayout.getTabAt(TabFragmentPagerAdapter.GRAPH_TAB_POSITION      );

        // Add the TabLayout Icons
        listTab      .setIcon(R.drawable.list_icon      );
        statisticsTab.setIcon(R.drawable.statistics_icon);
        graphTab     .setIcon(R.drawable.graph_icon     );

        // Ensure that the ListTab is Selected First
        listTab.select();
    }

    @Override
    public void onBackPressed()
    {
        if (drawerLayout.isDrawerOpen(GravityCompat.START))
        {
            drawerLayout.closeDrawer(GravityCompat.START);
        }

        else
        {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item)
    {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}

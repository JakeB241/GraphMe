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
import android.view.MenuItem;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import umich.jakebock.graphme.R;
import umich.jakebock.graphme.classes.DataProject;
import umich.jakebock.graphme.fragments.ProjectEditorFragment;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener
{
    private DrawerLayout    drawerLayout;
    private Toolbar         toolbar;
    private TabLayout       tabLayout;
    private ViewPager       viewPager;
    private AdView          adView;

    private DataProject     currentDataProject;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        // Call the Constructor
        super.onCreate(savedInstanceState);

        // Set the Content View
        setContentView(R.layout.activity_main);

        // Initialize the Ads
        initializeAds();

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
    }

    public DataProject getCurrentDataProject()
    {
        return currentDataProject;
    }

    public void setCurrentProject(DataProject dataProject)
    {
        currentDataProject = dataProject;
    }

    private void initializeNavigationDrawer()
    {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void initializeToolbar()
    {
        // Initialize the Views
        drawerLayout    = findViewById(R.id.drawer_layout);
        toolbar         = findViewById(R.id.toolbar);

        // Set the Support Action Bar
        setSupportActionBar(toolbar);

        // Fetch the Support Action Toolbar
        ActionBar actionBar = getSupportActionBar();

        // Ensure the Action Bar is NOT Null
        if (actionBar != null)
        {
            // Set the Menu Frame
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);

            // Set the Menu Frame
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // Set the Action Bar Toggle
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    private void initializeAds()
    {
        MobileAds.initialize(this, "ca-app-pub-9526664903701522/3045069586");
        adView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
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
    public boolean onNavigationItemSelected(MenuItem item)
    {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}

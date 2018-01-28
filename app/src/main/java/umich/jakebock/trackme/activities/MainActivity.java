package umich.jakebock.trackme.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

import umich.jakebock.trackme.R;
import umich.jakebock.trackme.classes.DataProject;
import umich.jakebock.trackme.fragments.ProjectEditorFragment;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener
{
    private DrawerLayout    drawerLayout;
    private DataProject     currentDataProject;

    public static DateFormat dateFormat = new SimpleDateFormat("M/d/yy h:mm a", Locale.US);

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
        Toolbar toolbar = findViewById(R.id.toolbar);

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
        AdView adView = findViewById(R.id.adView);
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
    public boolean onNavigationItemSelected(@NonNull MenuItem item)
    {
        // Handle navigation view item clicks here
        switch (item.getItemId())
        {
            // Log the User Out
            case R.id.log_out:
                logOut();
                break;
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void logOut()
    {
        // Log the User Out from FireBase
        FirebaseAuth.getInstance().signOut();

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build();

        // Sign out User from Google
        GoogleSignIn.getClient(this, gso).signOut().addOnCompleteListener(this, new OnCompleteListener<Void>()
        {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {
                // Show the Login Activity
                startActivity(new Intent(MainActivity.this, LoginActivity.class));

                // Remove History
                finish();
            }
        });
    }
}

package umich.jakebock.trackme.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
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
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import umich.jakebock.trackme.R;
import umich.jakebock.trackme.classes.DataObject;
import umich.jakebock.trackme.classes.DataProject;
import umich.jakebock.trackme.classes.Setting;
import umich.jakebock.trackme.fragments.GraphFragment;
import umich.jakebock.trackme.fragments.ProjectEditorFragment;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener
{
    private DrawerLayout drawerLayout;
    private DataProject  currentDataProject;

    public static ArrayList<Setting> settingsList;

    private static final int FILE_SELECT_CODE = 0;

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

        // Initalize the Settings for this Version of the Application
        initializeAppSettings();

        // Start the Project Editor Fragment
        startProjectEditorFragment();
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
        MobileAds.initialize(this, "ca-app-pub-9526664903701522~5124438011");
        AdView adView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
    }

    private void initializeAppSettings()
    {
        // Initalize the Settings List
        settingsList = new ArrayList<>();

        // Setting for Time Enabled Data Project
        settingsList.add(new Setting("INCLUDE_TIME", "Include Time in Date", Setting.SettingType.SWITCH, false, null));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        switch (requestCode)
        {
            case FILE_SELECT_CODE:
                if (resultCode == RESULT_OK)
                {
                    try
                    {
                        // Get the Uri of the selected file
                        Uri uri = data.getData();

                        // Ensure the URI is not null
                        if (uri != null)
                        {
                            // Create the Input Stream
                            InputStream inputStream = getContentResolver().openInputStream(uri);

                            // Ensure the Input Stream was not null
                            if (inputStream != null)
                            {
                                // Create the Buffered Reader
                                BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));

                                // Create the List of Possible Formats
                                List<DateFormat> possibleFormats = Arrays.asList(DataProject.dateWithoutTime, DataProject.dateWithTime);

                                // Create the Data Object List
                                ArrayList<DataObject> dataObjects = new ArrayList<>();

                                // Read the First Line (Title)
                                String dataProjectTitle = br.readLine();

                                // Read the File
                                String line;
                                while ((line = br.readLine()) != null)
                                {
                                    // Ensure there is Text in the Line
                                    if (line.trim().length() > 0)
                                    {
                                        try
                                        {
                                            // Split the Line by One or More Spaces
                                            String[] stringTokens = line.split(" +");

                                            // Match the First Token
                                            String dataObjectInformationString = stringTokens[0];
                                            String dataObjectDateString        = stringTokens[1];

                                            try
                                            {
                                                // Loop through the Possible Formats
                                                Date dataObjectDate;
                                                for (DateFormat dateFormat : possibleFormats)
                                                {
                                                    // Add the Data Object to the List
                                                    dataObjects.add(new DataObject(dataObjectInformationString, dateFormat.parse(dataObjectDateString)));
                                                }
                                            }

                                            catch (Exception ignored) {}
                                        }

                                        catch (Exception e)
                                        {
                                            Toast.makeText(this, "Import Failed", Toast.LENGTH_LONG).show();
                                        }
                                    }
                                }

                                // Close the Buffered Reader
                                br.close();

                                // Show the Success Message
                                Toast.makeText(this, "Import Successful", Toast.LENGTH_LONG).show();

                                // Navigate to the Project Creation Activity
                                showProjectCreationFragment(dataProjectTitle, dataObjects);
                            }
                        }
                    }

                    catch (FileNotFoundException e)
                    {
                        e.printStackTrace();
                    }

                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                }
                break;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void importProject()
    {
        // Show the How-To for Importing Projects
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Importing Data Project")
               .setMessage("Data Object must be in the format of \'<TITLE>\n<Data_Object> <Space> <Date>\'\n\nDate must be in the format of MM/DD/YYY or MM/DD/YYY HH:MM")
               .setCancelable(false)
               .setPositiveButton("OK", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int id)
            {
                // Create the File Select Intent
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("text/plain");
                startActivityForResult(intent, FILE_SELECT_CODE);
            }
        });

        // Show the Alert Dialog
        AlertDialog alert = builder.create();
        alert.show();

        // Set the Color of the Positive Button
        alert.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(Color.BLACK);
    }

    private void showProjectCreationFragment(String dataProjectTitle, ArrayList<DataObject> dataObjects)
    {
        // Create the Project Creation Activity
        Intent projectCreationIntent = new Intent(this, ProjectCreationActivity.class);

        // Add Extra to the Intent
        if (dataProjectTitle != null) projectCreationIntent.putExtra("DATA_TITLE"  , dataProjectTitle);
        if (dataObjects      != null) projectCreationIntent.putExtra("DATA_OBJECTS", dataObjects     );

        // Create the Project Creation Activity with Animation
        startActivity(projectCreationIntent);
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }


    @Override
    public void onBackPressed()
    {
        if (drawerLayout.isDrawerOpen(GravityCompat.START))
        {
            drawerLayout.closeDrawer(GravityCompat.START);
        }

        // Exit out of Full Screen Graph
        else if (GraphFragment.isFullScreen)
        {
            // Fetch the Main App View and Content View
            RelativeLayout mainAppView = findViewById(R.id.main_app_view);
            RelativeLayout contentMain = mainAppView.findViewById(R.id.content_main);

            // Show the Views
            mainAppView.findViewById(R.id.app_bar_layout).setVisibility(View.VISIBLE);
            mainAppView.findViewById(R.id.adView)        .setVisibility(View.VISIBLE);
            contentMain.findViewById(R.id.tab_layout)    .setVisibility(View.VISIBLE);

            // Return the System UI Visibility
            mainAppView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);

            // Set the FullScreen Flag
            GraphFragment.isFullScreen = false;
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
            // Send the User to the Project Editor Fragment
            case R.id.manage_projects:
                startProjectEditorFragment();
                break;

            // Import Project
            case R.id.import_project:
                importProject();
                break;

            // Log the User Out
            case R.id.log_out:
                logOut();
                break;
        }

        // Close the Drawer
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void startProjectEditorFragment()
    {
        // Clear the Backstack
        while (getSupportFragmentManager().getBackStackEntryCount() > 0)
            getSupportFragmentManager().popBackStackImmediate();

        // Start the Project Editor Fragment (If not Already on the Project Editor Fragment)
        if (!(getSupportFragmentManager().findFragmentById(R.id.content_main) instanceof ProjectEditorFragment))
            getSupportFragmentManager().beginTransaction().add(R.id.content_main, new ProjectEditorFragment()).commit();
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

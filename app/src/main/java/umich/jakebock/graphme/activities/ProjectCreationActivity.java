package umich.jakebock.graphme.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import java.io.FileNotFoundException;
import java.io.IOException;

import umich.jakebock.graphme.R;
import umich.jakebock.graphme.classes.DataProject;
import umich.jakebock.graphme.support_classes.DataProjectContainer;

public class ProjectCreationActivity extends AppCompatActivity
{
    private View                 rootView;
    private DataProject          dataProject;
    private DataProjectContainer dataProjectContainer;

    private String projectTitle = null;
    private Bitmap projectImage = null;

    private int GALLERY_REQUEST_CODE = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        // Call the Super
        super.onCreate(savedInstanceState);

        // Set the Content View
        setContentView(R.layout.activity_project_creation);

        // Initalize the Views
        initializeViews();

        // Initialize the Toolbar
        initializeToolbar();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.project_creation_menu, menu);
        return true;
    }

    // Handle the Options Selected in the Fragment
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle item selection
        switch (item.getItemId())
        {
            // Back Button Clicked
            case android.R.id.home:

                // Return to the Main Activity
                finish();

            // Checkbox Menu Clicked
            case R.id.action_menu_done:

                // Collect the Project Information
                collectDataProjectInformation();

                // Ensure the Project Title is Populated
                if (projectTitle.length() > 0)
                {
                    // Create the New Data Project
                    dataProject = new DataProject(projectTitle, projectImage);

                    // Create the Data Project Container
                    dataProjectContainer = new DataProjectContainer(getApplicationContext());

                    // Create the New Project
                    dataProjectContainer.createProject(dataProject);

                    // Fetch the List View
                    //ListView projectListView = fragment.getView().findViewById(R.id.project_list_view);

                    //// Create the List of Data Project
                    //// TODO Get the List from Memory
                    //ArrayList<DataProject> dataProjectArrayList = new ArrayList<DataProject>();
                    //dataProjectArrayList.add(dataProject);

                    //// Create the List Adapter
                    //ProjectListAdapter adapter = new ProjectListAdapter(dataProjectArrayList, activity.getApplicationContext());

                    //// Set the Adapter for the List View
                    //projectListView.setAdapter(adapter);

                    // Return to the Main Activity
                    finish();
                }

            default:

                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        // Detect Request Codes
        if (requestCode == GALLERY_REQUEST_CODE && resultCode == Activity.RESULT_OK)
        {
            try
            {
                // Fetch the Image Button
                ImageButton imageButton = findViewById(R.id.import_image_image_button);

                // Set the Image Button to Visible
                imageButton.setVisibility(View.VISIBLE);

                // Set the Button to GONE
                findViewById(R.id.import_image_button).setVisibility(View.GONE);

                // Set the Image to the Bitmap of the Selected Image
                imageButton.setImageBitmap(MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData()));
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
    }

    private void collectDataProjectInformation()
    {
        // Fetch the Project Title
        projectTitle = ((EditText) findViewById(R.id.project_name)).getText().toString();

        // Fetch the Project Image
        ImageButton imageButton = (ImageButton) findViewById(R.id.import_image_image_button);
        if ((imageButton.getDrawable()) != null)
            projectImage = ((BitmapDrawable) imageButton.getDrawable()).getBitmap();
    }

    private void initializeToolbar()
    {
        // Set the Support Action Bar
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        // Fetch the Support Action Toolbar
        ActionBar actionBar = getSupportActionBar();

        // Set the Project Page Title
        actionBar.setTitle(getResources().getString(R.string.title_activity_project_creation));

        // Set the Menu Frame
        actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_black_24dp);

        // Set the Menu Frame
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    private void initializeViews()
    {
        // Request Focus on the Edit Text
        findViewById(R.id.project_name).requestFocus();

        // Add Listener for Import Image Button
        findViewById(R.id.import_image_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI), GALLERY_REQUEST_CODE);
            }
        });

        // Add Listener for Import Image Button
        // NOTE - This is after the first image has been selected
        findViewById(R.id.import_image_image_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI), GALLERY_REQUEST_CODE);
            }
        });
    }
}

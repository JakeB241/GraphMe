package umich.jakebock.graphme.activities;

import android.Manifest;
import android.app.Activity;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import pub.devrel.easypermissions.EasyPermissions;
import umich.jakebock.graphme.R;
import umich.jakebock.graphme.classes.DataObject;
import umich.jakebock.graphme.classes.DataProject;
import umich.jakebock.graphme.support_classes.DataObjectListAdapter;
import umich.jakebock.graphme.support_classes.DataProjectContainer;

public class ProjectCreationActivity extends AppCompatActivity
{
    private DataProject currentDataProject;

    private String projectTitle;
    private String projectImageFilePath = "";

    private EditText    projectName;
    private Button      importImageButton;
    private ImageButton importImageImageButton;

    private ListView                dataObjectListView;
    private DataObjectListAdapter   dataObjectListAdapter;

    private int GALLERY_REQUEST_CODE = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        // Call the Super
        super.onCreate(savedInstanceState);

        // Set the Content View
        setContentView(R.layout.activity_project_creation);

        // Check to see if a Data Project was Passed to be Edited
        currentDataProject = (DataProject) getIntent().getSerializableExtra("DATA_PROJECT");

        // Initalize the Views
        initializeViews();
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
                return true;

            // Checkbox Menu Clicked
            case R.id.action_menu_done:

                // Fetch the Project Title
                projectTitle = projectName.getText().toString();

                // Create the Data Project Container
                DataProjectContainer container = new DataProjectContainer(getApplicationContext());

                // Ensure the Project Title is Populated
                if (projectTitle.length() < 1 || projectTitle.length() > 15)
                {
                    projectName.setError("Project Name Must be Between 1-15 Characters");
                    return false;
                }

                // Ensure this is Not an Edit and the Project Exist
                else if (currentDataProject == null && container.projectExists(projectTitle))
                {
                    projectName.setError("Project Exists");
                    return false;
                }

                // Delete the Previous Project from Internal Storage (If this is an Edit)
                Boolean removePreviousProject = currentDataProject != null;

                // Create the New Data Project
                currentDataProject = new DataProject(projectTitle, projectImageFilePath, returnDataObjects());

                // Create the New Project
                container.createProject(currentDataProject, removePreviousProject);

                // Return to the Main Activity
                finish();

                return true;

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
                // Set the Image Button to Visible
                importImageImageButton.setVisibility(View.VISIBLE);

                // Set the Button to GONE
                importImageButton.setVisibility(View.GONE);

                // Fetch the Data
                Uri selectedImageURI = data.getData();

                // Set the Image to the Bitmap of the Selected Image
                importImageImageButton.setImageBitmap(MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageURI));

                // Fetch the File for the Image
                projectImageFilePath = getRealPathFromURI(selectedImageURI);
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

    public String getRealPathFromURI(Uri contentUri)
    {
        String[] proj = { MediaStore.Images.Media.DATA };

        CursorLoader cursorLoader = new CursorLoader(this, contentUri, proj, null, null, null);
        Cursor cursor = cursorLoader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    private void initializeToolbar(String title)
    {
        // Set the Support Action Bar
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        // Fetch the Support Action Toolbar
        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null)
        {
            // Set the Project Page Title title
            actionBar.setTitle(title);

            // Set the Menu Frame
            actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_black_24dp);

            // Set the Menu Frame
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void initializeAddButton()
    {
        // Create the Floating Action Button
        FloatingActionButton addButton = (FloatingActionButton) findViewById(R.id.add_button);

        // Create the Listener for the Add Button
        addButton.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                dataObjectListAdapter.add(new DataObject());
                dataObjectListAdapter.notifyDataSetChanged();
            }
        });
    }

    private ArrayList<DataObject> returnDataObjects()
    {
        ArrayList<DataObject> dataObjects = new ArrayList<>();

        for (int i=0 ; i < dataObjectListAdapter.getCount(); i++)
        {
            dataObjects.add(dataObjectListAdapter.getItem(i));
        }

        return dataObjects;
    }


    private void startGalleryRequest()
    {
        String[] galleryPermissions = new String[0];
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN)
            galleryPermissions = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};

        if ((!galleryPermissions[0].equals("")) && (EasyPermissions.hasPermissions(this, galleryPermissions)))
            startActivityForResult(new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI), GALLERY_REQUEST_CODE);

        else
            EasyPermissions.requestPermissions(this, "Access for storage",101, galleryPermissions);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults)
    {
        // If request is cancelled, the result arrays are empty.
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
        {
            startGalleryRequest();
        }
    }

    private void initializeDataObjectListView()
    {
        // Create the List Adapter
        dataObjectListAdapter = new DataObjectListAdapter(getApplicationContext(), ProjectCreationActivity.this);

       if (currentDataProject != null && currentDataProject.getDataObjectList().size() > 0) dataObjectListAdapter.addAll(currentDataProject.getDataObjectList());

        // Set the Adapter for the List View
        dataObjectListView.setAdapter(dataObjectListAdapter);
    }

    private void initializeViews()
    {
        // Fetch the Views
        projectName             = findViewById(R.id.project_name);
        importImageButton       = findViewById(R.id.import_image_button);
        importImageImageButton  = findViewById(R.id.import_image_image_button);
        dataObjectListView      = findViewById(R.id.data_object_list_view);

        // Set the Existing Data Project Parameters (If the Data Project was Passed In)
        if (currentDataProject != null)
        {
            // Set the Text of the Project Name
            projectName.setText(currentDataProject.getProjectTitle());

            // Set the Image Button to Visible
            importImageImageButton.setVisibility(View.VISIBLE);

            // Set the Button to GONE
            importImageButton.setVisibility(View.GONE);

            // Set the Image of the Image Button
            importImageImageButton.setImageBitmap(currentDataProject.returnBitmapImage());

            // Get the Image Path
            projectImageFilePath = currentDataProject.getProjectImageFilePath();

            // Set the Toolbar to Edit Project
            initializeToolbar(getResources().getString(R.string.title_activity_project_edit));
        }

        else
        {
            // Initialize the Toolbar to Create Project
            initializeToolbar(getResources().getString(R.string.title_activity_project_creation));
        }

        // Add Listener for Import Image Button
        importImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startGalleryRequest();
            }
        });

        // Add Listener for Import Image Button
        // NOTE - This is after the first image has been selected
        importImageImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startGalleryRequest();
            }
        });


        // Initialize the Add Button
        initializeAddButton();

        // Initialize the Data Object List View
        initializeDataObjectListView();

        // Request Focus on the Edit Text
        //projectName.requestFocus();
    }
}

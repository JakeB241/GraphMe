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
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;

import pub.devrel.easypermissions.EasyPermissions;
import umich.jakebock.graphme.R;
import umich.jakebock.graphme.classes.DataProject;
import umich.jakebock.graphme.classes.Setting;
import umich.jakebock.graphme.support_classes.DataObjectListAdapter;
import umich.jakebock.graphme.support_classes.DataProjectContainer;

public class ProjectCreationActivity extends AppCompatActivity
{
    private DataProject currentDataProject;

    private String projectTitle;
    private String projectImageFilePath = "";

    private EditText                 projectName;
    private Button                   importImageButton;
    private ImageButton              importImageImageButton;
    private HashMap<String, Setting> settingHashMap;

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

                // TODO - Collect the Settings

                // Delete the Previous Project from Internal Storage (If this is an Edit)
                Boolean removePreviousProject = currentDataProject != null;

                // Create the New Data Project
                currentDataProject = new DataProject(projectTitle, projectImageFilePath);

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

    private void initializeSettings()
    {
        // Fetch the Setting Linear Layout
        LinearLayout settingsLinearLayout  = findViewById(R.id.settings_linear_layout);

        // Initialize the Setting HashMap
        settingHashMap = new HashMap<String, Setting>();

        // Create the Settings for the Project and Append to the Hashmap
        settingHashMap.put("INCLUDE_TIME", new Setting("Include Time in Date", Setting.SettingType.CHECKBOX, true));

        // Append the Settings to the Linear Layout
        for (String settingKey : settingHashMap.keySet())
        {
            // Fetch the Current Setting
            Setting setting = settingHashMap.get(settingKey);

            // Inflate the View
            View parentView = View.inflate(this, R.layout.setting_item, settingsLinearLayout);

            // Fetch the Text View
            TextView settingLabel = parentView.findViewById(R.id.setting_label);
            settingLabel.setText(setting.getLabelText());

            // Find the Setting Type
            if (setting.getSettingType().equals(Setting.SettingType.CHECKBOX))
            {
                // Create the Checkbox
                CheckBox checkBox = parentView.findViewById(R.id.setting_checkbox);
                checkBox.setVisibility(View.VISIBLE);

                // Set the Checkbox
                if (currentDataProject != null) checkBox.setChecked((Boolean)currentDataProject.getDataProjectSettings().get(settingKey).getChosenValue());
                else                            checkBox.setChecked((Boolean)setting.getDefaultValue());
            }

            else if ((setting.getSettingType().equals(Setting.SettingType.CHECKBOX)))
            {
                Spinner spinner = parentView.findViewById(R.id.setting_spinner);
                spinner.setVisibility(View.VISIBLE);
            }
        }
    }

    private void initializeViews()
    {
        // Fetch the Views
        projectName             = findViewById(R.id.project_name);
        importImageButton       = findViewById(R.id.import_image_button);
        importImageImageButton  = findViewById(R.id.import_image_image_button);

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

        // Initialize the Settings
        initializeSettings();

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
    }
}

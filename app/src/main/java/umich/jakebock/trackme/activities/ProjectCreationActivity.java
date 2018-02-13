package umich.jakebock.trackme.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;

import pub.devrel.easypermissions.EasyPermissions;
import umich.jakebock.trackme.R;
import umich.jakebock.trackme.classes.DataProject;
import umich.jakebock.trackme.classes.Setting;
import umich.jakebock.trackme.firebase.FirebaseHandler;
import umich.jakebock.trackme.support_classes.DataObjectListAdapter;

public class ProjectCreationActivity extends AppCompatActivity
{
    private DataProject currentDataProject;
    private DataProject previousDataProject;

    private String projectTitle;
    private String projectImageFilePath = "";

    private EditText           projectName;
    private ImageButton        importImageImageButton;
    private FirebaseHandler    firebaseHandler;
    private ArrayList<Setting> dataProjectSettings;

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
        previousDataProject = (DataProject) getIntent().getSerializableExtra("DATA_PROJECT");

        // Create the FireBase Handler
        firebaseHandler = new FirebaseHandler(this);

        // Set the Listener for the FireBase Handler
        firebaseHandler.setListener(dataLoadCompletedListener);

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

                // Ensure the Project Title is Populated
                if (projectTitle.length() < 1 || projectTitle.length() > 15)
                {
                    projectName.setError("Project Name Must be Between 1-15 Characters");
                    return false;
                }

                // Collect the Settings
                collectSettings();

                // Create the New Data Project
                currentDataProject = new DataProject(projectTitle, projectImageFilePath, dataProjectSettings);

                // Creating a New Project
                if (previousDataProject == null)
                {
                    // Check if the Project Exists and Create the Project in the Callback
                    firebaseHandler.projectExists(projectTitle);
                }

                // Editing Existing Project
                else
                {
                    // Delete the Current Project and Create the new Project in the Callback
                    firebaseHandler.deleteProjects(new ArrayList<DataProject>(Collections.singletonList(previousDataProject)));
                }

                return true;

            default:

                return super.onOptionsItemSelected(item);
        }
    }

    private void collectSettings()
    {
        // Fetch the Setting Linear Layout
        LinearLayout settingsLinearLayout  = findViewById(R.id.settings_linear_layout);

        // Loop through all App Settings
        for (Setting setting : MainActivity.settingsList)
        {
            // Fetch the Values
            switch (setting.getSettingType())
            {
                // Switch Case
                case SWITCH:

                    // Fetch the Switch
                    Switch switchView = settingsLinearLayout.getChildAt(0).findViewById(R.id.setting_switch);

                    // Set the Chosen Value
                    setting.setChosenValue(switchView.isChecked());
                    break;

                // Swich Spinner
                case SPINNER:

                    // Fetch the Switch
                    Spinner spinnerView = settingsLinearLayout.getChildAt(0).findViewById(R.id.setting_spinner);

                    // Set the Chosen Value
                    setting.setChosenValue(spinnerView.getSelectedItem());
                    break;
            }

            // Add to the Setting List
            dataProjectSettings.add(setting);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        // Detect Request Codes
        if (requestCode == GALLERY_REQUEST_CODE && resultCode == Activity.RESULT_OK)
        {
            // Fetch the Data
            Uri selectedImageURI = data.getData();

            // Set the Image to the Bitmap of the Selected Image
            importImageImageButton.setImageBitmap(DataProject.returnCorrectlyOrientedImage(this, selectedImageURI));

            // Fetch the File for the Image
            projectImageFilePath = selectedImageURI.toString();
        }
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

    // Listener for the Data Object List Adapter
    FirebaseHandler.DataLoadCompletedListener dataLoadCompletedListener = new FirebaseHandler.DataLoadCompletedListener()
    {
        @Override
        public void dataProjectExistsCompleted(Boolean projectExists)
        {
            // If the Project Exists, Show an Error Message
            if (projectExists)
            {
                projectName.setError("Project Exists");
                return;
            }

            // Add the Previous Data Project Data to the New Data Project
            if (previousDataProject != null)
                currentDataProject.setDataObjectList(previousDataProject.getDataObjectList());

            // Create the Project
            firebaseHandler.createProject(currentDataProject);
        }

        @Override
        public void dataProjectCreatedCompleted()
        {
            // Finish the Activity when the Data Project has been Created
            finish();
        }

        @Override
        public void dataProjectsDeletedCompleted(ArrayList<DataProject> deletedDataProjects)
        {
            // Check if the Project Exists and Create the Project in the Callback
            firebaseHandler.projectExists(projectTitle);
        }

        @Override
        public void dataProjectLoadCompleted(ArrayList<DataProject> loadedDataProjects) {}
    };

    private void initializeSettings()
    {
        // Fetch the Setting Linear Layout
        LinearLayout settingsLinearLayout = findViewById(R.id.settings_linear_layout);

        // Fetch the Inflater
        LayoutInflater inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // Initialize the Setting HashMap
        dataProjectSettings = new ArrayList<>();

        // Append the Settings to the Linear Layout
        for (Setting setting : MainActivity.settingsList)
        {
            // Ensure Chosen Value is null
            setting.setChosenValue(null);

            // Swap the Setting with the Data Project Setting if this is an Edit
            if (previousDataProject != null && previousDataProject.findSettingById(setting.getSettingId()) != null)
                setting = previousDataProject.findSettingById(setting.getSettingId());

            // Inflate the View
            View parentView = inflater.inflate(R.layout.setting_item, null);

            // Fetch the Text View
            TextView settingLabel = parentView.findViewById(R.id.setting_label);

            // Set the Label Text
            settingLabel.setText(setting.getLabelText());

            // Find the Setting Type
            if (setting.getSettingType().equals(Setting.SettingType.SWITCH))
            {
                // Create the Switch
                Switch settingSwitch = parentView.findViewById(R.id.setting_switch);
                settingSwitch.setVisibility(View.VISIBLE);

                // Set the Switch
                if (setting.getChosenValue() != null) settingSwitch.setChecked((Boolean)setting.getChosenValue());
                else                                  settingSwitch.setChecked((Boolean)setting.getDefaultValue());

                // Set the Tag
                settingSwitch.setTag(setting.getSettingId());
            }

            else if ((setting.getSettingType().equals(Setting.SettingType.SPINNER)))
            {
                // Create the Spinner
                Spinner settingSpinner = parentView.findViewById(R.id.setting_spinner);
                settingSpinner.setVisibility(View.VISIBLE);

                // Set the Spinner Adapter
                ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, setting.getAvailableValues());
                settingSpinner.setAdapter(spinnerArrayAdapter);

                // Set the Spinner
                if (setting.getChosenValue() != null) settingSpinner.setSelection(setting.getAvailableValues().indexOf(setting.getChosenValue()));
                else                                  settingSpinner.setSelection(setting.getAvailableValues().indexOf(setting.getDefaultValue()));

                // Set the Tag
                settingSpinner.setTag(setting.getSettingId());
            }

            // Adjust the Margin of the Parent View
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(0, 0,0, 60);
            parentView.setLayoutParams(layoutParams);

            // Add the View
            settingsLinearLayout.addView(parentView);
        }
    }

    private void initializeViews()
    {
        // Fetch the Views
        projectName             = findViewById(R.id.project_name);
        importImageImageButton  = findViewById(R.id.import_image_image_button);

        // Set the Existing Data Project Parameters (If the Data Project was Passed In)
        if (previousDataProject != null)
        {
            // Set the Text of the Project Name
            projectName.setText(previousDataProject.getProjectTitle());

            // Set the Image of the Image Button
            if (!previousDataProject.returnImageURI().toString().equals(""))
                importImageImageButton.setImageBitmap(DataProject.returnCorrectlyOrientedImage(this, previousDataProject.returnImageURI()));

            // Get the Image Path
            projectImageFilePath = previousDataProject.getProjectImageFilePath();

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
        importImageImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startGalleryRequest();
            }
        });
    }
}

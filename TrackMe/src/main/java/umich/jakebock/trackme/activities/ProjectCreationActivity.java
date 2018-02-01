package umich.jakebock.trackme.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
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
import android.widget.EditText;
import android.widget.ImageButton;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

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

    private EditText                 projectName;
    private Button                   importImageButton;
    private ImageButton              importImageImageButton;
    private FirebaseHandler          firebaseHandler;
    private HashMap<String, Setting> settingHashMap;

    private DataObjectListAdapter   dataObjectListAdapter;
    private int MAX_IMAGE_DIMENSION = 512;

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

                // TODO - Collect the Settings

                // Create the New Data Project
                currentDataProject = new DataProject(projectTitle, projectImageFilePath);

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
                importImageImageButton.setImageBitmap(getCorrectlyOrientedImage(this, selectedImageURI));

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
        /*LinearLayout settingsLinearLayout  = findViewById(R.id.settings_linear_layout);

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
        }*/
    }

    private int getOrientation(Context context, Uri photoUri)
    {
    /* it's on the external media. */
        Cursor cursor = context.getContentResolver().query(photoUri, new String[] { MediaStore.Images.ImageColumns.ORIENTATION }, null, null, null);

        if (cursor.getCount() != 1) {
            return -1;
        }

        cursor.moveToFirst();
        return cursor.getInt(0);
    }

    private Bitmap getCorrectlyOrientedImage(Context context, Uri photoUri) throws IOException
    {
        InputStream is = context.getContentResolver().openInputStream(photoUri);
        BitmapFactory.Options dbo = new BitmapFactory.Options();
        dbo.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(is, null, dbo);
        is.close();

        int rotatedWidth, rotatedHeight;
        int orientation = getOrientation(context, photoUri);

        if (orientation == 90 || orientation == 270) {
            rotatedWidth = dbo.outHeight;
            rotatedHeight = dbo.outWidth;
        } else {
            rotatedWidth = dbo.outWidth;
            rotatedHeight = dbo.outHeight;
        }

        Bitmap srcBitmap;
        is = context.getContentResolver().openInputStream(photoUri);
        if (rotatedWidth > MAX_IMAGE_DIMENSION || rotatedHeight > MAX_IMAGE_DIMENSION)
        {
            float widthRatio = ((float) rotatedWidth) / ((float) MAX_IMAGE_DIMENSION);
            float heightRatio = ((float) rotatedHeight) / ((float) MAX_IMAGE_DIMENSION);
            float maxRatio = Math.max(widthRatio, heightRatio);

            // Create the bitmap from file
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = (int) maxRatio;
            srcBitmap = BitmapFactory.decodeStream(is, null, options);
        }

        else
        {
            srcBitmap = BitmapFactory.decodeStream(is);
        }
        is.close();

    /*
     * if the orientation is not 0 (or -1, which means we don't know), we
     * have to do a rotation.
     */
        if (orientation > 0) {
            Matrix matrix = new Matrix();
            matrix.postRotate(orientation);

            srcBitmap = Bitmap.createBitmap(srcBitmap, 0, 0, srcBitmap.getWidth(), srcBitmap.getHeight(), matrix, true);
        }

        return srcBitmap;
    }

    private void initializeViews()
    {
        // Fetch the Views
        projectName             = findViewById(R.id.project_name);
        importImageButton       = findViewById(R.id.import_image_button);
        importImageImageButton  = findViewById(R.id.import_image_image_button);

        // Set the Existing Data Project Parameters (If the Data Project was Passed In)
        if (previousDataProject != null)
        {
            // Set the Text of the Project Name
            projectName.setText(previousDataProject.getProjectTitle());

            // Set the Image Button to Visible
            importImageImageButton.setVisibility(View.VISIBLE);

            // Set the Button to GONE
            importImageButton.setVisibility(View.GONE);

            // Set the Image of the Image Button
            importImageImageButton.setImageBitmap(previousDataProject.returnBitmapImage());

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

package umich.jakebock.graphme.fragments;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupWindow;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import umich.jakebock.graphme.R;
import umich.jakebock.graphme.classes.DataProject;
import umich.jakebock.graphme.support_classes.ProjectListAdapter;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProjectEditorFragment extends Fragment
{
    private ViewGroup container;
    private View      popupView;

    public ProjectEditorFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Save the container
        this.container = container;

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_project_editor, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        // Initialize the Add Button
        initializeAddButton(view);
    }


    private void initializeAddButton(View view)
    {
        // Create the Floating Action Button
        FloatingActionButton addButton = (FloatingActionButton) view.findViewById(R.id.add_button);

        // Create the Listener for the Add Button
        addButton.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                ListView projectListView = (ListView) getView().findViewById(R.id.project_list_view);

                // TODO Create GUI for Adding Projects

                // Create Test Project
                DataProject dataProject = new DataProject("Project Title");

                // Create the List Adapter
                //adapter= new ProjectListAdapter(dataModels,getApplicationContext());

                // Fetch the Project List View
                showProjectCreationWindow();
            }
        });
    }

    private void okButtonClicked(View view)
    {

    }

    private void showProjectCreationWindow()
    {
        // Inflate the Layout of the Popup Window
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(LAYOUT_INFLATER_SERVICE);
        final View popupView = inflater.inflate(R.layout.project_creator, null);

        // Set the PopView
        this.popupView = popupView;

        // Create the Popup Window
        final PopupWindow popupWindow = new PopupWindow(popupView, container.getWidth()-40, container.getHeight()/2, true);

        // Show the Popup Window
        popupWindow.showAtLocation(container.findViewById(R.id.content_main), Gravity.CENTER, 0, 0);

        // Dismiss the Popup Window when Touched
        popupView.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                popupWindow.dismiss();
                return true;
            }
        });

        // Add Listener for OK Button
        popupView.findViewById(R.id.ok_button).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                // Create Project
                DataProject dataProject = new DataProject(((EditText)popupView.findViewById(R.id.project_name)).getText().toString());

                // If there is a Selected Image, Add it to the Project List
                Bitmap bitmapImage = ((BitmapDrawable)((ImageButton)popupView.findViewById(R.id.import_image_image_button)).getDrawable()).getBitmap();
                if (bitmapImage != null)
                    dataProject.setProjectImage(((BitmapDrawable)((ImageButton)popupView.findViewById(R.id.import_image_image_button)).getDrawable()).getBitmap());

                // Fetch the List View
                ListView projectListView = getView().findViewById(R.id.project_list_view);

                // Create the List of Data Project
                // TODO Get the List from Memory
                ArrayList<DataProject> dataProjectArrayList = new ArrayList<DataProject>();
                dataProjectArrayList.add(dataProject);

                // Create the List Adapter
                ProjectListAdapter adapter = new ProjectListAdapter(dataProjectArrayList, getActivity().getApplicationContext());

                // Set the Adapter for the List View
                projectListView.setAdapter(adapter);

                // Dismiss the Popup Window
                popupWindow.dismiss();
            }
        });

        // Add Listener for Cancel Button
        popupView.findViewById(R.id.cancel_button).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                popupWindow.dismiss();
            }
        });

        // Add Listener for Import Image Button
        popupView.findViewById(R.id.import_image_button).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                startActivityForResult(new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI), 3);
            }
        });

        // Add Listener for Import Image Button
        // NOTE - This is after the first image has been selected
        popupView.findViewById(R.id.import_image_image_button).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                startActivityForResult(new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI), 3);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        //Detects request codes
        if (requestCode == 3 && resultCode == Activity.RESULT_OK) {
            Uri selectedImage = data.getData();
            Bitmap bitmap = null;
            try
            {
                // Fetch the Image
                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), selectedImage);

                // Fetch the Image Button
                ImageButton imageButton = popupView.findViewById(R.id.import_image_image_button);

                // Set the Image Button to Visible
                imageButton.setVisibility(View.VISIBLE);

                // Set the Button to GONE
                popupView.findViewById(R.id.import_image_button).setVisibility(View.GONE);

                // Set the Image to the Bitmap of the Selected Image
                imageButton.setImageBitmap(bitmap);
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
}

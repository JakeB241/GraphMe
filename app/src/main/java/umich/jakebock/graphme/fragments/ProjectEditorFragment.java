package umich.jakebock.graphme.fragments;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

import umich.jakebock.graphme.R;
import umich.jakebock.graphme.activities.ProjectCreationActivity;
import umich.jakebock.graphme.classes.DataProject;
import umich.jakebock.graphme.support_classes.DataProjectContainer;
import umich.jakebock.graphme.support_classes.ProjectListAdapter;

public class ProjectEditorFragment extends Fragment
{
    private View                    rootView;
    private DataProjectContainer    dataProjectContainer;
    private ListView                projectListView;

    private ArrayList<DataProject>  projectList;
    private ArrayList<DataProject>  selectedProjects;

    public ProjectEditorFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_project_editor, container, false);

        // Populate the Project List
        populateProjectListView();

        // Initialize Toolbar
        initializeToolbar();

        // Initialize the Add Button
        initializeAddButton();

        // Return the RootView
        return rootView;
    }

    @Override
    public void onResume()
    {
        // Re Populate the Project List
        populateProjectListView();

        // Re Initialize Toolbar
        initializeToolbar();

        // Call the Activity On Resume
        super.onResume();
    }

    private void populateProjectListView()
    {
        // Initalize the Data Project Container and Load the Projects
        initializeDataProjectContainer(getActivity().getApplicationContext());

        // Fetch the List View
        projectListView = rootView.findViewById(R.id.project_list_view);

        // Create the List Adapter
        ProjectListAdapter adapter = new ProjectListAdapter(projectList, getActivity().getApplicationContext());

        // Set the Adapter for the List View
        projectListView.setAdapter(adapter);

        // Set the Action Mode Callback
        projectListView.setMultiChoiceModeListener(new ActionModeCallback());

        // Set the On Click for the Project List View
        projectListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                // Fetch the Data Project
                DataProject dataProject = (DataProject) projectListView.getItemAtPosition(position);

                // Create the Fragment
                ProjectBreakdownFragment projectBreakdownFragment = new ProjectBreakdownFragment();

                // Create the Bundle for the Data Project Selected
                Bundle bundle = new Bundle();

                // Add the Data Project
                bundle.putSerializable("DATA_PROJECT", dataProject);

                // Add the Bundle to the Fragment
                projectBreakdownFragment.setArguments(bundle);

                // Transition to the Project Breakdown Fragment
                getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out).
                                                                             add(R.id.content_main, projectBreakdownFragment).
                                                                             addToBackStack("ProjectEditorFragment").
                                                                             commit();
            }
        });
    }

    private void showDeleteAlertDialog()
    {
        // Create the Alert Dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Get the Message that will be Displayed
        String message = "";
        if (selectedProjects.size() == 1) message = (selectedProjects.get(0).getProjectTitle());
        else                              message =  selectedProjects.size() + " Projects";

        // Set the Message of the Alert Dialog
        builder.setMessage(message + " will be deleted.");

        // Create the Delete Button
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                // Delete the Projects
                projectList = dataProjectContainer.deleteProjects(selectedProjects);

                // Re-Create the Project List View
                populateProjectListView();
            }
        });

        // Create the Cancel Button
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.dismiss();
            }
        });

        // Show the Alert Dialog
        AlertDialog alert = builder.create();
        alert.show();

        // Set the Color of the Positive and Negative Button
        alert.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(Color.BLACK);
        alert.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
    }

    // Listener for the Action Mode Callback for the Action Bar (Long Click on List Items)
    private class ActionModeCallback implements ListView.MultiChoiceModeListener
    {
        @Override
        public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked)
        {
            // Fetch the Data Project of the Selected Item
            DataProject selectedDataProject = (DataProject) projectListView.getItemAtPosition(position);

            // Add/Remove from the Selected Projects List
            if (checked) selectedProjects.add   (selectedDataProject);
            else         selectedProjects.remove(selectedDataProject);

            // Remove the Edit Button there are More Than One Selected Projects
            if (selectedProjects.size() > 1) mode.getMenu().findItem(R.id.action_menu_edit).setVisible(false);
            else                             mode.getMenu().findItem(R.id.action_menu_edit).setVisible(true);
        }

        public boolean onCreateActionMode(ActionMode mode, Menu menu)
        {
            // Initialize the Selected Projects
            selectedProjects = new ArrayList<>();

            // Inflate the Project Editor Edit Menu
            getActivity().getMenuInflater().inflate(R.menu.project_editor_edit, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) { return false; }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item)
        {
            // Switch for the Item ID
            switch (item.getItemId())
            {
                case R.id.action_menu_edit:
                    showProjectCreationFragment(selectedProjects.get(0));
                    break;
                case R.id.action_menu_delete:
                    showDeleteAlertDialog();
                    break;
                default:
                    return false;

            }

            // Finish the Action Mode
            mode.finish();
            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {}
    }

    private void initializeDataProjectContainer(Context context)
    {
        // Create a New Instance of the Data Project Container
        dataProjectContainer = new DataProjectContainer(context);

        // Load All Projects from Device Memory
        projectList = dataProjectContainer.loadProjects();
    }

    private void initializeAddButton()
    {
        // Create the Floating Action Button
        FloatingActionButton addButton = (FloatingActionButton) rootView.findViewById(R.id.add_button);

        // Create the Listener for the Add Button
        addButton.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                // Show the Project Creation Fragment
                showProjectCreationFragment(null);
            }
        });
    }

    private void initializeToolbar()
    {
        // Fetch the Action Bar
        ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();

        // Set the Action Bar Title
        if (actionBar != null) actionBar.setTitle(R.string.toolbar_project_title);
    }


    private void showProjectCreationFragment(DataProject dataProject)
    {
        // Create the Project Creation Activity
        Intent projectCreationIntent = new Intent(getActivity(), ProjectCreationActivity.class);

        // Add Extra to the Intent
        if (dataProject != null) projectCreationIntent.putExtra("DATA_PROJECT", dataProject);

        // Create the Project Creation Activity with Animation
        startActivity(projectCreationIntent);
        getActivity().overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }
}

package umich.jakebock.graphme.fragments;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.TextView;

import java.util.ArrayList;

import umich.jakebock.graphme.R;
import umich.jakebock.graphme.activities.ProjectCreationActivity;
import umich.jakebock.graphme.classes.DataProject;
import umich.jakebock.graphme.support_classes.DataProjectContainer;
import umich.jakebock.graphme.support_classes.ProjectListAdapter;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProjectEditorFragment extends Fragment
{
    private View                    rootView;
    private ArrayList<DataProject>  projectList;
    private DataProjectContainer    dataProjectContainer;
    private ListView                projectListView;
    private ArrayList<String>       selectedProjects;

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

        // Set the OnClickListener for the List Adapter
        projectListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                // Create the Fragment
                ProjectBreakdownFragment projectBreakdownFragment = new ProjectBreakdownFragment();

                // Create the Bundle for the Data Project Selected
                Bundle bundle = new Bundle();

                // Fetch the Project Name
                String projectName = ((TextView)view.findViewById(R.id.project_name)).getText().toString();

                // Add the Data Project Name
                bundle.putString("PROJECT_NAME", projectName);

                // Add the Bundle to the Fragment
                projectBreakdownFragment.setArguments(bundle);

                // Transition to the Project Breakdown Fragment
                getActivity().getSupportFragmentManager().beginTransaction().
                                                         setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out).
                                                         add(R.id.content_main, projectBreakdownFragment).
                                                         addToBackStack("ProjectEditorFragment").
                                                         commit();
            }
        });

        projectListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        projectListView.setMultiChoiceModeListener(new ModeCallback());
    }

    private void showDeleteAlertDialog()
    {
        // Create the Alert Dialog
        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());

        // Get the Message that will be Displayed
        String message;
        if (selectedProjects.size() == 1) message = (selectedProjects.get(0));
        else                              message = selectedProjects.size() + " Projects";

        // Set the Message of the Alert Dialog
        alert.setMessage(message + " will be deleted.");

        // Create the Delete Button
        alert.setPositiveButton("Delete", new DialogInterface.OnClickListener()
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
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.dismiss();
            }
        });

        // Show the Alert Dialog
        alert.show();
    }

    // Listener for the Action Mode Callback for the Action Bar (Long Click on List Items)
    private class ModeCallback implements ListView.MultiChoiceModeListener
    {
        @Override
        public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked)
        {
            // Fetch the Project Name
            String projectName = ((TextView) projectListView.getChildAt(position).findViewById(R.id.project_name)).getText().toString();

            // Add/Remove from the Selected Projects List
            if (checked) selectedProjects.add   (projectName);
            else         selectedProjects.remove(projectName);
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
                showProjectCreationFragment();
            }
        });
    }

    private void initializeToolbar()
    {
        // Fetch the Action Bar
        ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();


        if (actionBar != null)
        {
            // Set the Action Bar Title
            actionBar.setTitle(R.string.toolbar_project_title);
        }
    }


    private void showProjectCreationFragment()
    {
        // Create the Project Creation Activity with Animation
        startActivity(new Intent(getActivity(), ProjectCreationActivity.class));
        getActivity().overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }
}

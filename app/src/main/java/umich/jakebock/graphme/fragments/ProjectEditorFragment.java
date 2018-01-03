package umich.jakebock.graphme.fragments;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

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

    public ProjectEditorFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_project_editor, container, false);

        // Initalize the Data Project Container and Load the Projects
        initializeDataProjectContainer(getActivity().getApplicationContext());

        // Populate the Project List
        populateProjectListView();

        // Initialize the Add Button
        initializeAddButton();

        // Return the RootView
        return rootView;
    }

    @Override
    public void onResume()
    {
        // Initalize the Data Project Container and Load the Projects
        initializeDataProjectContainer(getActivity().getApplicationContext());

        // Populate the Project List
        populateProjectListView();

        // Call the Activity On Resume
        super.onResume();
    }

    private void populateProjectListView()
    {
        // Fetch the List View
        ListView projectListView = rootView.findViewById(R.id.project_list_view);

        // Create the List Adapter
        ProjectListAdapter adapter = new ProjectListAdapter(projectList, getActivity().getApplicationContext());

        // Set the Adapter for the List View
        projectListView.setAdapter(adapter);
    }

    private void initializeDataProjectContainer(Context context)
    {
        // Create a New Instance of the Data Project Container
        DataProjectContainer dataProjectContainer = new DataProjectContainer(context);

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

    private void showProjectCreationFragment()
    {
        // Create the Project Creation Activity with Animation
        startActivity(new Intent(getActivity(), ProjectCreationActivity.class));
        getActivity().overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }
}

package umich.jakebock.graphme.fragments;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import umich.jakebock.graphme.R;
import umich.jakebock.graphme.activities.MainActivity;
import umich.jakebock.graphme.classes.DataObject;
import umich.jakebock.graphme.classes.DataProject;
import umich.jakebock.graphme.support_classes.DataObjectListAdapter;
import umich.jakebock.graphme.support_classes.DataProjectContainer;

public class ListFragment extends Fragment
{
    private View                   rootView;
    private DataProject            currentDataProject;
    private DataObjectListAdapter  dataObjectListAdapter;
    private ListView               dataObjectListView;
    private Boolean                saveNeeded;
    private Boolean                actionModeEnabled;

    public ListFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Create the Root View
        rootView = inflater.inflate(R.layout.fragment_list, container, false);

        // Set the Save Needed and Action Mode Enabled Flags
        saveNeeded          = false;
        actionModeEnabled   = false;

        // Fetch the Data Project
        currentDataProject = ((MainActivity)getActivity()).getCurrentDataProject();

        // Initialize the Data Object List View
        initializeDataObjectListView();

        // Initialize the Floating Action Button
        initializeAddDataObjectButton();

        // Allow the Fragment to Have a Custom Options Menu
        setHasOptionsMenu(true);

        // Return the Root View
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        // Inflate the Menu
        inflater.inflate(R.menu.list_fragment_menu, menu);

        // Fetch the Action Bar
        ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();

        // Set the Action Bar Title to the Current Data Project Title
        if (actionBar != null) actionBar.setTitle(((MainActivity) getActivity()).getCurrentDataProject().getProjectTitle());

        // Call the Super
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPrepareOptionsMenu (Menu menu)
    {
        // Fetch the MenuItem
        MenuItem saveItem = menu.findItem(R.id.action_menu_save);

        // Enable or Disable the Menu Option for Saving
        if (saveNeeded)
        {
            saveItem.setEnabled(true);
            saveItem.getIcon().setAlpha(255);
        }

        else
        {
            saveItem.setEnabled(false);
            saveItem.getIcon().setAlpha(100);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Item Selection
        switch (item.getItemId())
        {
            // Save the Data Objects
            case R.id.action_menu_save:
                collectAndSaveDataObjects();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onDestroyView()
    {
        // Collect the Data Objects
        //collectAndSaveDataObjects();

        // Call the Super
        super.onDestroyView();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser)
    {
        // Call the Super
        super.setUserVisibleHint(isVisibleToUser);

        // Collect the Data when the View Has been Created and the
        // Fragment is no longer visible to the User
        if (rootView != null && !isVisibleToUser)
        {
            // Collect the Data Objects and Set the Current Data Project
            collectAndSaveDataObjects();
        }
    }

    private void collectAndSaveDataObjects()
    {
        // Check the Save Needed Flag to Ensure that a Save is Required
        if (saveNeeded)
        {
            // Create the ArrayList to Collect the Data Objects
            ArrayList<DataObject> dataObjectArrayList = new ArrayList<>();

            // Add all of the Data Objects to the Array List
            for (int i = 0; i < dataObjectListAdapter.getCount(); i++)
            {
                // Fetch the Data Object
                DataObject dataObject = dataObjectListAdapter.getItem(i);

                // Ensure the Data Object has Text and Add to the List
                if (dataObject != null && dataObject.getObjectInformation().length() > 0)
                    dataObjectArrayList.add(dataObjectListAdapter.getItem(i));

                // Fetch the Edit Text and Text View
                RelativeLayout parentView = (RelativeLayout) dataObjectListView.getChildAt(i);
                TextView dataObjectInformationTextView = parentView.findViewById(R.id.data_object_information_text_view);
                EditText dataObjectInformationEditText = parentView.findViewById(R.id.data_object_information_edit_text);

                // Return the Edit Text Views to Text Views
                dataObjectInformationTextView.setVisibility(View.VISIBLE);
                dataObjectInformationEditText.setVisibility(View.GONE);
            }

            // Set the Current Data Objects
            currentDataProject.setDataObjectList(dataObjectArrayList);

            // Set the Current Project
            ((MainActivity) getActivity()).setCurrentProject(currentDataProject);

            // Create the Data Project Container
            DataProjectContainer dataProjectContainer = new DataProjectContainer(getActivity().getApplicationContext());

            // Create the New Version of the Project
            dataProjectContainer.createProject(currentDataProject, true);

            // Invalidate the Options Menu so the Save is Disabled
            getActivity().invalidateOptionsMenu();

            // Set the Save Needed Flag to False
            saveNeeded = false;
        }
    }

    private void initializeDataObjectListView()
    {
        // Fetch the List View
        dataObjectListView = (ListView) rootView.findViewById(R.id.data_object_list_view);

        // Initialize the List Adapter
        dataObjectListAdapter = new DataObjectListAdapter(getActivity().getApplicationContext(), getActivity());

        // Set the Data Objects for the List Adapter
        dataObjectListAdapter.addAll(currentDataProject.getDataObjectList());

        // Set the Adapter for the List View
        dataObjectListView.setAdapter(dataObjectListAdapter);

        // Set the Listener for the Save Needed Flag
        dataObjectListAdapter.setListener(dataObjectListAdapterListener);

        // Set the Action Mode Callback
        dataObjectListView.setMultiChoiceModeListener(new DataObjectActionModeCallback());
    }

    private void initializeAddDataObjectButton()
    {
        // Fetch the Floating Action Button
        FloatingActionButton addButton = (FloatingActionButton) rootView.findViewById(R.id.add_button);

        // Set the Add Button
        addButton.setImageResource(android.R.drawable.ic_input_add);

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

    // Listener for the Set Save Needed Flag
    DataObjectListAdapter.DataObjectListAdapterListener dataObjectListAdapterListener = new DataObjectListAdapter.DataObjectListAdapterListener()
    {
        @Override
        public void setSaveNeeded()
        {
            // Set the Save Needed Flag to True
            saveNeeded = true;

            // Redraw the Options Menu
            getActivity().invalidateOptionsMenu();
        }

        @Override
        public boolean getActonModeEnabled()
        {
            return actionModeEnabled;
        }
    };

    // Listener for the Action Mode Callback for the Action Bar (Long Click on List Items)
    private class DataObjectActionModeCallback implements ListView.MultiChoiceModeListener
    {
        @Override
        public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked)
        {
            // Fetch the Data Project of the Selected Item
            /*DataProject selectedDataProject = (DataProject) projectListView.getItemAtPosition(position);
            View selectedView               = (View)        projectListView.getChildAt(position);

            // Add/Remove from the Selected Projects List
            if (checked)
            {
                selectedProjects.add(selectedDataProject);
                selectedViews   .add(selectedView);
            }

            else
            {
                selectedProjects.remove(selectedDataProject);
                selectedViews   .remove(selectedView);
            }

            // Remove the Edit Button there are More Than One Selected Projects
            if (selectedProjects.size() > 1) mode.getMenu().findItem(R.id.action_menu_edit).setVisible(false);
            else                             mode.getMenu().findItem(R.id.action_menu_edit).setVisible(true);*/
        }

        public boolean onCreateActionMode(ActionMode mode, Menu menu)
        {
            // Initialize the Selected Projects
            //selectedProjects = new ArrayList<>();

            // Initialize the Selected Views
            //selectedViews = new ArrayList<>();

            // Don't allow the User to Go to Action Mode if an Edit Text is Present
            if (saveNeeded)
                return false;

            // Set the Flag for Action Mode Enabled
            actionModeEnabled = true;

            // Inflate the Project Editor Edit Menu
            getActivity().getMenuInflater().inflate(R.menu.data_object_action_mode_menu, menu);

            // Set the Title
            mode.setTitle(R.string.edit_objects_title);

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
                case R.id.action_menu_delete:
                    //showDeleteAlertDialog();
                    break;
                default:
                    return false;
            }

            // Finish the Action Mode
            mode.finish();
            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode)
        {
            // Action Mode No Longer Enabled
            actionModeEnabled = false;
        }
    }
    //endregion
}

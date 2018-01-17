package umich.jakebock.graphme.fragments;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

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
    private Boolean                saveNeeded;

    public ListFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Create the Root View
        rootView = inflater.inflate(R.layout.fragment_list, container, false);

        // Set the Save Needed Flag
        saveNeeded = false;

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
        inflater.inflate(R.menu.list_fragment_menu, menu);
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
        collectAndSaveDataObjects();

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
            for (int i = 0; i < dataObjectListAdapter.getCount(); i++) {
                // Fetch the Data Object
                DataObject dataObject = dataObjectListAdapter.getItem(i);

                // Ensure the Data Object has Text and Add to the List
                if (dataObject != null && dataObject.getObjectInformation().length() > 0)
                    dataObjectArrayList.add(dataObjectListAdapter.getItem(i));
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
        // Fetch the List VIew
        ListView dataObjectListView = (ListView) rootView.findViewById(R.id.data_object_list_view);

        // Initialize the List Adapter
        dataObjectListAdapter = new DataObjectListAdapter(getActivity().getApplicationContext(), getActivity());

        // Set the Data Objects for the List Adapter
        dataObjectListAdapter.addAll(currentDataProject.getDataObjectList());

        // Set the Adapter for the List View
        dataObjectListView.setAdapter(dataObjectListAdapter);

        // Set the Listener for the Save Needed Flag
        dataObjectListAdapter.setListener(new DataObjectListAdapter.AdapterListener()
        {
            public void setSaveNeededFlag()
            {
                System.out.println("SAVE NEEDED FLAG SET");

                // Set the Save Needed Flag to True
                saveNeeded = true;

                // Redraw the Options Menu
                getActivity().invalidateOptionsMenu();
            }
        });
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
}

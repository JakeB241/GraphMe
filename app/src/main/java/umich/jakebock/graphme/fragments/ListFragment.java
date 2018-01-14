package umich.jakebock.graphme.fragments;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import umich.jakebock.graphme.R;
import umich.jakebock.graphme.classes.DataObject;
import umich.jakebock.graphme.classes.DataProject;
import umich.jakebock.graphme.support_classes.DataObjectListAdapter;

public class ListFragment extends Fragment
{
    private View                    rootView;
    private DataProject             currentDataProject;
    private DataObjectListAdapter   dataObjectListAdapter;

    public ListFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Create the Root View
        rootView = inflater.inflate(R.layout.fragment_list, container, false);

        // Fetch the Data Project
        currentDataProject = (DataProject) getArguments().getSerializable("DATA_PROJECT");

        // Initialize the Data Object List View
        initializeDataObjectListView();

        // Initialize the Floating Action Button
        initializeAddDataObjectButton();

        // Return the Root View
        return rootView;
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

        dataObjectListAdapter.notifyDataSetChanged();
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

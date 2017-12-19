package umich.jakebock.graphme.fragments;


import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import umich.jakebock.graphme.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProjectEditorFragment extends Fragment
{
    public ProjectEditorFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
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
                // TODO Create Project
                System.out.println("Add button clicked");
            }
        });
    }

}

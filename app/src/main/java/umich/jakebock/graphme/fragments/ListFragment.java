package umich.jakebock.graphme.fragments;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import umich.jakebock.graphme.R;

public class ListFragment extends Fragment {

    public ListFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Create the Root View
        View rootView = inflater.inflate(R.layout.fragment_list, container, false);

        // Create the Floating Action Button
        createAddDataObjectButton(rootView);

        // Return the Root View
        return rootView;
    }

    private void createAddDataObjectButton(View view)
    {
        // Create the Floating Action Button
        FloatingActionButton addButton = (FloatingActionButton) view.findViewById(R.id.add_button);

        // Set the Add Button
        addButton.setImageResource(android.R.drawable.ic_input_add);

        // Create the Listener for the Add Button
        addButton.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                // Create the Layout for adding Data Objects
                // TODO
            }
        });
    }
}

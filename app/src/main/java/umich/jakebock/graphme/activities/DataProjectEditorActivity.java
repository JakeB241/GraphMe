package umich.jakebock.graphme.activities;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import umich.jakebock.graphme.R;

public class DataProjectEditorActivity  extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.data_project_editor_activity);

        // Create the Navigation Drawer
        createNavigationDrawer();

        // Create the ToolBar
        createToolbar();

        // Create the Add Project Floating Action Button
        createAddProjectButton();
    }

    private void createNavigationDrawer()
    {
        // Create the Navigation Drawer
        ListView navigationDrawer = (ListView) findViewById(R.id.navigation_drawer);

        // Get the List of Options
        String[] navigationDrawerList = getResources().getStringArray(R.array.navigation_drawer_list);

        // Set the adapter for the list view
        navigationDrawer.setAdapter(new ArrayAdapter<String>(this, R.layout.navigation_drawer_list_item, navigationDrawerList));
    }

    private void createToolbar()
    {
        // Create the ToolBar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        // Set the Support Action Bar
        setSupportActionBar(toolbar);

        // Set the Title
        getSupportActionBar().setTitle(getResources().getString(R.string.toolbar_project_title));

        // Set the Menu Frame
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);

        // Set the Menu Frame
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void createAddProjectButton()
    {
        // Create the Floating Action Button
        FloatingActionButton addProjectButton = (FloatingActionButton) findViewById(R.id.add_project_button);

        // Create the Listener for the Add Button
        addProjectButton.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                // Create the Add Project Functionality
                // Send the User to the Data Project Breakdown Activity post completion
                // TODO
            }
        });
    }
}

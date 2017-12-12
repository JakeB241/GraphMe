package umich.jakebock.graphme.activities;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ListView;

import umich.jakebock.graphme.R;
import umich.jakebock.graphme.support_classes.MyNavigationDrawer;
import umich.jakebock.graphme.support_classes.MyToolBar;

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
        // Create the Custom Navigation Drawer
        MyNavigationDrawer navigationDrawer = new MyNavigationDrawer(this, (ListView) findViewById(R.id.navigation_drawer));

        navigationDrawer.initializeNavigationDrawer();
    }

    private void createToolbar()
    {
        // Set the Support Action Bar
        setSupportActionBar((Toolbar)findViewById(R.id.toolbar));

        // Create the Custom Toolbar
        MyToolBar toolbar = new MyToolBar(getSupportActionBar(), getResources().getString(R.string.toolbar_project_title));

        // Initialize the Toolbar
        toolbar.initializeToolbar();

        // Set the Title with the Current Project
        // TODO
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

package umich.jakebock.graphme.support_classes;

import android.support.v7.app.ActionBar;

import umich.jakebock.graphme.R;

/**
 * Created by Jake on 12/12/2017.
 */

public class MyToolBar
{
    private String title;
    private ActionBar toolbar;

    public MyToolBar(ActionBar toolbar, String title)
    {
        this.title   = title;
        this.toolbar = toolbar;
    }

    public void initializeToolbar()
    {
        // Set the Title
        toolbar.setTitle(title);

        // Set the Menu Frame
        toolbar.setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);

        // Set the Menu Frame
        toolbar.setDisplayHomeAsUpEnabled(true);
    }
}

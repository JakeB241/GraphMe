package umich.jakebock.graphme.support_classes;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import umich.jakebock.graphme.R;

/**
 * Created by Jake on 12/12/2017.
 */

public class MyNavigationDrawer
{
    private Context context;
    private ListView navigationDrawer;

    public MyNavigationDrawer(Context context, ListView navigationDrawer)
    {
        this.context          = context;
        this.navigationDrawer = navigationDrawer;
    }

    public void initializeNavigationDrawer()
    {
        // Get the List of Options
        String[] navigationDrawerList = context.getResources().getStringArray(R.array.navigation_drawer_list);

        // Set the adapter for the list view
        navigationDrawer.setAdapter(new ArrayAdapter<String>(context, R.layout.navigation_drawer_list_item, navigationDrawerList));
    }
}

package umich.jakebock.trackme.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.DataPointInterface;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.OnDataPointTapListener;
import com.jjoe64.graphview.series.PointsGraphSeries;
import com.jjoe64.graphview.series.Series;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import umich.jakebock.trackme.R;
import umich.jakebock.trackme.activities.MainActivity;
import umich.jakebock.trackme.classes.DataObject;
import umich.jakebock.trackme.classes.DataProject;


public class GraphFragment extends Fragment
{
    private View                 rootView;
    private DataProject          currentDataProject;
    private GraphView            graphView;
    private ArrayList<DataPoint> dataObjectList;

    public static final List<String> GRAPH_TYPES = Arrays.asList("Line", "Bar", "Point");

    public GraphFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        rootView = inflater.inflate(R.layout.fragment_graph, container, false);

        // Allow the Fragment to Have a Custom Options Menu
        setHasOptionsMenu(true);

        // Fetch the Data Project
        currentDataProject = ((MainActivity) getActivity()).getCurrentDataProject();

        if (currentDataProject.getDataObjectList().size() > 0)
        {
            // Initialize the Graph View
            initializeGraphView();
        }

        // Return Root View
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        // Fetch the Action Bar
        ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();

        // Set the Action Bar Title to the Current Data Project Title
        if (actionBar != null) actionBar.setTitle(((MainActivity) getActivity()).getCurrentDataProject().getProjectTitle());

        // Create the Custom Graph Fragment Menu
        inflater.inflate(R.menu.graph_fragment_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.action_menu_graph_choice:
                showGraphTypeSelection();
                break;
        }

        return false;
    }

    private void showGraphTypeSelection()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Select Graph Type");
        builder.setItems(GRAPH_TYPES.toArray(new String[GRAPH_TYPES.size()]), new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int position)
            {
                if      (GRAPH_TYPES.indexOf("Line")  == position) createLineGraph();
                else if (GRAPH_TYPES.indexOf("Bar")   == position) createBarGraph();
                else if (GRAPH_TYPES.indexOf("Point") == position) createPointGraph();
            }
        });
        builder.show();
    }

    // Compare Class for the Dates
    private class DataObjectInformationCompare implements Comparator<DataPoint>
    {
        @Override
        public int compare(DataPoint dataPoint1, DataPoint dataPoint2)
        {
            return Double.compare(dataPoint1.getY(), dataPoint2.getY());
        }
    }

    // Compare Class for the Objects
    private class DataObjectDateCompare implements Comparator<DataPoint>
    {
        @Override
        public int compare(DataPoint dataPoint1, DataPoint dataPoint2)
        {
            return Double.compare(dataPoint1.getX(), dataPoint2.getX());
        }
    }

    private void initializeGraphView()
    {
        // Fetch the Graph
        graphView = (GraphView) rootView.findViewById(R.id.graph_view);

        // Create the Data Points
        dataObjectList = new ArrayList<>();
        for (DataObject dataObject : currentDataProject.getDataObjectList())
        {
            // Fetch the Data of the Data Object
            Date dataObjectDate = dataObject.getObjectTime();
            Double dataObjectInformation = Double.parseDouble(dataObject.getObjectInformation());

            // Add the Data Object Information to the List
            dataObjectList.add(new DataPoint(dataObjectDate, dataObjectInformation));
        }

        // Set the Number of Horizontal Labels
        graphView.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(graphView.getContext()));
        //graphView.getGridLabelRenderer().setNumHorizontalLabels(5);
        //graphView.getGridLabelRenderer().setNumVerticalLabels  (20);

        // Fetch the Max/Min Values for the Default Graph
        int minValue = (int) Collections.min(dataObjectList, new DataObjectInformationCompare()).getY();
        int maxValue = (int) Collections.max(dataObjectList, new DataObjectInformationCompare()).getY();

        // Fetch the Max/Min Dates for the Default Graph
        double startDate = Collections.min(dataObjectList, new DataObjectDateCompare()).getX();
        double endDate   = Collections.max(dataObjectList, new DataObjectDateCompare()).getX();

        // Set the X Bounds Manually
        graphView.getViewport().setMinX(startDate);
        graphView.getViewport().setMaxX(endDate);
        graphView.getViewport().setXAxisBoundsManual(true);

        // Set the Y Bounds Manually
        graphView.getViewport().setMinY(Math.floor(minValue));
        graphView.getViewport().setMaxY(Math.ceil (maxValue));
        graphView.getViewport().setYAxisBoundsManual(true);

        // Set the Padding
        graphView.getGridLabelRenderer().setPadding(40);

        // Set Scalable
        graphView.getViewport().setScalable(true);

        // Disable Human Rounding with Dates
        graphView.getGridLabelRenderer().setHumanRounding(false);

        createBarGraph();

        // Create the Graph
        //if      (currentDataProject.findSettingById("DEFAULT_GRAPH").getChosenValue().equals("List"))  createLineGraph();
        //else if (currentDataProject.findSettingById("DEFAULT_GRAPH").getChosenValue().equals("Bar"))   createBarGraph();
        //else if (currentDataProject.findSettingById("DEFAULT_GRAPH").getChosenValue().equals("Point")) createPointGraph();
    }

    private void createLineGraph()
    {
        // Remove all Previous Series
        graphView.removeAllSeries();

        // Create the Series
        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(dataObjectList.toArray(new DataPoint[dataObjectList.size()]));
        series.setDrawAsPath(false);
        series.setDrawDataPoints(false);

        series.setOnDataPointTapListener(dataPointTapListener);

        // Add the Series
        graphView.addSeries(series);
    }

    private void createBarGraph()
    {
        // Remove all Previous Series
        graphView.removeAllSeries();

        // Create the Series
        BarGraphSeries<DataPoint> series = new BarGraphSeries<>(dataObjectList.toArray(new DataPoint[dataObjectList.size()]));
        series.setDrawValuesOnTop(true);
        series.setValuesOnTopColor(Color.RED);
        series.setSpacing(15);

        series.setOnDataPointTapListener(dataPointTapListener);

        // Add the Series
        graphView.addSeries(series);
    }

    private void createPointGraph()
    {
        // Remove all Previous Series
        graphView.removeAllSeries();

        // Create the Series
        PointsGraphSeries<DataPoint> series = new PointsGraphSeries<>(dataObjectList.toArray(new DataPoint[dataObjectList.size()]));

        series.setOnDataPointTapListener(dataPointTapListener);

        // Add the Series
        graphView.addSeries(series);
    }

    private OnDataPointTapListener dataPointTapListener = new OnDataPointTapListener()
    {
        @Override
        public void onTap(Series series, DataPointInterface dataPoint)
        {
            Toast.makeText(getActivity(), Double.toString(dataPoint.getY()), Toast.LENGTH_SHORT).show();
        }
    };
}
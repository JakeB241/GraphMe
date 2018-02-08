package umich.jakebock.trackme.fragments;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.helper.StaticLabelsFormatter;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.DataPointInterface;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.OnDataPointTapListener;
import com.jjoe64.graphview.series.PointsGraphSeries;
import com.jjoe64.graphview.series.Series;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

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

    private int PERCENTAGE_Y_BUFFER = 10;

    public GraphFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        rootView = inflater.inflate(R.layout.fragment_graph, container, false);

        // Allow the Fragment to Have a Custom Options Menu
        setHasOptionsMenu(true);

        // Fetch the Data Project
        currentDataProject = ((MainActivity) getActivity()).getCurrentDataProject();

        // Only Create the Graph if there is more than one Data Object
        if (currentDataProject.getDataObjectList().size() > 1)
        {
            // Create the Graph View
            drawGraphView();
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
            default:
                break;
        }

        return false;
    }

    private void showGraphTypeSelection()
    {
        final CharSequence graphTypes[] = new CharSequence[] {"Line", "Bar", "Point"};

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Select Graph Type");
        builder.setItems(graphTypes, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                switch (which)
                {
                    case 0:
                        createLineGraph();
                        break;
                    case 1:
                        createBarGraph();
                        break;
                    case 2:
                        createPointGraph();
                        break;
                }
            }
        });
        builder.show();
    }

    private class DataObjectInformationCompare implements Comparator<DataPoint>
    {
        @Override
        public int compare(DataPoint dataPoint1, DataPoint dataPoint2)
        {
            return Double.compare(dataPoint1.getY(), dataPoint2.getY());
        }
    }

    private class DataObjectDateCompare implements Comparator<DataPoint>
    {
        @Override
        public int compare(DataPoint dataPoint1, DataPoint dataPoint2)
        {
            return Double.compare(dataPoint1.getX(), dataPoint2.getX());
        }
    }

    private void drawGraphView()
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

            System.out.println("Data Object Time:        " + dataObjectDate);
            System.out.println("Data Object Information: " + dataObjectInformation);

            // Add the Data Object Information to the List
            dataObjectList.add(new DataPoint(dataObjectDate, dataObjectInformation));
        }

        // Ensure there are Enough Data Points
        if (dataObjectList.size() > 1)
        {
            // Set the Number of Horizontal Labels
            graphView.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(graphView.getContext()));
            graphView.getGridLabelRenderer().setNumHorizontalLabels(dataObjectList.size());
            graphView.getGridLabelRenderer().setNumVerticalLabels(dataObjectList.size() + 20);

            // Fetch the Max/Min Values for the Default Graph
            int minValue = (int) Collections.min(dataObjectList, new DataObjectInformationCompare()).getY();
            int maxValue = (int) Collections.max(dataObjectList, new DataObjectInformationCompare()).getY();

            // Fetch the Max/Min Dates for the Default Graph
            double startDate = Collections.min(dataObjectList, new DataObjectDateCompare()).getX();
            double endDate   = Collections.max(dataObjectList, new DataObjectDateCompare()).getX();

            // Calculate the Min/Max for the Viewport
            minValue -= minValue * PERCENTAGE_Y_BUFFER / 100;
            maxValue += maxValue * PERCENTAGE_Y_BUFFER / 100;

            // Set the X Bounds Manually
            graphView.getViewport().setMinX(startDate);
            graphView.getViewport().setMaxX(endDate);
            graphView.getViewport().setXAxisBoundsManual(true);

            // Set the Y Bounds Manually
            graphView.getViewport().setMinY(minValue);
            graphView.getViewport().setMaxY(maxValue);
            graphView.getViewport().setYAxisBoundsManual(true);

            // Set the Scrollable
            graphView.getViewport().setScalable(true);

            // Disable Human Rounding with Dates
            graphView.getGridLabelRenderer().setHumanRounding(false);

            // Create the Series
            createBarGraph();
        }
    }

    private void createLineGraph()
    {
        // Remove all Previous Series
        graphView.removeAllSeries();

        // Create the Series
        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(dataObjectList.toArray(new DataPoint[dataObjectList.size()]));

        series.setOnDataPointTapListener(new OnDataPointTapListener()
        {
            @Override
            public void onTap(Series series, DataPointInterface dataPoint) {
                Toast.makeText(getActivity(), "" + dataPoint.getY(), Toast.LENGTH_SHORT).show();
            }
        });

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

        series.setOnDataPointTapListener(new OnDataPointTapListener() {
            @Override
            public void onTap(Series series, DataPointInterface dataPoint) {
                Toast.makeText(getActivity(), "Series1: On Data Point clicked: " + dataPoint, Toast.LENGTH_SHORT).show();
            }
        });

        // Add the Series
        graphView.addSeries(series);
    }

    private void createPointGraph()
    {
        // Remove all Previous Series
        graphView.removeAllSeries();

        // Create the Series
        PointsGraphSeries<DataPoint> series = new PointsGraphSeries<>(dataObjectList.toArray(new DataPoint[dataObjectList.size()]));

        series.setOnDataPointTapListener(new OnDataPointTapListener() {
            @Override
            public void onTap(Series series, DataPointInterface dataPoint) {
                Toast.makeText(getActivity(), "Series1: On Data Point clicked: " + dataPoint, Toast.LENGTH_SHORT).show();
            }
        });

        // Add the Series
        graphView.addSeries(series);
    }
}
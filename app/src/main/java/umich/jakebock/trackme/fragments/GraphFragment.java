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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LabelFormatter;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.BaseSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.DataPointInterface;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.OnDataPointTapListener;
import com.jjoe64.graphview.series.PointsGraphSeries;
import com.jjoe64.graphview.series.Series;

import java.text.ParseException;
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
import umich.jakebock.trackme.support_classes.DateTimePicker;

import static com.google.android.gms.internal.zzahn.runOnUiThread;


public class GraphFragment extends Fragment
{
    private RelativeLayout       rootView;
    private DataProject          currentDataProject;
    private GraphView            graphView;
    private ArrayList<DataPoint> dataObjectList;
    private String               currentGraphType;

    private Date startDate;
    private Date endDate;

    private Date minimumDate;
    private Date maximumDate;

    public static boolean isFullScreen;

    private final double TIME_TO_DRAW_GRAPH_SECONDS = 0.75;
    private final int    MAX_Y_LABELS               = 15;
    private final int    MAX_X_LABELS               = 15;

    public static final List<String> GRAPH_TYPES = Arrays.asList("Line", "Bar", "Point");

    public GraphFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the Root View
        rootView = (RelativeLayout) inflater.inflate(R.layout.fragment_graph, container, false);

        // Allow the Fragment to Have a Custom Options Menu
        setHasOptionsMenu(true);

        // Return Root View
        return rootView;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser)
    {
        // Call the Super
        super.setUserVisibleHint(isVisibleToUser);

        // Only Draw the Graph when the Fragment is Visible to the User
        if (isVisibleToUser)
        {
            // Fetch the Data Project
            currentDataProject = ((MainActivity) getActivity()).getCurrentDataProject();

            // Default Graph Type
            currentGraphType = "Line";

            // Set the FullScreen Flag
            isFullScreen = false;

            // Ensure there is Data to Graph the Graph View
            if (currentDataProject.getDataObjectList().size() > 0)
                drawGraphView();

                // Message for Not Enough Data
            else
                Toast.makeText(getActivity(), "Enter Data to be Graphed!", Toast.LENGTH_SHORT).show();
        }
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
            // Graph Type Choice
            case R.id.action_menu_graph_choice:
                showGraphTypeSelection();
                break;

            // Date Range Choice
            case R.id.action_menu_date_range:
                showDateRangeSelection();
                break;

            // Date Range Choice
            case R.id.action_menu_toggle_fullscreen:
                toggleFullScreen();
                break;
        }

        return false;
    }

    private void showGraphTypeSelection()
    {
        // Create the Graph Type Alert Dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Select Graph Type");
        builder.setItems(GRAPH_TYPES.toArray(new String[GRAPH_TYPES.size()]), new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int position)
            {
                // Set the Current Graph Type
                currentGraphType = GRAPH_TYPES.get(position);

                // Re Draw the Graph
                drawGraphView();
            }
        });
        builder.show();
    }

    private void calculateMaxMinDates()
    {
        // Calculate the Minimum and Maximum Date
        minimumDate = Collections.min(currentDataProject.getDataObjectList(), DataObject.sortAscendingOrder).getObjectTime();
        maximumDate = Collections.max(currentDataProject.getDataObjectList(), DataObject.sortAscendingOrder).getObjectTime();

        // Set the Default Start Date and End Date to the Minimum and the Maximum Dates
        startDate = minimumDate;
        endDate   = maximumDate;
    }


    private void showDateRangeSelection()
    {
        // Create the Data Object Prompt View
        final View dataObjectPromptView = LayoutInflater.from(getActivity()).inflate(R.layout.date_range_prompt, null);

        // Create the Alert Dialog
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());

        // Set the Message of the Alert Dialog
        alertDialogBuilder.setMessage("Date Range");

        // Fetch the Data Object Components
        final TextView startDateTextView = dataObjectPromptView.findViewById(R.id.start_date);
        final TextView endDateTextView   = dataObjectPromptView.findViewById(R.id.end_date  );

        // Set the Text of the Data Object
        startDateTextView.setText(currentDataProject.returnDateFormat().format(startDate));
        endDateTextView  .setText(currentDataProject.returnDateFormat().format(endDate  ));

        // Set the On Click Listeners for the State Date and End Date Text Views
        startDateTextView.setOnClickListener(dateTextViewClickListener);
        endDateTextView  .setOnClickListener(dateTextViewClickListener);

        // Create the Delete Button
        alertDialogBuilder.setPositiveButton("Save", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                try
                {
                    // Save off the Start Date and End Date
                    startDate = currentDataProject.returnDateFormat().parse(startDateTextView.getText().toString());
                    endDate   = currentDataProject.returnDateFormat().parse(endDateTextView  .getText().toString());

                    // Swap the Dates
                    if (endDate.before(startDate) || startDate.after(endDate))
                    {
                        // Show that the Dates were Swapped
                        Toast.makeText(getActivity(), "Swapped Start Date and End Date", Toast.LENGTH_SHORT).show();

                        // Swap the Dates
                        Date tempDate = startDate;
                        startDate     = endDate;
                        endDate       = tempDate;
                    }

                    // Repopulate the Data Point List
                    drawGraphView();
                }

                catch (ParseException e)
                {
                    e.printStackTrace();
                }
            }
        });

        // Create the Cancel Button
        alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.dismiss();
            }
        });

        // Create the Reset Button
        alertDialogBuilder.setNeutralButton("Reset", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                // Calculate the Minimum and Maximum Date
                calculateMaxMinDates();

                // Repopulate the Data Point List
                drawGraphView();
            }
        });

        alertDialogBuilder.setView(dataObjectPromptView);

        // Show the Alert Dialog
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();

        // Set the Color of the Positive and Negative Button
        alert.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(Color.BLACK);
        alert.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
        alert.getButton(DialogInterface.BUTTON_NEUTRAL) .setTextColor(Color.BLACK);
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

    private void toggleFullScreen()
    {
        // Set the System UI Visibility
        if (!isFullScreen)
        {
            // Fetch the Main App View and the Content View
            RelativeLayout mainAppView = getActivity().findViewById(R.id.main_app_view);
            RelativeLayout contentMain = mainAppView.findViewById(R.id.content_main);

            // Hide the Views
            mainAppView.findViewById(R.id.app_bar_layout).setVisibility(View.GONE);
            mainAppView.findViewById(R.id.adView)        .setVisibility(View.GONE);
            contentMain.findViewById(R.id.tab_layout)    .setVisibility(View.GONE);

            // Set the View to FullScreen
            mainAppView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);

            // Set the FullScreen Flag
            isFullScreen = true;
        }
    }

    private void drawGraphView()
    {
        // Remove all Views
        rootView.removeAllViews();

        // Create a New Graph View
        graphView = new GraphView(getActivity());

        // Add the View
        rootView.addView(graphView);

        // Fetch the Start Date and End Date - Default is the Max/Min
        if (startDate == null && endDate == null)
        {
            // Calculate the Minimum and Maximum Date
            calculateMaxMinDates();
        }

        // Create the Data Points
        dataObjectList = new ArrayList<>();

        // Store the Data Object List
        ArrayList<DataObject> dataObjects = currentDataProject.getDataObjectList();

        // Sort the Data
        Collections.sort(dataObjects, DataObject.sortAscendingOrder);

        // Loop through the List
        for (DataObject dataObject : dataObjects)
        {
            // Fetch the Data of the Data Object
            Date   dataObjectDate        = dataObject.getObjectTime();
            Double dataObjectInformation = Double.parseDouble(dataObject.getObjectInformation());

            // Add the Data Object Information to the List
            if ((dataObjectDate.after(startDate) || dataObjectDate.equals(startDate)) && (dataObjectDate.before(endDate) || dataObjectDate.equals(endDate)))
                dataObjectList.add(new DataPoint(dataObjectDate, dataObjectInformation));
        }

        // Set the Number of Horizontal Labels
        if (dataObjectList.size() >= MAX_Y_LABELS) graphView.getGridLabelRenderer().setNumHorizontalLabels(MAX_Y_LABELS         );
        else                                       graphView.getGridLabelRenderer().setNumHorizontalLabels(dataObjectList.size());

        // Set the Number of Vertical Labels
        if (dataObjectList.size() >= MAX_X_LABELS) graphView.getGridLabelRenderer().setNumVerticalLabels  (MAX_X_LABELS         );
        else                                       graphView.getGridLabelRenderer().setNumVerticalLabels  (dataObjectList.size());

        // Fetch the Max/Min Values for the Default Graph
        double minValue = Collections.min(dataObjectList, new DataObjectInformationCompare()).getY();
        double maxValue = Collections.max(dataObjectList, new DataObjectInformationCompare()).getY();

        // Set the X Bounds Manually
        graphView.getViewport().setMinX(startDate.getTime());
        graphView.getViewport().setMaxX(endDate  .getTime());
        graphView.getViewport().setXAxisBoundsManual(true);

        // Set the Y Bounds Manually
        graphView.getViewport().setMinY(Math.floor(minValue));
        graphView.getViewport().setMaxY(Math.ceil (maxValue));
        graphView.getViewport().setYAxisBoundsManual(true);

        // Create the Grid Label Renderer
        graphView.getGridLabelRenderer().setLabelFormatter(labelFormatter);

        // Rotate the Labels on the X Axis
        graphView.getGridLabelRenderer().setHorizontalLabelsAngle(120);

        // Set the Padding
        graphView.getGridLabelRenderer().setPadding(40);

        // Disable Human Rounding with Dates
        graphView.getGridLabelRenderer().setHumanRounding(false);

        // Create the Graph
        createGraph();
    }

    private void createGraph()
    {
        // Create the Series
        BaseSeries<DataPoint> series = null;
        switch (currentGraphType)
        {
            // Line Graph
            case "Line":

                // Create the Series
                series = new LineGraphSeries<>();

                // Create the Line Graph Settings
                ((LineGraphSeries)series).setDrawDataPoints(true);
                break;

            // Bar Graph
            case "Bar":

                // Create the Series
                series = new BarGraphSeries<>();
                break;

            // Point Graph
            case "Point":

                // Create the Series
                series = new PointsGraphSeries<>();
                break;
        }

        // Add the Series
        if (series != null)
        {
            graphView.addSeries(series);

            // Create the Graph
            final BaseSeries<DataPoint> finalSeries = series;
            new Thread(new Runnable()
            {
                @Override
                public void run()
                {
                    // Sleep until the Graph can be seen
                    try
                    {
                        Thread.sleep(180);
                    }

                    catch (InterruptedException ignored) {}

                    // Loop through the Data Object List
                    for (final DataPoint dataPoint : dataObjectList)
                    {
                        runOnUiThread(new Runnable()
                        {
                            @Override
                            public void run() {
                                finalSeries.appendData(dataPoint, false, dataObjectList.size());
                            }
                        });

                        // Sleep the Thread based on the Size
                        try
                        {
                            Thread.sleep((long) ((TIME_TO_DRAW_GRAPH_SECONDS * 1000) / dataObjectList.size()));
                        }

                        catch (InterruptedException ignored) {}
                    }
                }
            }).start();

            // Set the On Tap Listener
            series.setOnDataPointTapListener(dataPointTapListener);
        }
    }

    // Create the Click Listener for the Date Range
    private View.OnClickListener dateTextViewClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View view)
        {
            // Show the Date Time Picker
            DateTimePicker dateTimePicker = new DateTimePicker(getActivity(), currentDataProject, view, minimumDate, maximumDate);
            dateTimePicker.showDateTimePicker();
        }
    };

    // Label Formatter
    private LabelFormatter labelFormatter = new LabelFormatter()
    {
        @Override
        public String formatLabel(double value, boolean isValueX)
        {
            if (isValueX)
                return android.text.format.DateFormat.getDateFormat(getContext()).format(value);

            else
                return String.valueOf(Math.round(value));
        }

        @Override
        public void setViewport(Viewport viewport) {}
    };

    // Create the Data Point Tap Listener
    private OnDataPointTapListener dataPointTapListener = new OnDataPointTapListener()
    {
        @Override
        public void onTap(Series series, DataPointInterface dataPoint)
        {
            Toast.makeText(getActivity(), android.text.format.DateFormat.getDateFormat(getContext()).format(dataPoint.getX()) + " - " + Double.toString(dataPoint.getY()), Toast.LENGTH_SHORT).show();
        }
    };
}
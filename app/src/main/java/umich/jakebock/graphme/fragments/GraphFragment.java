package umich.jakebock.graphme.fragments;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import umich.jakebock.graphme.R;
import umich.jakebock.graphme.activities.MainActivity;
import umich.jakebock.graphme.classes.DataProject;


public class GraphFragment extends Fragment
{
    private View        rootView;
    private DataProject currentDataProject;

    private Date startDate;
    private Date endDate;

    private int MAX_NUMBER_OF_LABELS;

    public GraphFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        rootView = inflater.inflate(R.layout.fragment_graph, container, false);

        // Fetch the Data Project
        currentDataProject = ((MainActivity)getActivity()).getCurrentDataProject();

        // Set the Graph Range
        createGraphDateRange();

        // Create the Graph View
        initializeGraphView();

        // Return Root View
        return rootView;
    }

    private void initializeGraphView()
    {
        // Fetch the Graph
        GraphView graphView = (GraphView) rootView.findViewById(R.id.graph_view);

        // Create the Data Points
        DataPoint[] dataPoints = new DataPoint[currentDataProject.getDataObjectList().size()];
        for (int i=0; i < currentDataProject.getDataObjectList().size(); i++)
        {
            try
            {
                dataPoints[i] = new DataPoint(MainActivity.dateFormat.parse(currentDataProject.getDataObjectList().get(i).getObjectTime()), Double.parseDouble(currentDataProject.getDataObjectList().get(i).getObjectInformation()));
            }

            catch (ParseException e)
            {
                e.printStackTrace();
            }
        }

        // Add the Series
        graphView.addSeries(new LineGraphSeries<DataPoint>(dataPoints));

        graphView.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(getActivity()));
        graphView.getGridLabelRenderer().setNumHorizontalLabels(4);

        System.out.println(graphView.getGraphContentHeight());
        System.out.println(graphView.getGraphContentWidth());

        // Set the X Bounds Manually
        graphView.getViewport().setMinX(startDate.getTime());
        graphView.getViewport().setMaxX(endDate  .getTime());
        graphView.getViewport().setXAxisBoundsManual(true);

        // Set the Y Bounds Manually
        graphView.getViewport().setMinX(startDate.getTime());
        graphView.getViewport().setMaxX(endDate  .getTime());
        graphView.getViewport().setXAxisBoundsManual(true);

        // Disable Human Rounding with Dates
        graphView.getGridLabelRenderer().setHumanRounding(false);
    }

    private void createGraphDateRange()
    {
        // Ensure there is Data in the Object List
        if (currentDataProject.getDataObjectList().size() >= 1)
        {
            // Fetch the Graph Date Range Text View
            TextView startDateTextView = rootView.findViewById(R.id.start_date_text_view);
            TextView endDateTextView   = rootView.findViewById(R.id.end_date_text_view);

            // Convert the List of Strings to List of Dates
            ArrayList<Date> dateList = new ArrayList<>();
            for (int i = 0; i < currentDataProject.getDataObjectList().size(); i++)
            {
                try
                {
                    dateList.add(MainActivity.dateFormat.parse(currentDataProject.getDataObjectList().get(i).getObjectTime()));
                }

                catch (ParseException e)
                {
                    e.printStackTrace();
                }
            }

            // Create the Date Format
            DateFormat dateFormat = new SimpleDateFormat("M/d/yy", Locale.US);

            // Fetch the Minimum and Maximum Dates by Default
            startDate = Collections.min(dateList);
            endDate   = Collections.max(dateList);

            // Set the Minimum and Maximum to the Start Date and End Date
            startDateTextView.setText(MainActivity.dateFormat.format(startDate));
            endDateTextView  .setText(MainActivity.dateFormat.format(endDate));

            // Set the On Click Listener for the Start Date and End Date
            startDateTextView.setOnClickListener(dateClickListener);
            endDateTextView  .setOnClickListener(dateClickListener);
        }
    }

    // Create the Date Click Listener
    private View.OnClickListener dateClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View view)
        {
            // Parse the Current Time from the View
            final TextView dateLabel = (TextView) view;
            final Calendar calendar  = new GregorianCalendar();
            try
            {
                // Parse the Displayed Date and Set the Time
                Date displayedDate = MainActivity.dateFormat.parse(dateLabel.getText().toString());
                calendar.setTime(displayedDate);
            }

            catch (ParseException e)
            {
                e.printStackTrace();
            }

            // Launch the Date Picker
            DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), R.style.DialogTheme, new DatePickerDialog.OnDateSetListener()
            {
                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
                {
                    // Fetch the Chosen Dates
                    final String chosenDate = (monthOfYear + 1) + " " + (dayOfMonth) + " " + (year);

                    // Launch Time Picker Dialog
                    TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), R.style.DialogTheme, new TimePickerDialog.OnTimeSetListener()
                    {
                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay, int minute)
                        {
                            // Fetch the Chosen Time
                            final String chosenTime = hourOfDay + " " + minute;

                            try
                            {
                                // Create the Date Parser
                                SimpleDateFormat dateParser = new SimpleDateFormat("MM dd yyyy HH mm", Locale.US);

                                // Populate the Text View
                                dateLabel.setText(MainActivity.dateFormat.format(dateParser.parse(chosenDate + " " + chosenTime)));
                            }
                            catch (ParseException e)
                            {
                                e.printStackTrace();
                            }
                        }
                    }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false);

                    timePickerDialog.show();
                }
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

            datePickerDialog.show();
        }
    };
}

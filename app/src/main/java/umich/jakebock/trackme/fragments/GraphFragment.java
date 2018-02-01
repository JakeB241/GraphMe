package umich.jakebock.trackme.fragments;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.os.Handler;
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

import umich.jakebock.trackme.R;
import umich.jakebock.trackme.activities.MainActivity;
import umich.jakebock.trackme.classes.DataProject;


public class GraphFragment extends Fragment
{
    private View        rootView;
    private DataProject currentDataProject;

    private final Handler mHandler = new Handler();

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

        // Only Create the Graph if there is more than one Data Object
        if (currentDataProject.getDataObjectList().size() > 1)
        {
            // Set the Graph Range
            initializeGraphDateRange();

            // Create the Graph View
            drawGraphView();
        }

        // Return Root View
        return rootView;
    }

    private void drawGraphView()
    {
        // Fetch the Graph
        GraphView graphView = (GraphView) rootView.findViewById(R.id.graph_view);

        // Remove all Previous Series
        //graphView.removeAllSeries();
        //graphView.invalidate();

        // Create the Data Points
        DataPoint[] dataPoints = new DataPoint[currentDataProject.getDataObjectList().size()];
        ArrayList<Double> dataObjectInformationList = new ArrayList<>();
        for (int i=0; i < currentDataProject.getDataObjectList().size(); i++)
        {
            // Fetch the Data of the Data Object
            Date   dataObjectDate           = currentDataProject.getDataObjectList().get(i).getObjectTime();
            Double dataObjectInformation    = Double.parseDouble(currentDataProject.getDataObjectList().get(i).getObjectInformation());

            // Add the Data Object Information to the List
            dataObjectInformationList.add(dataObjectInformation);

            // Add the Information to the Data Point If Its in the Specified Range
            if ((dataObjectDate.after(startDate) || dataObjectDate.equals(startDate)) && (dataObjectDate.before(endDate) || dataObjectDate.equals(endDate)))
                dataPoints[i] = new DataPoint(dataObjectDate, dataObjectInformation);

        }

        // Ensure there are Enough Data Points
        if (dataPoints.length > 1)
        {
            // Set the Number of Horizontal Labels
            graphView.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(getActivity()));
            graphView.getGridLabelRenderer().setNumHorizontalLabels(dataPoints.length);
            graphView.getGridLabelRenderer().setNumVerticalLabels  (dataPoints.length);

            // Set the X Bounds Manually
            graphView.getViewport().setMinX(startDate.getTime());
            graphView.getViewport().setMaxX(endDate  .getTime());
            graphView.getViewport().setXAxisBoundsManual(true);

            // Set the Y Bounds Manually
            graphView.getViewport().setMinY(Collections.min(dataObjectInformationList).intValue());
            graphView.getViewport().setMaxY(Collections.max(dataObjectInformationList).intValue());
            graphView.getViewport().setYAxisBoundsManual(true);

            // Disable Human Rounding with Dates
            graphView.getGridLabelRenderer().setHumanRounding(false);

            // Create the Series
            LineGraphSeries<DataPoint> series = new LineGraphSeries<>(dataPoints);
            series.setDrawDataPoints(true);
            series.setAnimated      (true);
            series.setDrawAsPath    (true);

            // Add the Series
            graphView.addSeries(series);
        }
    }

    private void initializeGraphDateRange()
    {
        // Fetch the Graph Date Range Text View
        TextView startDateTextView = rootView.findViewById(R.id.start_date_text_view);
        TextView endDateTextView   = rootView.findViewById(R.id.end_date_text_view);

        // Convert the List of Strings to List of Dates
        ArrayList<Date> dateList = new ArrayList<>();
        for (int i = 0; i < currentDataProject.getDataObjectList().size(); i++)
        {
            dateList.add(currentDataProject.getDataObjectList().get(i).getObjectTime());
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

                                // Fetch the Graph Date Range Text View
                                TextView startDateTextView = rootView.findViewById(R.id.start_date_text_view);
                                TextView endDateTextView   = rootView.findViewById(R.id.end_date_text_view);

                                // Populate the End Date and Start Date
                                startDate = MainActivity.dateFormat.parse(startDateTextView.getText().toString());
                                endDate   = MainActivity.dateFormat.parse(endDateTextView  .getText().toString());

                                // Redraw the Graph View
                                drawGraphView();
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

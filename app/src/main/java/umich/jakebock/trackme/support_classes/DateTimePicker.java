package umich.jakebock.trackme.support_classes;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import umich.jakebock.trackme.R;
import umich.jakebock.trackme.classes.DataProject;

/**
 * Created by Jake on 2/15/2018.
 */

public class DateTimePicker
{
    private Context     context;
    private DataProject dataProject;
    private View        view;
    private Date        startDate;
    private Date        endDate;

    public DateTimePicker(Context context, DataProject dataProject, View view, Date startDate, Date endDate)
    {
        this.context     = context;
        this.dataProject = dataProject;
        this.view        = view;
        this.startDate   = startDate;
        this.endDate     = endDate;
    }

    public void showDateTimePicker()
    {
        // Parse the Current Time from the View
        final TextView updateTimeLabel = (TextView) view;
        final Calendar calendar        = new GregorianCalendar();

        try
        {
            // Parse the Displayed Date and Set the Time
            Date displayedDate = dataProject.returnDateFormat().parse(updateTimeLabel.getText().toString());
            calendar.setTime(displayedDate);
        }

        catch (ParseException e)
        {
            e.printStackTrace();
        }

        // Launch the Date Picker
        DatePickerDialog datePickerDialog = new DatePickerDialog(context, R.style.DialogTheme, new DatePickerDialog.OnDateSetListener()
        {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
            {
                // Fetch the Chosen Dates
                final String chosenDate = (monthOfYear + 1) + " " + (dayOfMonth) + " " + (year);

                // Only show the Time if the Include Time is Set
                if ((Boolean)dataProject.findSettingById("INCLUDE_TIME").getChosenValue())
                {
                    // Launch Time Picker Dialog
                    TimePickerDialog timePickerDialog = new TimePickerDialog(context, R.style.DialogTheme, new TimePickerDialog.OnTimeSetListener()
                    {
                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay, int minute)
                        {
                            // Fetch the Chosen Time
                            final String chosenTime = hourOfDay + " " + minute;

                            try
                            {
                                // Create the Date Parser
                                DateFormat dateParser = new SimpleDateFormat("MM dd yyyy HH mm", Locale.US);

                                // Populate the Text View
                                updateTimeLabel.setText(dataProject.returnDateFormat().format(dateParser.parse(chosenDate + " " + chosenTime)));
                            }

                            catch (ParseException e)
                            {
                                e.printStackTrace();
                            }
                        }
                    }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false);

                    timePickerDialog.show();
                }

                else
                {
                    try
                    {
                        // Create the Date Parser
                        DateFormat dateParser = new SimpleDateFormat("MM dd yyyy", Locale.US);

                        // Populate the Text View
                        updateTimeLabel.setText(dataProject.returnDateFormat().format(dateParser.parse(chosenDate)));
                    }

                    catch (ParseException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        // Set the Minimum and Maximum Dates
        if (startDate != null && endDate != null)
        {
            datePickerDialog.getDatePicker().setMinDate(startDate.getTime());
            datePickerDialog.getDatePicker().setMaxDate(endDate.getTime());
        }

        // Show the Data Picker
        datePickerDialog.show();
    }
}

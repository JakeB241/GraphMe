package umich.jakebock.graphme.support_classes;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import umich.jakebock.graphme.R;
import umich.jakebock.graphme.classes.DataObject;
import umich.jakebock.graphme.classes.DataProject;

/**
 * Created by Jake on 1/10/2018.
 */

public class DataObjectListAdapter extends ArrayAdapter<DataObject> implements View.OnClickListener
{
    private Context context;
    private Context dialogContext;
    private int currentYear;
    private int currentMonth;
    private int currentDay;
    private int currentHour;
    private int currentMinute;

    private DataObjectViewHolder dataObjectViewHolder;

    // Data Object View Holder
    private static class DataObjectViewHolder
    {
        EditText    dataObjectInformation;
        TextView    updateDateTime;
    }

    public DataObjectListAdapter(Context context, Context dialogContext)
    {
        // Call the Super
        super(context, R.layout.data_object_item);

        // Initialize Data
        this.context        = context;
        this.dialogContext  = dialogContext;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent)
    {
        // Get the data object for this position
        DataObject dataObject = getItem(position);

        if (convertView == null)
        {
            dataObjectViewHolder = new DataObjectViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.data_object_item, parent, false);
            dataObjectViewHolder.dataObjectInformation  = (EditText)  convertView.findViewById(R.id.data_object_information);
            dataObjectViewHolder.updateDateTime         = (TextView)  convertView.findViewById(R.id.updated_date);
            convertView.setTag(dataObjectViewHolder);

            // Ensure a Data Object is Found
            if (dataObject != null)
            {
                // Set the View Parameters
                dataObjectViewHolder.dataObjectInformation.setText(dataObject.getObjectInformation());
                dataObjectViewHolder.updateDateTime       .setText(dataObject.getUpdatedTime());

                // Set the Listener for the Updated Date Time Text View
                dataObjectViewHolder.updateDateTime.setOnClickListener(this);
            }
        }

        else
        {
            dataObjectViewHolder = (DataObjectViewHolder) convertView.getTag();

            // Ensure a Data Object is Found
            if (dataObject != null)
            {
                dataObject.setObjectInformation(dataObjectViewHolder.dataObjectInformation.getText().toString());
                dataObject.setUpdatedTime      (dataObjectViewHolder.updateDateTime       .getText().toString());
            }
        }

        // Return the Completed View
        return convertView;
    }

    @Override
    public void onClick(View view)
    {
        showDateAndTimePicker(view);
    }

    private void showDateAndTimePicker(final View chosenView)
    {
        // Parse the Current Time from the View
        final TextView updateTimeLabel = (TextView) chosenView;
        final Calendar calendar = new GregorianCalendar();
        try
        {
            // Parse the Displayed Date and Set the Time
            Date displayedDate = DataProject.dateFormat.parse(updateTimeLabel.getText().toString());
            calendar.setTime(displayedDate);
        }

        catch (ParseException e)
        {
            e.printStackTrace();
        }

        // Launch the Date Picker
        DatePickerDialog datePickerDialog = new DatePickerDialog(dialogContext, R.style.DialogTheme, new DatePickerDialog.OnDateSetListener()
        {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
            {
                // Fetch the Chosen Dates
                final String chosenDate = (monthOfYear + 1) + " " + (dayOfMonth) + " " + (year);

                // Launch Time Picker Dialog
                TimePickerDialog timePickerDialog = new TimePickerDialog(dialogContext, R.style.DialogTheme, new TimePickerDialog.OnTimeSetListener()
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
                            updateTimeLabel.setText(DataProject.dateFormat.format(dateParser.parse(chosenDate + " " + chosenTime)));
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
}

package umich.jakebock.graphme.support_classes;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
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

public class DataObjectListAdapter extends ArrayAdapter<DataObject>
{
    private Context                 context;
    private Context                 dialogContext;
    private DataObjectListAdapterListener dataObjectListAdapterListener;

    // Data Object View Holder
    private static class DataObjectViewHolder
    {
        TextView dataObjectInformationTextView;
        EditText dataObjectInformationEditText;
        TextView dataObjectDateTime;
    }

    public DataObjectListAdapter(Context context, Context dialogContext)
    {
        // Call the Super
        super(context, R.layout.data_object_item);

        // Initialize Data
        this.context        = context;
        this.dialogContext  = dialogContext;
    }

    // Define the Listener
    public interface DataObjectListAdapterListener
    {
        void    setSaveNeeded();
        boolean getActonModeEnabled();
    }

    // Set the Listener for the Adapter
    public void setListener(DataObjectListAdapterListener listener)
    {
        this.dataObjectListAdapterListener = listener;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent)
    {
        // Get the data object for this position
        final DataObject dataObject = getItem(position);

        if (convertView == null)
        {
            final DataObjectViewHolder dataObjectViewHolder = new DataObjectViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.data_object_item, parent, false);
            dataObjectViewHolder.dataObjectInformationTextView = (TextView) convertView.findViewById(R.id.data_object_information_text_view);
            dataObjectViewHolder.dataObjectInformationEditText = (EditText) convertView.findViewById(R.id.data_object_information_edit_text);
            dataObjectViewHolder.dataObjectDateTime            = (TextView) convertView.findViewById(R.id.updated_date);

            // Ensure a Data Object is Found and Isn't a Newly Created Data Object
            if (dataObject != null && dataObject.getObjectInformation().length() > 0)
            {
                // Set the Visbility of the Edit Text and Text View
                dataObjectViewHolder.dataObjectInformationTextView.setVisibility(View.VISIBLE);
                dataObjectViewHolder.dataObjectInformationEditText.setVisibility(View.GONE);
            }

            // Newly Created Data Object
            else
            {
                // Set the Visibility of the Edit Text and Text View
                dataObjectViewHolder.dataObjectInformationTextView.setVisibility(View.GONE);
                dataObjectViewHolder.dataObjectInformationEditText.setVisibility(View.VISIBLE);

                // Set the Focus on the Newly Created Edit Text
                dataObjectViewHolder.dataObjectInformationEditText.requestFocus();
            }

            // Set the View Parameters
            dataObjectViewHolder.dataObjectInformationTextView.setText(dataObject.getObjectInformation());
            dataObjectViewHolder.dataObjectDateTime           .setText(dataObject.getObjectTime());
            dataObjectViewHolder.dataObjectInformationEditText.setTag(dataObjectViewHolder.dataObjectInformationTextView);
            dataObjectViewHolder.dataObjectInformationTextView.setTag(dataObjectViewHolder.dataObjectInformationEditText);

            // Set the Click Listener for the Data Object Text View
            dataObjectViewHolder.dataObjectInformationTextView.setOnClickListener(dataObjectInformationClicked);

            // Set the Click Listener for the Date Time Text View
            dataObjectViewHolder.dataObjectDateTime.setOnClickListener(dataObjectDateTimeClicked);

            // Set the On Text Changed Listener for the Data Object Information
            dataObjectViewHolder.dataObjectInformationEditText.addTextChangedListener(new TextWatcher()
            {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {}

                @Override
                public void afterTextChanged(Editable textString)
                {
                    // Set the Object Information
                    dataObject.setObjectInformation(textString.toString());

                    // Transfer the Text to the Text View
                    dataObjectViewHolder.dataObjectInformationTextView.setText(textString);

                    // Set the Save Needed Flag
                    dataObjectListAdapterListener.setSaveNeeded();
                }
            });

            // Set the On Text Changed Listener for the Data Object Date Time
            dataObjectViewHolder.dataObjectDateTime.addTextChangedListener(new TextWatcher()
            {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {}

                @Override
                public void afterTextChanged(Editable textString)
                {
                    // Set the Object Time
                    dataObject.setObjectTime(textString.toString());

                    // Set the Save Needed Flag
                    dataObjectListAdapterListener.setSaveNeeded();
                }
            });

            // Set the Tag
            convertView.setTag(dataObjectViewHolder);
        }

        else
        {
            // Fetch the Data Object View Holder
            DataObjectViewHolder dataObjectViewHolder = (DataObjectViewHolder) convertView.getTag();
        }

        // Return the Completed View
        return convertView;
    }

    // Create the Listener for the Data Object Date Time
    private View.OnClickListener dataObjectDateTimeClicked = new View.OnClickListener()
    {
        public void onClick(View view)
        {
            // Check if Action Mode is NOT Enabled
            if (!dataObjectListAdapterListener.getActonModeEnabled())
                showDateAndTimePicker(view);
        }
    };

    // Create the Listener for the Data Object Information
    private View.OnClickListener dataObjectInformationClicked = new View.OnClickListener()
    {
        @Override
        public void onClick(View view)
        {
            if (!dataObjectListAdapterListener.getActonModeEnabled())
            {
                // Fetch the Views
                TextView dataObjectInformationTextView = (TextView) view;
                EditText dataObjectInformationEditText = (EditText) view.getTag();

                // Set the Text of the Edit Text
                dataObjectInformationEditText.setText(dataObjectInformationTextView.getText().toString());

                // Set the Edit Text to VISIBLE and Set the Text View to GONE
                dataObjectInformationEditText.setVisibility(View.VISIBLE);
                dataObjectInformationTextView.setVisibility(View.GONE);

                // Set the Text of the Text View
                dataObjectInformationTextView.setText(dataObjectInformationEditText.getText().toString());

                // Request Focus of the Edit View
                dataObjectInformationEditText.requestFocus();
            }
        }
    };

    private void showDateAndTimePicker(final View chosenView)
    {
        // Parse the Current Time from the View
        final TextView updateTimeLabel = (TextView) chosenView;
        final Calendar calendar        = new GregorianCalendar();
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

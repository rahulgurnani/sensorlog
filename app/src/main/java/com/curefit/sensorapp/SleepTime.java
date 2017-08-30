package com.curefit.sensorapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;

import android.widget.TimePicker;

import android.text.format.DateFormat;

import com.curefit.sensorapp.db.DataStoreHelper;

import java.util.Calendar;

/*
SleepTime activity to get user start and end sleep time.
 */
public class SleepTime extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sleep_time);
        final Button startTime= (Button) findViewById(R.id.starttime);
        startTime.setOnClickListener(startTimeListener);
        final Button endTime = (Button) findViewById(R.id.endtime);
        endTime.setOnClickListener(endTimeListener);
    }

    final View.OnClickListener startTimeListener= new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            DialogFragment newFragment = new TimePickerFragment();
            Bundle bundle = new Bundle();
            bundle.putString("name", "start");
            newFragment.setArguments(bundle);
            newFragment.show(getFragmentManager(), "timePicker");
        }
    };

    final View.OnClickListener endTimeListener= new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            DialogFragment newFragment = new TimePickerFragment();
            Bundle bundle = new Bundle();
            bundle.putString("name", "end");
            newFragment.setArguments(bundle);
            newFragment.show(getFragmentManager(), "timePicker");
        }
    };

    public static class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);
            if (this.getArguments().getString("name").equals("start")) {
                hour = 20;
                minute = 0;
            }
            if (this.getArguments().getString("name").equals("end")) {
                hour = 9;
                minute = 0;
            }
            DataStoreHelper dataStoreHelper = DataStoreHelper.getInstance(getActivity());
            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            // Do something with the time chosen by the user
            System.out.println("Time is Hour : " + Integer.toString(hourOfDay) + " minutes : " + Integer.toString(minute) + " for " + this.getArguments().getString("name")) ;
            DataStoreHelper dsh = DataStoreHelper.getInstance(getActivity());
            dsh.addEntryTime(this.getArguments().getString("name"), hourOfDay, minute);
        }
    }
}

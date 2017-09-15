package com.curefit.sensorapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;

import android.widget.TextView;
import android.widget.TimePicker;

import android.text.format.DateFormat;

import com.curefit.sensorapp.data.SleepData;
import com.curefit.sensorapp.db.DataStoreHelper;

import java.util.Calendar;

/*
SleepTime activity to get user start and end sleep time.
 */
public class SleepTimeActivity extends AppCompatActivity {

    int start_hour;
    int end_hour;
    int start_minute;
    int end_minute;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sleep_time);
        final Button startTime= (Button) findViewById(R.id.starttime);
        startTime.setOnClickListener(startTimeListener);
        final Button endTime = (Button) findViewById(R.id.endtime);
        endTime.setOnClickListener(endTimeListener);
        timeViewUpdate();
    }

    private void timeViewUpdate() {
        System.out.println("Time view updated");
        SleepData sleepData = DataStoreHelper.getInstance(this).getSleepData();
//        if (sleepData.getSu()) {
//            TextView updated1 = (TextView) findViewById(R.id.updated1);
//            updated1.setText("Updated");
//        }
//        if (sleepData.getEu()) {
//            TextView updated2 = (TextView) findViewById(R.id.updated2);
//            updated2.setText("Updated");
//        }
        start_hour = sleepData.getHs();
        start_minute = sleepData.getMs();
        end_hour = sleepData.getHe();
        end_minute = sleepData.getMe();
    }

    final View.OnClickListener startTimeListener= new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            timeViewUpdate();
            System.out.println("Begin time pressed");
            DialogFragment newFragment = new TimePickerFragment();
            Bundle bundle = new Bundle();
            bundle.putString("name", "start");
            bundle.putInt("start_hour", start_hour);
            bundle.putInt("start_minute", start_minute);
            newFragment.setArguments(bundle);
            newFragment.show(getFragmentManager(), "timePicker");
        }
    };

    final View.OnClickListener endTimeListener= new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            timeViewUpdate();
            System.out.println("End time pressed");
            DialogFragment newFragment = new TimePickerFragment();
            Bundle bundle = new Bundle();
            bundle.putString("name", "end");
            bundle.putInt("end_hour", end_hour);
            bundle.putInt("end_minute", end_minute);
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
            DataStoreHelper dsh = DataStoreHelper.getInstance(getActivity());

            if (this.getArguments().getString("name").equals("start")) {
                hour = getArguments().getInt("start_hour");
                minute = getArguments().getInt("start_minute");
            }
            if (this.getArguments().getString("name").equals("end")) {
                hour = getArguments().getInt("end_hour");
                minute = getArguments().getInt("end_minute");
            }

            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            // Do something with the time chosen by the user
            System.out.println("Time in Hour : " + Integer.toString(hourOfDay) + " minutes : " + Integer.toString(minute) + " for " + this.getArguments().getString("name")) ;
            DataStoreHelper dsh = DataStoreHelper.getInstance(getActivity());
            dsh.addEntrySleepTime(this.getArguments().getString("name"), hourOfDay, minute);
            System.out.println("Entry add");
        }
    }
}

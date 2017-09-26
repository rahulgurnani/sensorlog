package com.curefit.sensorapp;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.curefit.sensorsdk.SensorSdk;
import com.curefit.sensorsdk.data.User;
import com.curefit.sensorsdk.db.DataStoreHelper;
import com.curefit.sensorsdk.sync.SyncAdapter;
import com.curefit.sensorsdk.sync.SyncUtils;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    DataStoreHelper dataStoreHelper;
    EditText nameText, emailText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button loginButton = (Button) findViewById(R.id.login);
        loginButton.setOnClickListener(loginButtonListener);

        dataStoreHelper = DataStoreHelper.getInstance(this);
        User user = dataStoreHelper.getUser();
        nameText = (EditText) findViewById(R.id.nameText);
        emailText = (EditText) findViewById(R.id.emailText);
        if(user!=null) {
            // we have a user in the database
            if (user.getName() != null && user.getEmail()!= null) {
                performLogin(user, false);
            }
            if (user.getName()!= null) {
                nameText.setText(user.getName());
            }
            if (user.getEmail()!= null){
                emailText.setText(user.getEmail());
            }
        }
    }

    private void performLogin(User user, boolean firstTime) {
        Log.d("SensorApp", "performLogin function called");
        // starting service
        SensorSdk.initialize(this).setDeviceId(user.getName()).setUserId(user.getEmail()).build();
        SensorSdk.startService(firstTime);

        if (!(dataStoreHelper.getSleepData().getSu() && dataStoreHelper.getSleepData().getEu())) {
            scheduleNotification();
        }
        Intent intent = new Intent(MainActivity.this, ViewDataActivity.class);
        startActivity(intent.putExtra("name", user.getName()));

        finish();       // because we don't want to go back to this activity if back is pressed.
    }

    final View.OnClickListener loginButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String name = nameText.getText().toString();
            String email = emailText.getText().toString();
            if (name.isEmpty() || name == null) {
                Context context = getApplicationContext();
                Toast.makeText(context, "Please Enter Name", Toast.LENGTH_SHORT);
                return;
            }
            if (email.isEmpty() || email == null) {
                Context context = getApplicationContext();
                Toast.makeText(context, "Please Enter Email", Toast.LENGTH_SHORT);
                return;
            }

            performLogin(new User(name, email), true);
        }
    };

    private void scheduleNotification() {
        Intent intent = new Intent(this, NotificationPublisher.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 10);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
    }
}

package com.curefit.sensorapp;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    DataStoreHelper dataStoreHelper;
    GlobalVariable globalVariable;
    EditText nameText, emailText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button loginButton = (Button) findViewById(R.id.login);
        loginButton.setOnClickListener(loginButtonListener);

        dataStoreHelper = new DataStoreHelper(this);
        User user = dataStoreHelper.getUser();
        nameText = (EditText) findViewById(R.id.nameText);
        emailText = (EditText) findViewById(R.id.emailText);
        if(user!=null) {
            if (user.name != null && user.email != null) {
                performLogin(user);
            }
            if (user.name != null) {
                nameText.setText(user.name);
            }
            if (user.email != null){
                emailText.setText(user.email);
            }
        }
        scheduleNotification();

    }

    private void performLogin(User user) {
        Context context = getApplicationContext();

        globalVariable = GlobalVariable.getInstance();
        globalVariable.setUser(user);
        globalVariable.setContext(context);

        startActivity(new Intent(MainActivity.this, ViewDataActivity.class));
    }

    final View.OnClickListener loginButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            System.out.println("Login pressed");
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
            // schedule notification for recording the sleeping time of the user.

            // adding user to database
            dataStoreHelper.addUser(name, email);
            // setting user globally
            performLogin(new User(name, email));
        }
    };

    private void scheduleNotification() {
        System.out.println("scheduled notification");

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

        System.out.println("Alarm set");
    }


}

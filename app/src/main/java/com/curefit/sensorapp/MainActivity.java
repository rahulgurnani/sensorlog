package com.curefit.sensorapp;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;

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
            // adding user to database
            dataStoreHelper.addUser(name, email);
            // setting user globally
            performLogin(new User(name, email));
        }
    };
}

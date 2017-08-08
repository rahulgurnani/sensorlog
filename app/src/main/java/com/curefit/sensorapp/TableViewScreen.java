package com.curefit.sensorapp;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.TableRow.LayoutParams;

import java.util.List;

public class TableViewScreen extends Activity {
    TableLayout tl;
    TableRow tr;
    TextView state, timestamp;
    DataStoreHelper dsh;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dsh = new DataStoreHelper(this);
        setContentView(R.layout.activity_table_view);
        tl = (TableLayout) findViewById(R.id.maintable);
        addHeaders();

        List<SensorData> list =  dsh.getAllDataScreen();

        addData(list);
    }
    public void addHeaders() {
        tr = new TableRow(this);
        tr.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

        TextView timestamp = new TextView(this);
        timestamp.setText("Timestamp");
        timestamp.setTextColor(Color.GRAY);
        timestamp.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        timestamp.setPadding(5,5,5,0);
        tr.addView(timestamp);

        TextView state = new TextView(this);
        state.setText("State");
        state.setTextColor(Color.GRAY);
        state.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        state.setPadding(5,5,5,0);
        tr.addView(state);
        // Add the TableRow to the TableLayout
        tl.addView(tr, new TableLayout.LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT));

        // we are adding two textviews for the divider because we have two columns
        tr = new TableRow(this);
        tr.setLayoutParams(new LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT));

        /** Creating another textview **/
        TextView divider = new TextView(this);
        divider.setText("-----------------");
        divider.setTextColor(Color.GREEN);
        divider.setPadding(5, 0, 0, 0);
        divider.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        tr.addView(divider); // Adding textView to tablerow.

        TextView divider2 = new TextView(this);
        divider2.setText("-------------------------");
        divider2.setTextColor(Color.GREEN);
        divider2.setPadding(5, 0, 0, 0);
        divider2.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        tr.addView(divider2); // Adding textView to tablerow.

        // Add the TableRow to the TableLayout
        tl.addView(tr, new TableLayout.LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT));
    }
    public void addData(List<SensorData> list) {
        for(int i = 0; i < list.size(); i++) {
            tr = new TableRow(this);
            tr.setLayoutParams(new LayoutParams(
                    LayoutParams.MATCH_PARENT,
                    LayoutParams.WRAP_CONTENT));

            timestamp = new TextView(this);
            timestamp.setText(list.get(i).getTimestamp());
            timestamp.setTextColor(Color.GRAY);
            timestamp.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
            timestamp.setPadding(5,5,5,0);
            tr.addView(timestamp);

            state = new TextView(this);
            state.setText(Integer.toString(list.get(i).getScreenValue()));
            state.setTextColor(Color.GRAY);
            state.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
            state.setPadding(5,5,5,0);
            tr.addView(state);
            // Add the TableRow to the TableLayout
            tl.addView(tr, new TableLayout.LayoutParams(
                    LayoutParams.MATCH_PARENT,
                    LayoutParams.WRAP_CONTENT));

        }
    }
}

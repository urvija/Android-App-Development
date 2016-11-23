package edu.sdsu.cs.cs646.assignment2;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TimePicker;
import android.widget.Toast;

public class TimeActivity extends AppCompatActivity
{
    private final static String TAG = "TimeActivity";

    private TimePicker mTimeSelector;
    private Button mSetTime;
    private AlertDialog mAlertDialog;
    private String format = "";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time);

        mTimeSelector = (TimePicker) findViewById(R.id.timePicker);
        mSetTime = (Button) findViewById(R.id.setTimeButton);

        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mSetTime.setOnClickListener(new OnClickListener() {

            @TargetApi(Build.VERSION_CODES.M)
            public void onClick(View v) {
                final int hour = mTimeSelector.getHour();
                final int min  = mTimeSelector.getMinute();
                String format = showTime(hour);

                AlertDialog.Builder dialogBuilder = createAlertDialog(hour, min, format);
                mAlertDialog = dialogBuilder.show();
            }
        });
    }

    public String showTime(int hour) {
        if (hour == 0) {
            format = "AM";
        }
        else if (hour == 12) {
            format = "PM";
        } else if (hour > 12) {
            format = "PM";
        } else {
            format = "AM";
        }
        return format;
    }

    @TargetApi(Build.VERSION_CODES.M)
    public void onResume() {
        super.onResume();
        SharedPreferences timePreference = getSharedPreferences(Assignment2Constants.PREFS_NAME, 0);
        int savedHour = timePreference.getInt(Assignment2Constants.PREF_KEY_HOUR, 0);
        int savedMinute = timePreference.getInt(Assignment2Constants.PREF_KEY_MINUTE, 0);

        Log.d(TAG,"Time Picker: "+savedHour+ " " +savedMinute+ " " );
        if(savedHour > 0) {
            mTimeSelector.setHour(savedHour) ;
            mTimeSelector.setMinute(savedMinute);
        }
    }

    public void onStop() {
        super.onStop();
        if(mAlertDialog != null && mAlertDialog.isShowing()) {
            mAlertDialog.cancel();
        }
    }

    private AlertDialog.Builder createAlertDialog(final int hour, final int minute, final String
            format) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(TimeActivity.this);
        dialogBuilder.setTitle(R.string.confirm_time_title)
                .setMessage(getApplicationContext().getResources().getString(R.string
                        .confirm_time_message, hour, minute, format))
                .setNegativeButton(android.R.string.cancel, null)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d(TAG, "Time Picker: " + hour + " " + minute + " " + format + " ");
                        saveTime(hour, minute, format);

                    }
                });
        return dialogBuilder;
    }

    private void saveTime(final int hour, final int minute, final String format) {
        SharedPreferences singlePreferance = getSharedPreferences(Assignment2Constants.PREFS_NAME, 0);
        SharedPreferences.Editor editPreferance = singlePreferance.edit();
        editPreferance.putInt(Assignment2Constants.PREF_KEY_HOUR, hour);
        editPreferance.putInt(Assignment2Constants.PREF_KEY_MINUTE, minute);
        editPreferance.putString(Assignment2Constants.PREF_KEY_FORMAT, format);

        editPreferance.commit();

        Toast.makeText(TimeActivity.this, R.string.time_is_set, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy()");
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

package edu.sdsu.cs.cs646.assignment2;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

public class KeyboardActivity extends AppCompatActivity {

    private final static String TAG = "KeyboardActivity";

    private EditText mTopEditText;
    private Button mBackSetTimeButton;
    private Button mHideSetTimeButton;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_keyboard);

        mTopEditText = (EditText) findViewById(R.id.topEditText);
        mBackSetTimeButton = (Button) findViewById(R.id.backSetTimeButton);
        mHideSetTimeButton = (Button) findViewById(R.id.hideSetTimeButton);

        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        Intent intent = getIntent();
        String receivedString = intent.getStringExtra(Assignment2Constants.KEY_ACTIVITY_DATA);

        if(receivedString != null && receivedString.isEmpty() == false) {
            mTopEditText.setText(receivedString);
            Log.d(TAG, "String Received");
        }

        mBackSetTimeButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mHideSetTimeButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager manager;
                manager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                manager.hideSoftInputFromWindow(mTopEditText.getWindowToken(), 0);
            }
        });
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

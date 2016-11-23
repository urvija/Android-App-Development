package edu.sdsu.cs.cs646.lifecycleevents;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class LifeCycleActivity extends Activity implements OnClickListener
{
    private static final String TAG = "LifeCycleActivity";
    private static final String STRING_KEY = "";

    private TextView mActivityTextView;
    private Button mClearButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_life_cycle);

        mActivityTextView = (TextView) findViewById(R.id.command_text_view);
        mClearButton = (Button) findViewById(R.id.clear_button);

        if (savedInstanceState != null) {
            String savedText = savedInstanceState.getString(STRING_KEY);
            mActivityTextView.append(savedText);
        }

        String onCreateString = getResources().getString(R.string.on_create);
        mActivityTextView.append(onCreateString);
        mActivityTextView.append("\n");
        Log.d(TAG, onCreateString);

        mClearButton.setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        String onStartString = getResources().getString(R.string.on_start);

        Log.d(TAG, onStartString);
        mActivityTextView.append(onStartString);
        mActivityTextView.append("\n");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        String onRestartString = getResources().getString(R.string.on_restart);

        Log.d(TAG, onRestartString);
        mActivityTextView.append(onRestartString);
        mActivityTextView.append("\n");
    }

    @Override
    protected void onPause() {
        super.onPause();
        String onPauseString = getResources().getString(R.string.on_pause);

        Log.d(TAG, onPauseString);
        mActivityTextView.append(onPauseString);
        mActivityTextView.append("\n");
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        String onSaveString = getResources().getString(R.string.on_save);

        mActivityTextView.append(onSaveString);
        mActivityTextView.append("\n");

        Log.d(TAG, onSaveString);
        savedInstanceState.putString(STRING_KEY, mActivityTextView.getText().toString());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        String onRestoreString = getResources().getString(R.string.on_restore);

        Log.i(TAG, onRestoreString);
        mActivityTextView.append(onRestoreString);
        mActivityTextView.append("\n");
    }

    @Override
    protected void onResume() {
        super.onResume();
        String onResumeString = getResources().getString(R.string.on_resume);

        Log.d(TAG, onResumeString);
        mActivityTextView.append(onResumeString);
        mActivityTextView.append("\n");
    }

    @Override
    public void onClick(View v) {
        mActivityTextView.setText("");
    }
}

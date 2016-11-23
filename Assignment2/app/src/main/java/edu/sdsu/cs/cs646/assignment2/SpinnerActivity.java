package edu.sdsu.cs.cs646.assignment2;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

public class SpinnerActivity extends AppCompatActivity
{
    private final static String TAG = "SpinnerActivity";

    private Spinner mActivitySelector;
    private EditText mEditText;
    private Button mNavigateButton;
    private CountryListFragment mCountryListFragment;

    private Intent mActivityIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        setContentView(R.layout.activity_spinner);

        mActivitySelector = (Spinner) findViewById(R.id.activitySpinner);
        mEditText = (EditText) findViewById(R.id.editText);
        mNavigateButton = (Button) findViewById(R.id.navigateButton);

        setSpinner();
        setCountryListFragment();

        mNavigateButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedActivity = mActivitySelector.getSelectedItemPosition();

                switch (selectedActivity) {
                    case 0:
                        goToTimeActivity();
                        break;

                    case 1:
                        goToKeyboardActivity();
                        break;

                    case 2:
                        goToListActivity();
                        return;
                }
            }
        });
    }

    private void goToTimeActivity() {
        mActivityIntent = new Intent(SpinnerActivity.this, TimeActivity.class);
        updateIntentForInput(mActivityIntent);
        startActivity(mActivityIntent);
    }

    private void goToKeyboardActivity() {
        mActivityIntent = new Intent(SpinnerActivity.this, KeyboardActivity.class);
        updateIntentForInput(mActivityIntent);
        startActivity(mActivityIntent);
    }

    private void goToListActivity() {
        mActivityIntent = new Intent(SpinnerActivity.this, ListActivity.class);
        updateIntentForInput(mActivityIntent);
        startActivityForResult(mActivityIntent, Assignment2Constants.SPINNER_ACTIVITY_REQUEST_KEY);
    }

    private Intent updateIntentForInput(Intent intent) {
        String userString = mEditText.getText().toString();
        if(userString.isEmpty() == false) {
            intent.putExtra(Assignment2Constants.KEY_ACTIVITY_DATA, userString);
        }
        return intent;
    }

    //reading the data passed back from ListActivity
    protected void onActivityResult (int requestCode, int resultCode, Intent data) {
        if(data != null) {
            int position = data.getIntExtra(Assignment2Constants.KEY_POSITION, -1);
            String name = data.getStringExtra(Assignment2Constants.KEY_ACTIVITY_DATA);
            Log.d(TAG, "Name: " + name);
            if(position > -1)  {
                mCountryListFragment.getListView().setItemChecked(position,true);
            }
        }
    }

    public void onResume() {
        super.onResume();
        setTimeTextFromMemory();
    }

    private void setTimeTextFromMemory() {
        SharedPreferences timePreferance = getSharedPreferences(Assignment2Constants.PREFS_NAME, 0);
        int savedHour = timePreferance.getInt(Assignment2Constants.PREF_KEY_HOUR, 0);
        int savedMinute = timePreferance.getInt(Assignment2Constants.PREF_KEY_MINUTE, 0);
        String savedFormat = timePreferance.getString(Assignment2Constants.PREF_KEY_FORMAT, "");

        if(savedHour > 0) {
            mEditText.setText(savedHour + ":" + savedMinute + " " + savedFormat + " ");
        }
    }

    private void setSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.activity_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        mActivitySelector.setAdapter(adapter);
    }

    //adding fragment to SpinnerActivity
    //the view is listFragmentContainer
    private void setCountryListFragment() {
        FragmentManager manager = getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        mCountryListFragment = new CountryListFragment();
        transaction.add(R.id.listFragmentContainer, mCountryListFragment);
        transaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId()) {
            case R.id.time_activity:
                goToTimeActivity();
                return true;
            case R.id.keyboard_activity:
                goToKeyboardActivity();
                return true;
            case R.id.list_activity:
                goToListActivity();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}

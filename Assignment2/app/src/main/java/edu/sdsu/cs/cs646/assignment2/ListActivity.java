package edu.sdsu.cs.cs646.assignment2;

import android.app.ActionBar;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class ListActivity extends AppCompatActivity {

    private static final String TAG = "ListActivity";

    private Button mBackButton;
    private CountryListFragment mListFragment;

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        mBackButton = (Button) findViewById(R.id.back);

        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        setListFragment();

        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResultAndFinish();
            }
        });
    }

    private void setResultAndFinish() {

        if (mListFragment != null) {

            Intent resultIntent = getIntent();

            if(mListFragment.getListView().getCheckedItemPosition() == 0) {
                resultIntent.putExtra(Assignment2Constants.KEY_ACTIVITY_DATA, "India");
                resultIntent.putExtra(Assignment2Constants.KEY_POSITION, 0);
                setResult(RESULT_OK, resultIntent);
                Log.d(TAG, resultIntent.getStringExtra(Assignment2Constants.KEY_ACTIVITY_DATA));
            }
            if(mListFragment.getListView().isItemChecked(1)) {
                resultIntent.putExtra(Assignment2Constants.KEY_ACTIVITY_DATA, "USA");
                setResult(RESULT_OK, resultIntent);
                Log.d(TAG, resultIntent.getStringExtra(Assignment2Constants.KEY_ACTIVITY_DATA));
            }
            if(mListFragment.getListView().isItemChecked(2)) {
                resultIntent.putExtra(Assignment2Constants.KEY_ACTIVITY_DATA, "Mexico");
                setResult(RESULT_OK, resultIntent);
                Log.d(TAG, resultIntent.getStringExtra(Assignment2Constants.KEY_ACTIVITY_DATA));
            }
        }
        finish();
    }

    private void setListFragment() {
        FragmentManager manager = getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        mListFragment = new CountryListFragment();
        transaction.add(R.id.listFragment, mListFragment);
        transaction.commit();
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




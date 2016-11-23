package cs646.coolcab;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.Calendar;

public class InformationForRideActivity extends AppCompatActivity {

    private static final String TAG = "InfoForRideActivity";

    private static TextView mDate;
    private static TextView mTime;
    private static TextView mMiles;
    private static TextView mFare;

    private static Button mSetDateButton;
    private static Button mSetTimeButton;

    private static int mSetDate;
    private static int mSetMonth;
    private static int mSetYear;
    private static int mCurrentDate;
    private static int mCurrentMonth;
    private static int mCurrentYear;
    private static int mSetHour;
    private static int mSetMinute;

    private static Spinner mNoOfPassengers;
    private static AlertDialog mAlertDialog;
    private Firebase mFireBaseReference;
    private Firebase mCancelReference;

    private String mUserId;
    private String mReservationsUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information_for_ride);
        Firebase.setAndroidContext(this);

        mMiles = (TextView) findViewById(R.id.totalMiles);
        mFare = (TextView) findViewById(R.id.totalFare);
        mNoOfPassengers = (Spinner)findViewById(R.id.noOfPassengersSpinner);
        mDate = (TextView)findViewById(R.id.selectedDate);
        mTime = (TextView)findViewById(R.id.selectedTime);

        mFireBaseReference = new Firebase("https://coolcab.firebaseio.com");
        try {
            mUserId = mFireBaseReference.getAuth().getUid();
        }
        catch (Exception e) {
            loadSignInActivity();
        }

        mReservationsUrl = "https://coolcab.firebaseio.com/users/"+mUserId+"/reservations";

        mCancelReference = new Firebase(mReservationsUrl);

        Intent fromIntent = getIntent();
        mMiles.setText(fromIntent.getStringExtra(CoolCabConstants.TOTAL_DISTANCE));
        mFare.setText(fromIntent.getStringExtra(CoolCabConstants.TOTAL_FARE));

        setSpinner(this, mNoOfPassengers, R.array.passenger_array, null);

        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mSetDateButton = (Button)findViewById(R.id.datePicker);
        mSetDateButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker(v);
            }
        });

        mSetTimeButton = (Button)findViewById(R.id.timePicker);
        mSetTimeButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePicker(v);
            }
        });

        setTodaysDate();

        android.support.v7.app.ActionBar menu = getSupportActionBar();
        menu.setDisplayShowHomeEnabled(true);
        menu.setLogo(R.mipmap.ic_launcher);
        menu.setDisplayUseLogoEnabled(true);
        menu.setTitle("  " + "CoolCab");
    }

    private void setSpinner(Context context, Spinner spinner, int arrayID, AdapterView
            .OnItemSelectedListener listener) {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(context, arrayID,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        if(listener != null) {
            spinner.setOnItemSelectedListener(listener);
        }
    }

    public void showDatePicker(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(InformationForRideActivity.this.getFragmentManager(), "datePicker");
    }

    public void showTimePicker(View v) {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(InformationForRideActivity.this.getFragmentManager(), "timePicker");
    }

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            String selectedDayByUser;
            String selectedMonthByUser;
            mSetDate = day;
            mSetMonth = month+1;
            mSetYear = year;

            //if days are from 1 to 9 then it will set 0 before them so 01, 02 etc
            if(mSetDate < 10) {
                selectedDayByUser = "0"+mSetDate;
            }
            else {
                selectedDayByUser = ""+mSetDate;
            }
            //if months are from 1 to 9 then it will set 0 before them so 01, 02 etc
            if(mSetMonth < 10) {
                selectedMonthByUser = "0"+mSetMonth;
            }
            else {
                selectedMonthByUser = ""+mSetMonth;
            }
            mDate.setText("" + selectedMonthByUser + "/" + selectedDayByUser + "/" + mSetYear);
        }
    }

    public static class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default time in the picker
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            return new TimePickerDialog(getActivity(), this, hour, minute, DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hour, int minute) {
            String am_pm = "";
            int hourOfDay;

            if(hour == 12) {
                am_pm = "PM";
                hourOfDay = 12;
            }
            else if(hour == 0) {
                am_pm = "AM";
                hourOfDay = 12;
            }
            else if(hour > 12) {
                am_pm = "PM";
                hourOfDay = hour - 12;
            }
            else {
                am_pm = "AM";
                hourOfDay = hour;
            }

            mSetMinute = minute;
            mSetHour = hour;
            String selectedHourByUser;
            String selectedMinuteByUser;

            if(hourOfDay < 10) {
                selectedHourByUser = "0"+hourOfDay;
            }
            else {
                selectedHourByUser = ""+hourOfDay;
            }

            if(minute < 10) {
                selectedMinuteByUser = "0"+minute;
            }
            else {
                selectedMinuteByUser = ""+minute;
            }

            mTime.setText(selectedHourByUser + ":" + selectedMinuteByUser + " " + am_pm);
        }
    }

    private void setTodaysDate() {
        Calendar c = Calendar.getInstance();
        mCurrentYear = c.get(Calendar.YEAR);
        mCurrentMonth = c.get(Calendar.MONTH)+1;
        mCurrentDate = c.get(Calendar.DAY_OF_MONTH);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_reserve:
                goToReservationActivity();
                return true;
            case R.id.action_cancel_reservation:
                cancelReservation();
                return true;
            case R.id.action_logout:
                successfulLogout();
                return true;
            case android.R.id.home:
                mSetMonth = 0;
                mSetDate = 0;
                mSetYear = 0;
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void goToReservationActivity() {
        String passengers = mNoOfPassengers.getSelectedItem().toString();
        String date = mDate.getText().toString();
        String time = mTime.getText().toString();

        if(isValidDate() == false) {
            return;
        }
        if(isValidTime() == false) {
            return;
        }

        Intent activityIntent = getIntent();
        Intent intent = new Intent(InformationForRideActivity.this, ReservationActivity.class);
        intent.putExtra(CoolCabConstants.NO_OF_PASSENGERS, passengers);
        intent.putExtra(CoolCabConstants.DATE, date);
        intent.putExtra(CoolCabConstants.TIME, time);
        intent.putExtra(CoolCabConstants.TO_ADDRESS, activityIntent.getStringExtra(CoolCabConstants.TO_ADDRESS));
        intent.putExtra(CoolCabConstants.FROM_ADDRESS, activityIntent.getStringExtra(CoolCabConstants.FROM_ADDRESS));
        intent.putExtra(CoolCabConstants.TOTAL_DISTANCE, activityIntent.getStringExtra(CoolCabConstants.TOTAL_DISTANCE));
        intent.putExtra(CoolCabConstants.TOTAL_FARE, activityIntent.getStringExtra(CoolCabConstants.TOTAL_FARE));

        startActivity(intent);
    }

    private boolean isValidDate() {
        boolean isError = false;

        if(mSetYear < mCurrentYear) {
            isError = true;
        }
        else if(mSetYear == mCurrentYear && mSetMonth < mCurrentMonth) {
            isError = true;
        }
        else if(mSetYear == mCurrentYear && mSetMonth == mCurrentMonth && mSetDate < mCurrentDate) {
            isError = true;
        }

        if(isError) {
            mAlertDialog = CoolCabHelper.showErrorAlertDialog(InformationForRideActivity.this, R.string.error, R.string
                    .date_error, true, R.string.ok, null, 0, null);
            mAlertDialog.show();
            return false;
        }
        return true;
    }

    private boolean isValidTime() {

        if(mTime.getText().toString().trim().equals("")) {
            mAlertDialog = CoolCabHelper.showErrorAlertDialog(InformationForRideActivity.this, R.string.error, R.string
                    .time_error, true, R.string.ok, null, 0, null);
            mAlertDialog.show();
            return false;
        }

        else if(mSetYear == mCurrentYear && mSetMonth == mCurrentMonth && mSetDate ==
                mCurrentDate) {
            final Calendar c = Calendar.getInstance();
            long currentTimeInMillies = c.getTimeInMillis();
            Calendar setCalendar = Calendar.getInstance();
            setCalendar.set(mSetYear, mSetMonth-1, mSetDate, mSetHour, mSetMinute);
            long setTimeInMillies = setCalendar.getTimeInMillis();

            if(setTimeInMillies - currentTimeInMillies < CoolCabConstants.THRESHOLD_TIME) {
                mAlertDialog = CoolCabHelper.showErrorAlertDialog(InformationForRideActivity.this, R.string.error, R.string
                        .time_error_threshold, true, R.string.ok, null, 0, null);
                mAlertDialog.show();
                return false;
            }
        }
        return true;
    }

    private void cancelReservation() {
        try {
            mCancelReference.orderByKey().addListenerForSingleValueEvent(new ValueEventListener() {
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.hasChildren()) {
                        DataSnapshot firstChild = dataSnapshot.getChildren().iterator().next();
                        firstChild.getRef().removeValue();
                        mAlertDialog = CoolCabHelper.showErrorAlertDialog(InformationForRideActivity.this, R.string
                                .cancel_reservation_success_title, R
                                .string.cancel_reservation_success_message, true, R.string
                                .ok, null, 0, null);
                        mAlertDialog.show();
                    } else {
                        mAlertDialog = CoolCabHelper.showErrorAlertDialog(InformationForRideActivity.this, R
                                .string.cancel_reservation_error_title, R
                                .string.cancel_reservation_error_message, true, R.string.ok, null, 0, null);
                        mAlertDialog.show();
                    }
                }
                public void onCancelled(FirebaseError firebaseError) {
                }
            });
        }
        catch (Exception e){
        }
    }

    private void successfulLogout() {
        mFireBaseReference.unauth();
        loadSignInActivity();
    }

    private void loadSignInActivity() {

        Toast.makeText(InformationForRideActivity.this, R.string.log_out_message, Toast
                .LENGTH_SHORT).show();

        Intent intent = new Intent(this, SignInActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        cancelAlertDialog();
    }

    private void cancelAlertDialog() {
        if(mAlertDialog != null) {
            mAlertDialog.cancel();
            mAlertDialog = null;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(CoolCabHelper.isNetworkConnected(this) == false) {
            mAlertDialog = CoolCabHelper.getNetworkErrorAlertDialog(this);
            mAlertDialog.show();
            return;
        }
        else {
            cancelAlertDialog();
        }
    }
}

package cs646.coolcab;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

public class ReservationActivity extends AppCompatActivity {

    private static final String TAG = "ReservationActivity";

    private EditText mFirstName;
    private EditText mLastName;
    private EditText mPhoneNumber;

    private AlertDialog mAlertDialog;

    private Firebase mFireBaseReference;
    private Firebase mEmailReference;

    private boolean mIsLeftForMail;

    private String mUserId;
    private String mReservationsUrl;

    private String mEmailId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation);
        Firebase.setAndroidContext(this);

        mFireBaseReference = new Firebase("https://coolcab.firebaseio.com");

        try {
            mUserId = mFireBaseReference.getAuth().getUid();
        }
        catch (Exception e) {
            loadSignInActivity();
        }

        mReservationsUrl = "https://coolcab.firebaseio.com/users/"+mUserId+"/reservations";

        //getting registered emailId from FireBase to send email
        mEmailReference = new Firebase("https://coolcab.firebaseio.com/users/"+mUserId+"/email");
        try {
            mEmailReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    mEmailId = dataSnapshot.getValue().toString().trim();
                }
                @Override
                public void onCancelled(FirebaseError firebaseError) {
                }
            });
        }
        catch(Exception e) {
        }

        mFirstName = (EditText)findViewById(R.id.firstNameEditText);
        mLastName = (EditText) findViewById(R.id.lastNameEditText);
        mPhoneNumber = (EditText)findViewById(R.id.phoneNumber);

        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        android.support.v7.app.ActionBar menu = getSupportActionBar();
        menu.setDisplayShowHomeEnabled(true);
        menu.setLogo(R.mipmap.ic_launcher);
        menu.setDisplayUseLogoEnabled(true);
        menu.setTitle("  " + "Make Reservation");

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.reservation_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_reserve:
                validate();
                return true;
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_logout:
                successfulLogout();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void validate() {

        if(mFirstName.getText().toString().trim().equals("")) {
            mAlertDialog = CoolCabHelper.showErrorAlertDialog(ReservationActivity.this, R.string.error, R.string.first_name_error_message, true, R.string.ok, null, 0, null);
            mAlertDialog.show();
            return;
        }

        if(mLastName.getText().toString().trim().equals("")) {
            mAlertDialog = CoolCabHelper.showErrorAlertDialog(ReservationActivity.this, R.string.error, R.string.last_name_error_message, true, R.string.ok, null, 0, null);
            mAlertDialog.show();
            return;
        }

        if(mPhoneNumber.equals("") || mPhoneNumber.length() < 10) {
            mAlertDialog = CoolCabHelper.showErrorAlertDialog(ReservationActivity.this, R.string.error, R.string.phone_error, true, R.string.ok, null, 0, null);
            mAlertDialog.show();
            return;
        }

        goToEmail();
    }

    private void goToEmail() {

        String subjectString;
        String subjectTitle;

        Intent activityIntent = getIntent();
        String toAddress = activityIntent.getStringExtra(CoolCabConstants.TO_ADDRESS).replace("+", " ");
        String fromAddress = activityIntent.getStringExtra(CoolCabConstants.FROM_ADDRESS).replace("+", " ");
        String passengers = activityIntent.getStringExtra(CoolCabConstants.NO_OF_PASSENGERS);
        String date = activityIntent.getStringExtra(CoolCabConstants.DATE);
        String time = activityIntent.getStringExtra(CoolCabConstants.TIME);

        UserData userData = new UserData(mFirstName.getText().toString().trim()+ " " +mLastName.getText().toString().trim(), mPhoneNumber.getText().toString().trim(),
                toAddress, fromAddress, date, time);
        new Firebase(mReservationsUrl).push().setValue(userData);

        mAlertDialog = CoolCabHelper.showErrorAlertDialog(ReservationActivity.this, R.string
                .reservation_success_title, R
                .string.reservation_success_message, true, R.string
                .ok, null, 0, null);
        mAlertDialog.show();

        subjectTitle = getString(R.string.reservation_confirmation);
        subjectString = "From Address: "+fromAddress+"\n\n"
                +"To Address: "+toAddress+"\n\n\n"
                +"Information:: \n\n"
                +"Name: "+ mFirstName.getText().toString().trim()+ " " +
                mLastName.getText().toString().trim()+"\n\n"
                +"Phone: "+ mPhoneNumber.getText().toString().trim()+"\n\n"
                +"Passengers: "+passengers+"\n\n"
                +"Date: "+date+"\n\n"
                +"Time: "+time+"\n\n"
                +"Driver will contact you 10 minutes before the scheduled ride."+"\n"
                +"Appreciate your Business,"+"\n\n"
                +"Team CoolCab";
        sendEmail(subjectString, subjectTitle);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(mIsLeftForMail) {
            mIsLeftForMail = false;
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mAlertDialog != null) {
            mAlertDialog.dismiss();
            mAlertDialog = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mIsLeftForMail = false;
    }

    protected void sendEmail(String emailText, String emailSubject) {
        CoolCabHelper.hideKeyboard(ReservationActivity.this);

        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.putExtra(Intent.EXTRA_SUBJECT, emailSubject);
        intent.putExtra(Intent.EXTRA_TEXT, emailText);
        intent.setData(Uri.parse("mailto:"+ mEmailId));

        ComponentName emailApp = intent.resolveActivity(getPackageManager());
        ComponentName unsupportedAction = ComponentName.unflattenFromString("com.android.fallback/.Fallback");
        if (emailApp != null && !emailApp.equals(unsupportedAction)) {
            try {
                mIsLeftForMail = true;
                Intent chooser = Intent.createChooser(intent, "Choose an email client from...");
                startActivity(chooser);
                Log.i("Finished sending email...", "");
                return;
            }
            catch (android.content.ActivityNotFoundException ex) {
            }
        }
        else {
            Toast.makeText(ReservationActivity.this, "There is no email client active.", Toast
                    .LENGTH_SHORT).show();
        }
    }

    private void successfulLogout() {
        mFireBaseReference.unauth();
        loadSignInActivity();
    }

    //This navigates to the Login view and clears the activity stack.
    //This prevents the user going back to the main activity when they press the Back button from the login view.
    private void loadSignInActivity() {

        Toast.makeText(ReservationActivity.this, R.string.log_out_message, Toast
                .LENGTH_SHORT).show();

        Intent intent = new Intent(this, SignInActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}


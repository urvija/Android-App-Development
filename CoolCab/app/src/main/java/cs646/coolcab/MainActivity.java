package cs646.coolcab;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private EditText mPickUpAddress;
    private EditText mDropOffAddress;
    private EditText mPickUpAddressZip;
    private EditText mDropOffAddressZip;

    private String mDropOffAddressZipCodeString;
    private String mPickUpAddressZipCodeString;
    private String mPickUpPoint;
    private String mDropOffPoint;
    private String mTotalMiles;
    private String mTotalFare;

    private RadioGroup mTripType;
    private RadioGroup mCarType;

    private boolean mIsGoToInformationForRideActivity;

    private ProgressDialog mProgressDialog;
    private AlertDialog mAlertDialog;
    private VolleyQueue mVolleyQueue;

    private Firebase mFireBaseReference;
    private Firebase mCancelReference;

    private String mUserId;
    private String mReservationsUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        setContentView(R.layout.activity_main);
        Firebase.setAndroidContext(this);

        mFireBaseReference = new Firebase("https://coolcab.firebaseio.com");

        // getAuth() checks if the user is authenticated or not. If not it takes you back to
        // login activity
        if (mFireBaseReference.getAuth() == null) {
            loadSignInActivity();
        }
        else {

            mUserId = mFireBaseReference.getAuth().getUid();

            this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

            mPickUpAddress = (EditText) findViewById(R.id.pickUpAddress);
            mDropOffAddress = (EditText) findViewById(R.id.dropOffAddress);
            mPickUpAddressZip = (EditText) findViewById(R.id.pickUpZip);
            mDropOffAddressZip = (EditText) findViewById(R.id.dropOffZip);
            mCarType = (RadioGroup) findViewById(R.id.radioTrip);
            mTripType = (RadioGroup) findViewById(R.id.radioRide);
            mVolleyQueue = new VolleyQueue(getApplicationContext());

            ActionBar actionBar = getActionBar();
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(true);
            }
            setAddress();
        }

        mReservationsUrl = "https://coolcab.firebaseio.com/users/"+mUserId+"/reservations";

        mCancelReference = new Firebase(mReservationsUrl);

        android.support.v7.app.ActionBar menu = getSupportActionBar();
        menu.setDisplayShowHomeEnabled(true);
        menu.setLogo(R.mipmap.ic_launcher);
        menu.setDisplayUseLogoEnabled(true);
        menu.setTitle("  " + "CoolCab");
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
                CoolCabHelper.hideKeyboard(MainActivity.this);
                if(!validateData()) {
                    return true;
                }
                mIsGoToInformationForRideActivity = true;
                return true;
            case R.id.action_logout:
                successfulLogout();
                return true;
            case R.id.action_cancel_reservation:
                cancelReservation();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private boolean validateData() {
        mPickUpPoint = mPickUpAddress.getText().toString().trim();
        mDropOffPoint = mDropOffAddress.getText().toString().trim();
        mPickUpAddressZipCodeString = mPickUpAddressZip.getText().toString().trim();
        mDropOffAddressZipCodeString = mDropOffAddressZip.getText().toString().trim();
        boolean isCorrectAddress;
        boolean isCorrectZip;

        if(mPickUpPoint.equals("") == false && mDropOffPoint.equals("") == false) {
            mPickUpPoint = mPickUpPoint.replace(" ", "+");
            mDropOffPoint = mDropOffPoint.replace(" ", "+");
            isCorrectAddress = true;
        }
        else {
            isCorrectAddress = false;
        }

        if(mPickUpAddressZipCodeString.equals("") == false && mDropOffAddressZipCodeString.equals("") == false) {
            isCorrectZip = true;
        }
        else {
            isCorrectZip = false;
        }

        if(isCorrectAddress && isCorrectZip) {
            getDataAndUpdate(mPickUpPoint + "+" + mPickUpAddressZipCodeString, mDropOffPoint + "+" + mDropOffAddressZipCodeString);
        }
        else if(isCorrectAddress == false) {
            mAlertDialog = CoolCabHelper.showErrorAlertDialog(MainActivity.this, R.string.error, R.string
                    .address_error, true, R.string.ok, null, 0, null);
            mAlertDialog.show();
            return false;
        }
        else if(isCorrectZip == false) {
            mAlertDialog = CoolCabHelper.showErrorAlertDialog(MainActivity.this, R.string.error, R.string
                    .zip_error, true, R.string.ok, null, 0, null);
            mAlertDialog.show();
            return false;
        }
        CoolCabHelper.hideKeyboard(MainActivity.this);
        return true;
    }

    private void getDataAndUpdate(String pickUpPoint, String dropOffPoint) {
        String url = "https://maps.googleapis.com/maps/api/distancematrix/json?origins="+pickUpPoint+"&destinations="+dropOffPoint+"&mode=driving";

        mProgressDialog = new ProgressDialog(this, ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setMessage(getResources().getString(R.string.progress_message));
        mProgressDialog.setOnCancelListener(new OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                finish();
            }
        });
        mProgressDialog.show();
        Response.Listener<JSONObject> success = new Response.Listener<JSONObject>() {
            public void onResponse(JSONObject response) {
                dismissProgressBar();
                setDistanceAndTotalPrice(response);
            }
        };
        Response.ErrorListener failure = new Response.ErrorListener() {
            public void onErrorResponse(VolleyError error) {
                dismissProgressBar();
                showWrongErrorDialog();
            }
        };
        JsonObjectRequest getRequest = new JsonObjectRequest(url, null, success, failure);
        mVolleyQueue.add(getRequest);
    }

    private void dismissProgressBar()  {
        if(mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    private void setDistanceAndTotalPrice(JSONObject jsonObject) {
        try {
            JSONArray rows = jsonObject.getJSONArray("rows");
            JSONObject elements = rows.getJSONObject(0);
            JSONArray elementArr = elements.getJSONArray("elements");

            int distance = elementArr.getJSONObject(0).getJSONObject("distance").getInt("value");
            double distanceInMiles = (distance * 0.62137f)/1000;
            distanceInMiles = Math.round(distanceInMiles * 100.0) / 100.0;

            int id = mCarType.getCheckedRadioButtonId();
            if(id == R.id.roundTrip) {
                distanceInMiles = 2 * distanceInMiles;
            }

            int selectedCar = mTripType.getCheckedRadioButtonId();
            double multiplyBy;
            if(selectedCar == R.id.economy) {
                multiplyBy = 1.5;
            }
            else if(selectedCar == R.id.premium) {
                multiplyBy = 2.5;
            }
            else {
                multiplyBy = 4;
            }
            double total = distanceInMiles * multiplyBy;

            mTotalMiles = ""+distanceInMiles+" miles";
            mTotalFare = ""+distanceInMiles+" x "+multiplyBy+" = $"+Math.round(total* 100.0) / 100.0;

            if(mIsGoToInformationForRideActivity) {
                goToInformationForRideActivity();
                mIsGoToInformationForRideActivity = false;
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
            showWrongErrorDialog();
        }
    }

    private void showWrongErrorDialog() {
        mAlertDialog = CoolCabHelper.showErrorAlertDialog(MainActivity.this, R.string.error, R.string
                .address_not_found_error, true, R.string.ok, null, 0, null);
        mAlertDialog.show();
    }

    private void goToInformationForRideActivity() {
        Intent intent = new Intent(MainActivity.this, InformationForRideActivity.class);
        intent.putExtra(CoolCabConstants.FROM_ADDRESS, mPickUpPoint +" "+ mPickUpAddressZipCodeString);
        intent.putExtra(CoolCabConstants.TO_ADDRESS, mDropOffPoint +" "+ mDropOffAddressZipCodeString);
        intent.putExtra(CoolCabConstants.TOTAL_DISTANCE, mTotalMiles);
        intent.putExtra(CoolCabConstants.TOTAL_FARE, mTotalFare);
        startActivity(intent);
    }

    private void cancelReservation() {
        try {
            mCancelReference.orderByKey().addListenerForSingleValueEvent(new ValueEventListener() {
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.hasChildren()) {
                        DataSnapshot firstChild = dataSnapshot.getChildren().iterator().next();
                        firstChild.getRef().removeValue();
                        mAlertDialog = CoolCabHelper.showErrorAlertDialog(MainActivity.this, R.string
                                .cancel_reservation_success_title, R
                                .string.cancel_reservation_success_message, true, R.string
                                .ok, null, 0, null);
                        mAlertDialog.show();
                    } else {
                        mAlertDialog = CoolCabHelper.showErrorAlertDialog(MainActivity.this, R
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

    //This navigates to the Login view and clears the activity stack.
    //This prevents the user going back to the main activity when they press the Back button from the login view.
    private void loadSignInActivity() {

        Toast.makeText(MainActivity.this, R.string.log_out_message, Toast
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

    private void setAddress() {
        mPickUpAddress.setText("");
        mPickUpAddressZip.setText("");
        mDropOffAddress.setText("");
        mDropOffAddressZip.setText("");
    }
}
package cs646.assignment4;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

public class PhotoAndDescActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "PhotoDescActivity";

    static final int REQUEST_TAKE_PHOTO = 1;
    static final long INTERVAL = 1000 * 10;
    static final long FASTEST_INTERVAL = 1000 * 5;

    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;

    Button mGetPictureButton;
    Button mSubmitButton;
    Button mCancelButton;

    AlertDialog mAlertDialog;
    ProgressDialog mProgressDialog;
    ArrayAdapter<String> adapter;

    ArrayList mCategoryList;
    ArrayList mArrayList;

    String mLatitude;
    String mLongitude;
    String mImageType;
    String mType;
    String binaryToString;

    VolleyQueue mVolleyQueue;

    TextView mTextViewLatitude;
    TextView mTextViewLongitude;
    EditText mDescriptionText;
    ImageView mImageView;
    Spinner mDropDown;
    Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_and_description);

        mVolleyQueue = new VolleyQueue(getApplicationContext());
        mCategoryList = new ArrayList<String>();

        getCategories();

        mDropDown = (Spinner)findViewById(R.id.spinner1);
        mTextViewLatitude = (TextView) findViewById(R.id.latitudeDisplay);
        mTextViewLongitude = (TextView) findViewById(R.id.longitudeDisplay);

        mGetPictureButton = (Button) findViewById(R.id.uploadImage);
        mGetPictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent(v);
            }
        });

        mImageView = (ImageView) findViewById(R.id.photoDisplay);
        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent(v);
            }
        });

        mDescriptionText = (EditText) findViewById(R.id.editText1);

        mSubmitButton =(Button) findViewById(R.id.submit);
        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postData();
            }
        });

        mCancelButton =(Button) findViewById(R.id.cancel);
        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        buildGoogleApiClient();
    }

    public void getCategories() {

        String url ="http://bismarck.sdsu.edu/city/categories";

        mProgressDialog = new ProgressDialog(this, ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setMessage(getResources().getString(R.string.load_categories));
        mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                finish();
            }
        });
        mProgressDialog.show();

        Response.Listener<JSONArray> success = new Response.Listener<JSONArray>() {
            public void onResponse(JSONArray response) {
                dismissProgressBar();
                updateCategory(response);
            }
        };
        Response.ErrorListener failure = new Response.ErrorListener() {
            public void onErrorResponse(VolleyError error) {
                dismissProgressBar();
                Log.e(TAG, error.toString());
            }
        };

        JsonArrayRequest getRequest = new JsonArrayRequest(url, success, failure);
        mVolleyQueue.add(getRequest);
    }

    private void updateCategory(JSONArray data) {
        mCategoryList = getCategoryList(data);
        updateCategory();
    }

    private ArrayList getCategoryList(JSONArray jsonArray) {
        mArrayList = new ArrayList();
        mArrayList.add("Choose Report Type");
        for(int i= 0; i< jsonArray.length(); i++){
            try {
                mArrayList.add(jsonArray.get(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return mArrayList;
    }

    private void updateCategory() {
        adapter = new ArrayAdapter<String>(this, android.R.layout
                .simple_spinner_dropdown_item, mArrayList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mDropDown.setAdapter(adapter);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        mDropDown.setOnItemSelectedListener(new MyOnItemSelectedListener());

        if(Assignment4Helpers.isNetworkConnected(this)) {
            cancelAlertDialog();
        }
        else
        {
            OnClickListener positiveButtonListener = new OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    PhotoAndDescActivity.this.finish();
                }
            };
            cancelAlertDialog();
            mAlertDialog = Assignment4Helpers.showAlertDialog(this, R.string.info_error_title, R.string
                    .network_error_message, false, R.string.ok,positiveButtonListener, 0, null);
        }
    }

    public class MyOnItemSelectedListener implements AdapterView.OnItemSelectedListener {

        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
            switch (pos) {
                case 0:
                    mType = String.valueOf(mArrayList.get(2));
                    break;
                case 1:
                    OnClickListener positiveButtonListener = new OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mDropDown.setAdapter(adapter);
                        }
                    };
                    cancelAlertDialog();
                    mAlertDialog = Assignment4Helpers.showAlertDialog(PhotoAndDescActivity.this, R
                            .string.info_error_title, R.string
                            .report_type_error, false, R.string.ok,positiveButtonListener, 0, null);
                    break;
                case 2:
                    mType = String.valueOf(mArrayList.get(pos));
                    break;
            }
        }
        public void onNothingSelected(AdapterView parent) {
            mType = String.valueOf(mArrayList.get(2));
        }
    }

    protected void buildGoogleApiClient() {
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
        }
        createLocationRequest();
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setNumUpdates(1);
    }

    public void displayCurrentLocation() {

        int permissionCheckCoarse = ActivityCompat.checkSelfPermission(this, Manifest.permission
                .ACCESS_COARSE_LOCATION);
        int permissionCheckFine = ActivityCompat.checkSelfPermission(this, Manifest.permission
                .ACCESS_COARSE_LOCATION);

        if (permissionCheckCoarse == PackageManager.PERMISSION_GRANTED || permissionCheckFine == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.getLocationAvailability(mGoogleApiClient);
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    mLatitude = String.valueOf(location.getLatitude());
                    mTextViewLatitude.setText(mLatitude);
                    mLongitude = String.valueOf(location.getLongitude());
                    mTextViewLongitude.setText(mLongitude);
                }
            });
        }
        else if(permissionCheckCoarse == PackageManager.PERMISSION_DENIED || permissionCheckFine == PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 200);
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 200);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 200: {
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    displayCurrentLocation();
                }
            }
        }
    }

    @Override
    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d(TAG, "onConnected - isConnected ...............: " + mGoogleApiClient.isConnected());
        displayCurrentLocation();
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "Connection failed: " + connectionResult.toString());
    }

    public String filename() {
        return "JPEG_FILE.jpg";
    }

    private File imageFile() {
        File externalFilesDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if ( externalFilesDir == null ) {
            return null;
        }
        return new File(externalFilesDir, filename());
    }

    public void dispatchTakePictureIntent (View button) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = imageFile();
            if (photoFile != null) {
                uri = Uri.fromFile(photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            File photoFile = imageFile();
            if (photoFile != null) {
                updatePhotoView(photoFile);
                try {
                    binaryToString = upload(photoFile);
                }
                catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void updatePhotoView(File photoFile) {
        if(photoFile == null || !photoFile.exists()) {
            mImageView.setImageDrawable(null);
        }
        else {
            Bitmap bm = ImageUtilities.getScaledBitmap(photoFile.getPath(), this);
            mImageView.setImageBitmap(bm);
        }
    }

    private String upload(File photoFile) throws FileNotFoundException {
        // Image location URL
        String picturePath = photoFile.getPath();
        Bitmap bm = BitmapFactory.decodeFile(picturePath);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] ba = baos.toByteArray();
        String ba1 = Base64.encodeToString(ba, Base64.NO_WRAP);
        return ba1;
    }

   public void postData() {

        JSONObject data = new JSONObject();
        try {

            mProgressDialog = new ProgressDialog(this, ProgressDialog.STYLE_SPINNER);
            mProgressDialog.setMessage(getResources().getString(R.string.submit_info));
            mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    finish();
                }
            });
            mProgressDialog.show();

            data.put("type", mType);

            if(mLatitude == null) {
                data.put("latitude", 32.7687458);
            }
            else {
                data.put("latitude", Double.valueOf(mLatitude));
            }

            if(mLongitude == null) {
                data.put("longitude", -117.0581417);
            }
            else {
                data.put("longitude", Double.valueOf(mLongitude));
            }

            data.put("user", Assignment4Constants.USER);

            if(mImageView.getDrawable() == null) {
                mImageType = "none";
            }
            else {
                mImageType = "jpg";
            }

            data.put("imagetype", mImageType);

            if(TextUtils.isEmpty(mDescriptionText.getText())) {
                data.put("description", "none");
            }
            else {
                data.put("description", mDescriptionText.getText());
            }

            if(mImageType == "none") {
            }
            else {
                data.put("image", binaryToString);
            }

        } catch (JSONException error) {
            Log.d(TAG, "JSON Error" + error);
            return;
        }

        Response.Listener<JSONObject> success = new Response.Listener<JSONObject>() {
            public void onResponse(JSONObject response) {
                Log.i(TAG, response.toString());
                dismissProgressBar();
                showSuccessOrErrorDialog(true);
            }
        };

        Response.ErrorListener failure = new Response.ErrorListener() {
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "Post Fail: " + new String(error.networkResponse.data));
                dismissProgressBar();
                showSuccessOrErrorDialog(false);
            }
        };

        String url ="http://bismarck.sdsu.edu/city/report";
        JsonObjectRequest postRequest = new JsonObjectRequest(url, data, success, failure);
        mVolleyQueue.add(postRequest);
   }

    private void showSuccessOrErrorDialog(boolean success)
    {
        if(success == false)
        {
            OnClickListener positiveButtonClickListener = new OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            };
            OnClickListener negativeButtonListener = new OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    PhotoAndDescActivity.this.finish();
                }
            };

            cancelAlertDialog();
            mAlertDialog = Assignment4Helpers.showAlertDialog(PhotoAndDescActivity.this, R.string
                    .post_error_title, R.string
                    .post_error_message, true,R.string.ok, positiveButtonClickListener,R.string.cancel, negativeButtonListener);
        }
        else
        {
            mImageView.setImageDrawable(null);
            mDescriptionText.setText("");
            mDropDown.setAdapter(adapter);

            OnClickListener positiveButtonClickListener = new OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            };
            OnClickListener negativeButtonListener = new OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    PhotoAndDescActivity.this.finish();
                }
            };
            cancelAlertDialog();
            mAlertDialog = Assignment4Helpers.showAlertDialog(PhotoAndDescActivity.this, R.string
                    .post_success_title, R.string.post_success_message, true, R.string.continue_post,
                    positiveButtonClickListener,R.string.cancel, negativeButtonListener);
        }
    }

    private void cancelAlertDialog() {
        if(mAlertDialog != null && mAlertDialog.isShowing()) {
            mAlertDialog.cancel();
        }
    }

    private void dismissProgressBar() {
        if(mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }
}

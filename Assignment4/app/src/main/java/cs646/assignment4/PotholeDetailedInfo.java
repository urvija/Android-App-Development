package cs646.assignment4;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;

public class PotholeDetailedInfo extends AppCompatActivity {

    private static final String TAG = "PotholeDetailedInfo";

    ImageView mImageViewValue;
    TextView mTextLongitudeValue;
    TextView mTextLatitudeValue;
    TextView mTextUserValue;
    TextView mTextDescriptionValue;

    String mPosition;
    String mPotholeID;
    String mLongitudeValue;
    String mLatitudeValue;
    String mUserValue;
    String mDescriptionValue;

    Button mButton;

    VolleyQueue mVolleyQueue;
    ProgressDialog mProgressDialog;
    AlertDialog mAlertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pothole_detailed_info);

        mPosition = getIntent().getStringExtra(Assignment4Constants.POSITION);
        mPotholeID = getIntent().getStringExtra(Assignment4Constants.ID);
        mLatitudeValue = getIntent().getStringExtra(Assignment4Constants.LATITUDE);
        mLongitudeValue = getIntent().getStringExtra(Assignment4Constants.LONGITUDE);
        mDescriptionValue = getIntent().getStringExtra(Assignment4Constants.DESCRIPTION);
        mUserValue = getIntent().getStringExtra(Assignment4Constants.CREATION_DATE_TIME);

        mImageViewValue = (ImageView) findViewById(R.id.potholeImage);
        mTextLatitudeValue = (TextView)findViewById(R.id.latitudeValue);
        mTextLongitudeValue = (TextView)findViewById(R.id.longitudeValue);
        mTextUserValue = (TextView)findViewById(R.id.userValue);
        mTextDescriptionValue = (TextView)findViewById(R.id.descriptionValue);

        mVolleyQueue = new VolleyQueue(getApplicationContext());

        mButton = (Button) findViewById(R.id.goToMapSingleMarker);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startMap(mPosition, mLatitudeValue, mLongitudeValue);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(Assignment4Helpers.isNetworkConnected(this)) {
            setPotholeInfo();
            setInfoData();
        }
        else {
            DialogInterface.OnClickListener positiveButtonListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    PotholeDetailedInfo.this.finish();
                }
            };
            cancelAlertDialog();
            mAlertDialog = Assignment4Helpers.showAlertDialog(this, R.string.info_error_title, R.string
                    .network_error_message, false, R.string.ok,positiveButtonListener, 0, null);
        }
    }

    private void setPotholeInfo() {

        String url = "http://bismarck.sdsu.edu/city/image?id="+mPotholeID;

        mProgressDialog = new ProgressDialog(this, ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setMessage(getResources().getString(R.string.load_info));
        mProgressDialog.setOnCancelListener(new OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                finish();
            }
        });
        mProgressDialog.show();

        Response.Listener<Bitmap> success = new Response.Listener<Bitmap>() {
            public void onResponse(Bitmap response) {
                dismissProgressBar();
                mImageViewValue.setImageBitmap(response);
            }
        };

        Response.ErrorListener failure = new Response.ErrorListener() {
            public void onErrorResponse(VolleyError error) {
                dismissProgressBar();
                Log.e(TAG, error.toString());
            }
        };

        ImageRequest imageToDisplay = new ImageRequest(url, success, 300, 400, ImageView.ScaleType
                .CENTER_INSIDE, null, failure);
        mVolleyQueue.add(imageToDisplay);
    }

    private void setInfoData() {
        mTextLatitudeValue.setText(mLatitudeValue);
        mTextLongitudeValue.setText(mLongitudeValue);
        mTextDescriptionValue.setText(mDescriptionValue);
        mTextUserValue.setText(mUserValue);
    }

    private void startMap(String id, String latitude, String longitude) {
        Intent intent = new Intent(this, MapsActivity.class);
        intent.putExtra("id", id);
        intent.putExtra("latitude", latitude);
        intent.putExtra("longitude", longitude);

        startActivity(intent);
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

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        dismissProgressBar();
    }
}

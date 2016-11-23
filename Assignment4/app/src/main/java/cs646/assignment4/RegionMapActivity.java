package cs646.assignment4;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class RegionMapActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap
        .OnCameraChangeListener {

    private static final String TAG = "RegionMapActivity";

    private GoogleMap mMap;

    //making boundary of San Diego
    Double mStartLatitude = 32.612803;
    Double mEndLatitude = 33.090420;
    Double mStartLongitude = -117.292961;
    Double mEndLongitude = -116.853507;

    private VolleyQueue mVolleyQueue;

    List<String> mPotholeList;
    ArrayList mArrayList;

    String mID;
    String mDescription;
    double mLatitude;
    double mLongitude;
    String mCreated;
    String mImageType;

    AlertDialog mAlertDialog;
    ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        mVolleyQueue = new VolleyQueue(getApplicationContext());
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Assignment4Helpers.isNetworkConnected(this)) {
            getPotholesListAndUpdate();
        } else {
            DialogInterface.OnClickListener positiveButtonListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    RegionMapActivity.this.finish();
                }
            };
            cancelAlertDialog();
            mAlertDialog = Assignment4Helpers.showAlertDialog(this, R.string.info_error_title, R.string
                        .network_error_message, false, R.string.ok,positiveButtonListener, 0, null);
        }
    }

    private void getPotholesListAndUpdate() {

        String url = "http://bismarck.sdsu" +
                ".edu/city/fromLocation?type=street&start-latitude=" + mStartLatitude +
                "&end-latitude=" + mEndLatitude + "&start-longitude=" + mStartLongitude +
                "&end-longitude=" + mEndLongitude;

        mProgressDialog = new ProgressDialog(this, ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setMessage(getResources().getString(R.string.load_potholes));
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
                updateMap(response);
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

    private void updateMap(JSONArray data) {
        mPotholeList = getPotholeList(data);
        updateMap();
    }

    private void updateMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private List<String> getPotholeList(JSONArray jsonArray) {
        List<String> potholeList = new ArrayList<String>();
        mArrayList = new ArrayList();

        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                mID = jsonArray.getJSONObject(i).getString("id");
                mLatitude = jsonArray.getJSONObject(i).getDouble("latitude");
                mLongitude = jsonArray.getJSONObject(i).getDouble("longitude");
                mDescription = jsonArray.getJSONObject(i).getString("description");
                mCreated = jsonArray.getJSONObject(i).getString("created");
                mImageType = jsonArray.getJSONObject(i).getString("imagetype");
                potholeList.add(mID + "       " + mDescription);

                mArrayList.add(mID);
                mArrayList.add(mLatitude);
                mArrayList.add(mLongitude);
                mArrayList.add(mDescription);
                mArrayList.add(mCreated);
                mArrayList.add(mImageType);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return potholeList;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                //LatLngBounds(southWest,northEast)
                LatLngBounds SanDiego = new LatLngBounds(
                        new LatLng(mStartLatitude, mStartLongitude), new LatLng(mEndLatitude, mEndLongitude));
                addMarker(SanDiego);
            }
        });
    }

    private void addMarker(LatLngBounds SanDiego) {

        for (int i = 0; i < mArrayList.size(); i = i + 6) {
            mID = mArrayList.get(i).toString();
            mLatitude = Double.valueOf(mArrayList.get(i + 1).toString());
            mLongitude = Double.valueOf(mArrayList.get(i + 2).toString());
            mMap.addMarker(new MarkerOptions().position(new LatLng(mLatitude, mLongitude)).title(mID));
        }
        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(SanDiego, 0));
    }

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {
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


package cs646.assignment5;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;

public class GetPotHoleDetailFragment extends Fragment {

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

    VolleyQueue mVolleyQueue;
    ProgressDialog mProgressDialog;
    AlertDialog mAlertDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {

        if(getArguments() != null) {

            mPosition = getArguments().getString("mPosition");
            mPotholeID = getArguments().getString("mPotholeID");
            mLongitudeValue = getArguments().getString("mLongitudeValue");
            mLatitudeValue = getArguments().getString("mLatitudeValue");
            mUserValue = getArguments().getString("mUserValue");
            mDescriptionValue = getArguments().getString("mDescriptionValue");
        }
        return inflater.inflate(R.layout.fragment_get_pothole_detail, parent, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        mImageViewValue = (ImageView) view.findViewById(R.id.potholeImage);
        mTextLatitudeValue = (TextView)view.findViewById(R.id.latitudeValue);
        mTextLongitudeValue = (TextView)view.findViewById(R.id.longitudeValue);
        mTextUserValue = (TextView)view.findViewById(R.id.userValue);
        mTextDescriptionValue = (TextView)view.findViewById(R.id.descriptionValue);

        mVolleyQueue = new VolleyQueue(this.getActivity());
    }

    @Override
    public void onResume() {
        super.onResume();

        if(Assignment5Helpers.isNetworkConnected(this.getActivity())) {
            setPotholeInfo();
            setInfoData();
        }
        else {
            DialogInterface.OnClickListener positiveButtonListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //GetPotHoleDetailFragment.this.finish();
                }
            };
            cancelAlertDialog();
            mAlertDialog = Assignment5Helpers.showAlertDialog(this.getActivity(), R.string.info_error_title,
                    R.string.network_error_message, false, R.string.ok, positiveButtonListener, 0, null);
        }
    }

    private void setPotholeInfo() {

        String url = "http://bismarck.sdsu.edu/city/image?id="+mPotholeID;

        mProgressDialog = new ProgressDialog(this.getActivity(), ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setMessage(getResources().getString(R.string.load_info));
        mProgressDialog.setOnCancelListener(new OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
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

        ImageRequest imageToDisplay = new ImageRequest(url, success, 400, 500, ImageView.ScaleType
                .CENTER_INSIDE, null, failure);
        mVolleyQueue.add(imageToDisplay);
    }

    private void setInfoData() {
        mTextLatitudeValue.setText(mLatitudeValue);
        mTextLongitudeValue.setText(mLongitudeValue);
        mTextDescriptionValue.setText(mDescriptionValue);
        mTextUserValue.setText(mUserValue);
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
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        dismissProgressBar();
    }
}

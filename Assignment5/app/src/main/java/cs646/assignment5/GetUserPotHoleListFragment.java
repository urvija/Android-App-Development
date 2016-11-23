package cs646.assignment5;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class GetUserPotHoleListFragment extends Fragment {

    private static final String TAG = "GetPotHoleListFragment";

    private int mSize = 10;
    private int mBatchNumber = 0;

    VolleyQueue mVolleyQueue;

    List<String> mPotholeList;
    ListView mListView;
    ArrayList mArrayList;

    Button mNextButton;
    Button mBackButton;

    String mID;
    String mDescription;
    double mLatitude;
    double mLongitude;
    String mCreated;
    String mImageType;

    AlertDialog mAlertDialog;
    ProgressDialog mProgressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_get_pothole_list, parent, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        mListView = (ListView) view.findViewById(R.id.list);
        mPotholeList = new ArrayList<String>();

        mVolleyQueue = new VolleyQueue(this.getContext());

        mNextButton = (Button) view.findViewById(R.id.next);
        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBatchNumber++;
                getPotholesListAndUpdate();
            }
        });

        mBackButton = (Button) view.findViewById(R.id.back);
        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBatchNumber--;
                if (mBatchNumber > 0 || mBatchNumber == 0) {
                    getPotholesListAndUpdate();
                }
                else {
                    DialogInterface.OnClickListener positiveButtonListener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            cancelAlertDialog();
                        }
                    };
                    cancelAlertDialog();
                    mAlertDialog = Assignment5Helpers.showAlertDialog(getActivity(), R.string
                            .info_error_title, R.string.back_message, false, R.string.ok, positiveButtonListener, 0, null);
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        if(Assignment5Helpers.isNetworkConnected(this.getContext())) {
            getPotholesListAndUpdate();
        }
        else {
            DialogInterface.OnClickListener positiveButtonListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                   // getPotholeListActivity.this.finish();
                }
            };
            cancelAlertDialog();
            mAlertDialog = Assignment5Helpers.showAlertDialog(this.getActivity(), R.string.info_error_title, R
                    .string.network_error_message, false, R.string.ok, positiveButtonListener, 0, null);
        }
    }

    private void getPotholesListAndUpdate() {

        String url = "http://bismarck.sdsu" +
                ".edu/city/batch?type=street&user="+Assignment5Constants
                .USER+"&size="+mSize+"&batch-number="+mBatchNumber;

        mProgressDialog = new ProgressDialog(this.getContext(), ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setMessage(getResources().getString(R.string.load_potholes));
        mProgressDialog.setOnCancelListener(new OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
            }
        });
        mProgressDialog.show();

        Response.Listener<JSONArray> success = new Response.Listener<JSONArray>() {
            public void onResponse(JSONArray response) {
                dismissProgressBar();
                updateList(response);
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

    private void updateList(JSONArray data) {
        mPotholeList = getPotholeList(data);
        updateList();
    }

    private List<String> getPotholeList(JSONArray jsonArray) {
        List<String> potholeList = new ArrayList<String>();
        mArrayList = new ArrayList();

        if(jsonArray.length() < 10) {
            mNextButton.setEnabled(false);
        }
        else {
            mNextButton.setEnabled(true);
        }

        for(int i= 0; i< jsonArray.length(); i++){
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
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return potholeList;
    }

    private void updateList() {

        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getContext(),
                android.R.layout.simple_spinner_dropdown_item, android.R.id.text1, mPotholeList);
        mListView.setAdapter(adapter);

        mListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startNewActivity(position);
            }
        });
    }

    private void startNewActivity(int position) {

        if(GetUserPotHoleList.isTwoPane) {
            GetPotHoleDetailFragment startFragment = new GetPotHoleDetailFragment();

            Bundle bundle = new Bundle();
            bundle.putString("mPosition", String.valueOf(position));

            String getId = String.valueOf(mListView.getAdapter().getItem(position));
            String getIdOfPothole = getId.substring(0, getId.indexOf(" "));

            bundle.putString("mPotholeID", getIdOfPothole);

            int id = mArrayList.indexOf(getIdOfPothole);

            bundle.putString("mLatitudeValue", String.valueOf(mArrayList.get(id + 1)));
            bundle.putString("mLongitudeValue", String.valueOf(mArrayList.get(id + 2)));
            bundle.putString("mUserValue", String.valueOf(mArrayList.get(id + 4)));
            bundle.putString("mDescriptionValue", String.valueOf(mArrayList.get(id + 3)));
            startFragment.setArguments(bundle);

            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.replace(R.id.detailedFragment, startFragment);
            ft.commit();
        }
        else {

            Intent intent = new Intent(this.getActivity(), GetPotholeDetail.class);
            intent.putExtra(Assignment5Constants.POSITION, String.valueOf(position));

            String getId = String.valueOf(mListView.getAdapter().getItem(position));
            String getIdOfPothole = getId.substring(0, getId.indexOf(" "));

            intent.putExtra(Assignment5Constants.ID, getIdOfPothole);
            int id = mArrayList.indexOf(getIdOfPothole);
            intent.putExtra(Assignment5Constants.LATITUDE, String.valueOf(mArrayList.get(id + 1)));
            intent.putExtra(Assignment5Constants.LONGITUDE, String.valueOf(mArrayList.get(id + 2)));
            intent.putExtra(Assignment5Constants.DESCRIPTION, String.valueOf(mArrayList.get(id + 3)));
            intent.putExtra(Assignment5Constants.CREATION_DATE_TIME, String.valueOf(mArrayList.get(id + 4)));

            this.startActivity(intent);
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

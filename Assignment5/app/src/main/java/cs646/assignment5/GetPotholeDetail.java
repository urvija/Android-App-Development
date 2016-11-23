package cs646.assignment5;

import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class GetPotholeDetail extends AppCompatActivity {

    String mPosition;
    String mPotholeID;
    String mLongitudeValue;
    String mLatitudeValue;
    String mUserValue;
    String mDescriptionValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_pothole_detail);

        mPosition = getIntent().getStringExtra(Assignment5Constants.POSITION);
        mPotholeID = getIntent().getStringExtra(Assignment5Constants.ID);
        mLatitudeValue = getIntent().getStringExtra(Assignment5Constants.LATITUDE);
        mLongitudeValue = getIntent().getStringExtra(Assignment5Constants.LONGITUDE);
        mUserValue = getIntent().getStringExtra(Assignment5Constants.CREATION_DATE_TIME);
        mDescriptionValue = getIntent().getStringExtra(Assignment5Constants.DESCRIPTION);

        GetPotHoleDetailFragment detailedFragment = new GetPotHoleDetailFragment();

        Bundle bundle = new Bundle();
        bundle.putString("mPosition", mPosition);
        bundle.putString("mPotholeID", mPotholeID);
        bundle.putString("mLongitudeValue", mLongitudeValue);
        bundle.putString("mLatitudeValue", mLatitudeValue);
        bundle.putString("mUserValue", mUserValue);
        bundle.putString("mDescriptionValue", mDescriptionValue);
        detailedFragment.setArguments(bundle);

        FragmentTransaction startFragment = getSupportFragmentManager().beginTransaction();
        startFragment.replace(R.id.detailedFragment, detailedFragment);
        startFragment.commit();
    }
}

package cs646.assignment4;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    ImageView reportNewPothole;
    ImageView getListOfAllPotholes;
    ImageView getUserList;
    ImageView getRegionMap;

    TextView reportNewPotholeText;
    TextView listOfPotholesText;
    TextView userListText;
    TextView mapOfPotholesText;

    AlertDialog mAlertDialog;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        reportNewPothole = (ImageView)findViewById(R.id.reportNewPothole);
        getListOfAllPotholes = (ImageView)findViewById(R.id.requestAllUserList);
        getUserList = (ImageView)findViewById(R.id.requestUserList);
        getRegionMap = (ImageView) findViewById(R.id.requestAllUserMap);

        reportNewPotholeText = (TextView)findViewById(R.id.reportNewPotholeText);
        listOfPotholesText  = (TextView)findViewById(R.id.requestAllUserListText);
        userListText = (TextView) findViewById(R.id.requestUserListText);
        mapOfPotholesText = (TextView) findViewById(R.id.requestAllUserMapText);

        reportNewPothole.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startReportNewPothole(v);
            }
        });
        reportNewPotholeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startReportNewPothole(v);
            }
        });

        listOfPotholesText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPotholeInfoList(v);
            }
        });
        getListOfAllPotholes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPotholeInfoList(v);
            }
        });

        userListText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startUserList(v);
            }
        });
        getUserList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startUserList(v);
            }
        });

        getRegionMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startRegionMap(v);
            }
        });
        mapOfPotholesText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startRegionMap(v);
            }
        });

    }

    private void startReportNewPothole(View view) {
        if(checkNetwork()) {
            Intent reportNewPothole = new Intent(this, PhotoAndDescActivity.class);
            startActivity(reportNewPothole);
        }
    }

    private void startPotholeInfoList(View view) {
        if(checkNetwork()) {
            Intent potHoleInfoList = new Intent(this, getPotholeListActivity.class);
            startActivity(potHoleInfoList);
        }
    }

    private void startUserList(View view) {
        if(checkNetwork()) {
            Intent userList = new Intent(this, UserListActivity.class);
            startActivity(userList);
        }
    }

    private void startRegionMap(View view) {
        if(checkNetwork()) {
            Intent regionMap = new Intent(this, RegionMapActivity.class);
            startActivity(regionMap);
        }
    }

    protected boolean checkNetwork() {

        if(Assignment4Helpers.isNetworkConnected(this)) {
            cancelAlertDialog();
            return true;
        }
        else {
            DialogInterface.OnClickListener positiveButtonListener = new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //MainActivity.this.finish();
                }
            };
            cancelAlertDialog();
            mAlertDialog = Assignment4Helpers.showAlertDialog(this, R.string.info_error_title, R.string
                    .network_error_message, false, R.string.ok,positiveButtonListener, 0, null);
            return false;
        }
    }

    private void cancelAlertDialog() {
        if(mAlertDialog != null && mAlertDialog.isShowing()) {
            mAlertDialog.cancel();
        }
    }
}


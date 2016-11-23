package cs646.assignment5;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    ImageView getListOfAllPotholes;
    ImageView getUserList;

    TextView listOfPotholesText;
    TextView userListText;

    AlertDialog mAlertDialog;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getListOfAllPotholes = (ImageView)findViewById(R.id.requestAllUserList);
        getUserList = (ImageView)findViewById(R.id.requestUserList);

        listOfPotholesText  = (TextView)findViewById(R.id.requestAllUserListText);
        userListText = (TextView) findViewById(R.id.requestUserListText);

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
    }

    private void startPotholeInfoList(View view) {
        if(checkNetwork()) {
            Intent potHoleInfoList = new Intent(this, GetPotHoleList.class);
            startActivity(potHoleInfoList);
        }
    }

    private void startUserList(View view) {
        if(checkNetwork()) {
            Intent userList = new Intent(this, GetUserPotHoleList.class);
            startActivity(userList);
        }
    }

    protected boolean checkNetwork() {
        if(Assignment5Helpers.isNetworkConnected(this)) {
            cancelAlertDialog();
            return true;
        }
        else {
            DialogInterface.OnClickListener positiveButtonListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            };
            cancelAlertDialog();
            mAlertDialog = Assignment5Helpers.showAlertDialog(this, R.string.info_error_title, R.string
                    .network_error_message, false, R.string.ok, positiveButtonListener, 0, null);
            return false;
        }
    }

    private void cancelAlertDialog() {
        if(mAlertDialog != null && mAlertDialog.isShowing()) {
            mAlertDialog.cancel();
        }
    }
}

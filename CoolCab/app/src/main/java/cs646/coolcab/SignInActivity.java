package cs646.coolcab;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.util.HashMap;
import java.util.Map;

public class SignInActivity extends AppCompatActivity {

    private static final String TAG = "SignInActivity";

    private EditText mEmailEditText;
    private EditText mPasswordEditText;
    private TextView mToSignUpActivity;
    private Button mSignInButton;

    private Firebase mFireBaseReference;
    private AlertDialog mAlertDialog;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        Firebase.setAndroidContext(this);

        mFireBaseReference = new Firebase("https://coolcab.firebaseio.com");
        mEmailEditText = (EditText) findViewById(R.id.emailField);
        mPasswordEditText = (EditText) findViewById(R.id.passwordField);

        mToSignUpActivity = (TextView) findViewById(R.id.signUpText);
        mToSignUpActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToSignUpActivity();
            }
        });

        mSignInButton = (Button) findViewById(R.id.SignInButton);
        mSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                authenticateUser();
            }
        });

        android.support.v7.app.ActionBar menu = getSupportActionBar();
        menu.setDisplayShowHomeEnabled(true);
        menu.setLogo(R.mipmap.ic_launcher);
        menu.setDisplayUseLogoEnabled(true);
        menu.setTitle("  " + "CoolCab");
    }

    private void goToSignUpActivity() {
        Intent startSignUpActivity = new Intent(SignInActivity.this, SignUpActivity.class);
        startActivity(startSignUpActivity);
    }

    private void authenticateUser() {

        String email = mEmailEditText.getText().toString().trim();
        String password = mPasswordEditText.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            mAlertDialog = CoolCabHelper.showErrorAlertDialog(SignInActivity.this, R.string.login_error_title, R.string.login_error_message, true, R.string.ok, null, 0, null);
            mAlertDialog.show();
        }
        else {
            final String emailAddress = email;

            mProgressDialog = new ProgressDialog(this, ProgressDialog.STYLE_SPINNER);
            mProgressDialog.setMessage(getResources().getString(R.string.progress_message));
            mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    finish();
                }
            });
            mProgressDialog.show();

            mFireBaseReference.authWithPassword(email, password, new Firebase.AuthResultHandler() {
                @Override
                public void onAuthenticated(AuthData authData) {
                    dismissProgressBar();
                    Map<String, Object> map = new HashMap<String, Object>();
                    map.put("email", emailAddress);
                    mFireBaseReference.child("users").child(authData.getUid()).updateChildren(map);

                    Intent intent = new Intent(SignInActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }

                @Override
                public void onAuthenticationError(FirebaseError firebaseError) {
                    dismissProgressBar();
                    AlertDialog.Builder builder = new AlertDialog.Builder(SignInActivity.this);
                    builder.setMessage(firebaseError.getMessage())
                            .setTitle(R.string.login_error_title)
                            .setPositiveButton(android.R.string.ok, null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            });
        }
    }

    private void dismissProgressBar()  {
        if(mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
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

    private void cancelAlertDialog() {
        if(mAlertDialog != null) {
            mAlertDialog.cancel();
            mAlertDialog = null;
        }
    }

}
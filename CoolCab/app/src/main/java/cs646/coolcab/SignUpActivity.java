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

import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

public class SignUpActivity extends AppCompatActivity {

    private static final String TAG = "SignUpActivity";

    private EditText mEmail;
    private EditText mPassword;

    private Button mCreateAccount;
    private Button mSignIn;

    private Firebase mFireBaseReference;
    private AlertDialog mAlertDialog;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        Firebase.setAndroidContext(this);

        mFireBaseReference = new Firebase("https://coolcab.firebaseio.com");

        mEmail = (EditText) findViewById(R.id.emailField);
        mPassword = (EditText) findViewById(R.id.passwordField);

        mSignIn = (Button) findViewById(R.id.accountExists);
        mSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToSignInActivity();
            }
        });

        mCreateAccount = (Button) findViewById(R.id.signUpButton);
        mCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateData();
            }
        });

        android.support.v7.app.ActionBar menu = getSupportActionBar();
        menu.setDisplayShowHomeEnabled(true);
        menu.setLogo(R.mipmap.ic_launcher);
        menu.setDisplayUseLogoEnabled(true);
        menu.setTitle("  " + "CoolCab");
    }

    private void goToSignInActivity() {
        Intent startSignInActivity = new Intent(this, SignInActivity.class);
        startActivity(startSignInActivity);
    }

    private void validateData() {

        String email = mEmail.getText().toString().trim();
        String password = mPassword.getText().toString().trim();

        if(password.isEmpty() || email.isEmpty()) {
            mAlertDialog = CoolCabHelper.showErrorAlertDialog(SignUpActivity.this, R.string.sign_up_error_title, R
                    .string.sign_up_error_message, true, R.string.ok, null, 0, null);
            mAlertDialog.show();
        }
        else if(password.length() < 6) {
            mAlertDialog = CoolCabHelper.showErrorAlertDialog(SignUpActivity.this, R.string.sign_up_error_title, R
                    .string.password_length_error_message, true, R.string.ok, null, 0, null);
            mAlertDialog.show();
        }
        else {

            mProgressDialog = new ProgressDialog(this, ProgressDialog.STYLE_SPINNER);
            mProgressDialog.setMessage(getResources().getString(R.string.progress_message));
            mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    finish();
                }
            });
            mProgressDialog.show();

            mFireBaseReference.createUser(email, password, new Firebase.ResultHandler() {
                @Override
                public void onSuccess() {
                    dismissProgressBar();
                    AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
                    builder.setMessage(R.string.sign_up_success).setPositiveButton(R.string.login_button_label, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent intent = new Intent(SignUpActivity.this, SignInActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }

                @Override
                public void onError(FirebaseError firebaseError) {
                    dismissProgressBar();
                    mEmail.setText("");
                    mPassword.setText("");
                    AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
                    builder.setMessage(firebaseError.getMessage())
                            .setTitle(R.string.sign_up_error_title)
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


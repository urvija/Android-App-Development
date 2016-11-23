package cs646.coolcab;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;

import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;

public class AppIntroductionActivity extends AppIntro {

    private static AlertDialog mAlertDialog;

    @Override
    public void init(Bundle savedInstanceState) {

        addSlide(AppIntroFragment.newInstance("Welcome to CoolCab", "Swipe to Explore", R
                .drawable.welcomescreenimage, R.color.black));
        addSlide(AppIntroFragment.newInstance("Sign Up", "Please Sign Up to Get the Best Service", R
                .drawable.signup, R.color.black));
        addSlide(AppIntroFragment.newInstance("Request a Ride", "Submit Pick Up and Drop Off " +
                "Location", R.drawable.requestridescreenimage, R.color.black));
        addSlide(AppIntroFragment.newInstance("Get a Quote", "Get Total Miles and\n Approximate " +
                        "Fare for the Ride", R.drawable.quotescreenimage, R.color.black));
        addSlide(AppIntroFragment.newInstance("Reserve a Ride", "Reserve the Ride and \nGet the Confirmation\n" +
                "via Email", R.drawable.reserveridescreenimage, R.color.black));
        addSlide(AppIntroFragment.newInstance("Payment", "Pay by Cash or Credit Card \nto Driver " +
                "after the Ride", R.drawable.paymentscreenimage, R.color.black));

        setFadeAnimation();
    }

    @Override
    public void onSkipPressed() {
        loadSignInActivity();
    }

    @Override
    public void onDonePressed() {
        loadSignInActivity();
    }

    @Override
    public void onNextPressed() {
    }

    @Override
    public void onSlideChanged() {
    }

    private void loadSignInActivity() {
        Intent startLoginActivity = new Intent(AppIntroductionActivity.this, SignInActivity.class);
        startActivity(startLoginActivity);
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
package cs646.coolcab;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

public class SplashScreenActivity extends AppCompatActivity {

    private final int SPLASH_SCREEN_DISPLAY_LENGTH = 2000;
    private ImageView mSplashScreenImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        mSplashScreenImage = (ImageView) findViewById(R.id.splashScreenImage);
        mSplashScreenImage.setImageResource(R.drawable.splashscreenimage);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashScreenActivity.this,AppIntroductionActivity.class);
                startActivity(intent);
                finish();
            }
        }, SPLASH_SCREEN_DISPLAY_LENGTH);
    }

    @Override
    public void onStop() {
        super.onStop();
        mSplashScreenImage.setImageDrawable(null);
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}
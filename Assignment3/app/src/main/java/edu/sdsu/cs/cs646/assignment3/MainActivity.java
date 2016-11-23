package edu.sdsu.cs.cs646.assignment3;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnTouchListener {

    private static final String TAG = "MainActivity";

    protected static boolean isStartButtonClicked;
    protected static boolean isPauseButtonClicked;
    protected static boolean isGameOver;
    protected static boolean isBirdColliding;

    AnimationDrawable birdAnimation;

    Button mStartButton;
    Button mPauseButton;
    Button mExitButton;

    DrawRectangles mDrawRectanglesView;

    ImageView mFlappyBird;

    boolean isBirdInMotion;
    AlertDialog mAlertDialog;

    int screenWidth;
    int screenHeight;
    int deltaY=15;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        mFlappyBird = (ImageView) findViewById(R.id.bird_image);
        mDrawRectanglesView = (DrawRectangles) findViewById(R.id.view);

        mStartButton = (Button) findViewById(R.id.start_button);
        mPauseButton = (Button) findViewById(R.id.stop_button);
        mExitButton = (Button) findViewById(R.id.exit_button);

        mStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isGameOver=false;
                isStartButtonClicked=true;
                isBirdColliding=false;
                startBirdAnimation();
                startTouchListener();
            }
        });

        mPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isBirdInMotion=false;
                isStartButtonClicked=false;
                isPauseButtonClicked=true;
                mFlappyBird.setOnTouchListener(null);
                mFlappyBird.setLongClickable(false);
                mDrawRectanglesView.setOnTouchListener(null);
                mDrawRectanglesView.setLongClickable(false);
                birdAnimation.stop();
            }
        });

        mExitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exitTheGame();
            }
        });

        setScreenSize();
        setFlappyBird();
    }

    public void setScreenSize() {
        Display display = getWindowManager().getDefaultDisplay();
        Point screenSize = new Point();
        display.getSize(screenSize);
        screenHeight = screenSize.y;
        screenWidth = screenSize.x;
    }

    protected void setFlappyBird() {
        mFlappyBird.setBackgroundResource(R.drawable.animation);
        mFlappyBird.bringToFront();

        birdAnimation = (AnimationDrawable) mFlappyBird.getBackground();
    }

    public void startTouchListener() {
        mFlappyBird.setOnTouchListener(this);
    }

    protected void startBirdAnimation() {
        birdAnimation.start();
        isBirdInMotion = true;
        moveTheBird();
    }

    private void moveTheBird() {
        mFlappyBird.setY(mFlappyBird.getY() + deltaY);
        if (isBirdInMotion) {
            mFlappyBird.postDelayed(new Mover(), 50);
            setCoordinatesToDetectCollision();
            gameOverOnCollision();
        }
    }

    class Mover implements Runnable {
        @Override
        public void run() {
            moveTheBird();
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int action = event.getAction();
        int actionCode = action & MotionEvent.ACTION_MASK;
        switch (actionCode) {
            case MotionEvent.ACTION_DOWN:
                return handleActionDown(event);
            case MotionEvent.ACTION_MOVE:
                return handleActionMove(event);
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                return handleActionUp(event);
        }
        return true;
    }

    private boolean handleActionUp(MotionEvent event) {
        isBirdInMotion =true;
        moveTheBird();
        setCoordinatesToDetectCollision();
        gameOverOnCollision();
        return true;
    }

    private boolean handleActionMove(MotionEvent event) {
        setCoordinatesToDetectCollision();
        if(!isBirdColliding){
            mFlappyBird.setY(mFlappyBird.getY()-40);
        }
        else {
            isBirdInMotion=false;
            birdAnimation.stop();

            mFlappyBird.setX((screenWidth/2)-150);
            mFlappyBird.setY((screenHeight/2)-280);

            isStartButtonClicked = false;
            isGameOver=true;
        }
        return false;
    }

    protected boolean handleActionDown(MotionEvent event) {
        isBirdInMotion =false;
        mFlappyBird.setY(mFlappyBird.getY()-40);
        setCoordinatesToDetectCollision();
        gameOverOnCollision();
        return true;
    }

    private void gameOverOnCollision() {
        if (yIsOutOfBounds(mFlappyBird)) {
            Toast toast = Toast.makeText(MainActivity.this, R.string.bird_touches_boundary, Toast.LENGTH_SHORT);
            toast.show();
            toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
            stopTheGame();
        }
        if(birdIsTouchingRectangle()) {
            Toast toast = Toast.makeText(MainActivity.this, R.string.game_over, Toast.LENGTH_SHORT);
            toast.show();
            toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
            stopTheGame();
      }
    }

    private void stopTheGame()
    {
        birdAnimation.stop();
        mFlappyBird.cancelLongPress();

        mFlappyBird.setX((screenWidth/2)-150);
        mFlappyBird.setY((screenHeight/2)-280);

        isBirdInMotion = false;
        isStartButtonClicked = false;
        mFlappyBird.setOnTouchListener(null);
        mFlappyBird.setOnLongClickListener(null);
        mDrawRectanglesView.setOnLongClickListener(null);
        mDrawRectanglesView.setOnTouchListener(null);
    }

    private boolean yIsOutOfBounds(View flappyBird) {
        float yAxisFlappyBird = flappyBird.getY();
        if (yAxisFlappyBird+flappyBird.getHeight()<40)
            return true;
        if (yAxisFlappyBird+flappyBird.getHeight()+380>screenHeight)
            return true;
        return false;
    }

    protected boolean birdIsTouchingRectangle()
    {
        if(isGameOver || isBirdColliding) {
            return true;
        }
        else {
            return false;
        }
    }

    protected void setCoordinatesToDetectCollision() {
        Rect mBirdInsideRect = new Rect();
        mFlappyBird.getHitRect(mBirdInsideRect);
        mDrawRectanglesView.getCoordinatesToDetectCollision(mBirdInsideRect);
    }

    private void exitTheGame() {
        AlertDialog.Builder dialogBuilder = createAlertDialog();
        mAlertDialog = dialogBuilder.show();
    }

    private AlertDialog.Builder createAlertDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(MainActivity.this);
        dialogBuilder
                .setMessage(getApplicationContext().getResources().getString(R.string.exit_message))
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
        return dialogBuilder;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
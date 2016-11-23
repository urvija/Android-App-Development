package edu.sdsu.cs.cs646.assignment3;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import java.util.Random;

public class DrawRectangles extends View {

    private static final String TAG = "DrawRectangles";
    public static final int MAX_RECTANGLE_SET_TO_BE_DRAWN = 5;

    public Random mRandomNumForHeight = new Random();

    MainActivity mMainActivity = new MainActivity();

    //canvas mRectWidth = 1080, canvas mRectHeight = 1600
    // to keep mSpace between the pipes
    int mSpace = 280;
    int mRectWidth = 170;
    // min mRectHeight is 50 and max mRectHeight is 300
    int mRectHeight = 50+ mRandomNumForHeight.nextInt(300);
    int mRectXAxis = 1080;
    Rect birdRect;

    public DrawRectangles(Context context) {
        this(context, null);
    }

    public DrawRectangles(Context context, AttributeSet xmlAttributes) {
        super(context, xmlAttributes);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        setTheScene(canvas);

        Paint pipesColorFill = new Paint();
        pipesColorFill.setColor(Color.rgb(0, 100, 0));
        pipesColorFill.setStyle(Paint.Style.FILL);

        if (MainActivity.isStartButtonClicked) {
            setThePipes(canvas, pipesColorFill);
        }

        if (!MainActivity.isStartButtonClicked) {
            canvas.drawRect(mRectXAxis, 0, mRectXAxis+mRectWidth, mRectHeight+150, pipesColorFill);
            canvas.drawRect(mRectXAxis, (canvas.getHeight()-(mSpace+mRectHeight+100)), mRectXAxis+mRectWidth, canvas.getHeight()-mSpace, pipesColorFill);
        }

        if (MainActivity.isPauseButtonClicked) {
            canvas.drawRect(mRectXAxis, 0, mRectXAxis+mRectWidth, mRectHeight, pipesColorFill);
            canvas.drawRect(mRectXAxis, (canvas.getHeight()-(mSpace+mRectHeight)), mRectXAxis+mRectWidth, canvas.getHeight()-mSpace, pipesColorFill);
        }
        invalidate();
    }

    protected void setThePipes(Canvas canvas, Paint pipesColorFill)
    {
        if(mRectXAxis > 0) {
            mRectXAxis = mRectXAxis-10;
        }
        if(mRectXAxis == 0) {
            mRectXAxis = 1080;
            mRectHeight = 50+mRandomNumForHeight.nextInt(300);
            canvas.drawRect(mRectXAxis, 0, mRectXAxis+mRectWidth, mRectHeight+150, pipesColorFill);
            canvas.drawRect(mRectXAxis, (canvas.getHeight()-(mSpace+mRectHeight+100)), mRectXAxis+mRectWidth, canvas.getHeight()-mSpace, pipesColorFill);
        }
        else
        {
            canvas.drawRect(mRectXAxis, 0, mRectXAxis+mRectWidth, mRectHeight+150, pipesColorFill);
            canvas.drawRect(mRectXAxis, (canvas.getHeight()-(mSpace+mRectHeight+100)),
                    mRectXAxis+mRectWidth, canvas.getHeight()-mSpace, pipesColorFill);

            Rect upperRectangle = new Rect();
            Rect lowerRectangle = new Rect();

            upperRectangle.set(mRectXAxis, 0, mRectXAxis+mRectWidth, mRectHeight+150);
            lowerRectangle.set(mRectXAxis, (canvas.getHeight()-(mSpace+mRectHeight+100)),
                    mRectXAxis+mRectWidth, canvas.getHeight()-mSpace);

            checkIfBirdIntersects(upperRectangle, lowerRectangle);
        }
    }

    protected void checkIfBirdIntersects(Rect rect1, Rect rect2)
    {
        getCoordinatesToDetectCollision(birdRect);
        if(rect1.intersects(birdRect.left-30, birdRect.top-30, birdRect.right-30, birdRect
                .bottom-30) || rect2.intersect(birdRect.left-30, birdRect.top-30, birdRect
                .right-30, birdRect.bottom-30))
        {
            MainActivity.isBirdColliding=true;
            MainActivity.isGameOver=true;
            mMainActivity.birdIsTouchingRectangle();
        }
    }

    protected void setTheScene(Canvas canvas) {
        canvas.drawColor(Color.CYAN);

        Paint bottomRectangleFill = new Paint();
        bottomRectangleFill.setColor(Color.rgb(255, 153, 102));
        bottomRectangleFill.setStyle(Paint.Style.FILL);
        canvas.drawRect(0, 1400, 1100, 1600, bottomRectangleFill);

        Paint bottomRectangle = new Paint();
        bottomRectangle.setColor(Color.rgb(208, 237, 180));
        bottomRectangle.setStyle(Paint.Style.FILL);
        canvas.drawRect(1, 1320, 1100, 1400, bottomRectangle);
    }

    protected void getCoordinatesToDetectCollision(Rect birdRectCoordinated) {
        birdRect = birdRectCoordinated;
    }
}
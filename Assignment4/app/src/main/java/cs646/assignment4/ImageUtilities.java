package cs646.assignment4;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;

public class ImageUtilities {

    public static Bitmap getScaledBitmap(String path, int deskWidth, int deskHeight) {

        BitmapFactory.Options options;
        options = new BitmapFactory.Options();

        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

        float scrWidth = options.outWidth;
        float scrHeight = options.outHeight;

        int inSampleSize = 1;
        if(scrHeight > deskHeight || scrWidth > deskWidth) {
            if(scrWidth >scrHeight) {
                inSampleSize = Math.round(scrHeight / deskHeight);
            }
            else {
                inSampleSize = Math.round(scrWidth / deskWidth);
            }
        }
        options = new BitmapFactory.Options();
        options.inSampleSize = inSampleSize;

        return BitmapFactory.decodeFile(path, options);
    }

    public static Bitmap getScaledBitmap(String path, Activity activity) {

        Point size = new Point();
        activity.getWindowManager().getDefaultDisplay().getSize(size);

        return getScaledBitmap(path, size.x, size.y);
    }
}

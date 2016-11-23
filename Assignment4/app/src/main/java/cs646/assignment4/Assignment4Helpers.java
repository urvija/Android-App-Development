package cs646.assignment4;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface.OnClickListener;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class Assignment4Helpers {

    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager connMgr = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        }
        else {
            return false;
        }
    }

    public static AlertDialog showAlertDialog(Activity activity, int titleId, int messageId, boolean cancelable, int positiveButtonTitle,  OnClickListener positiveButtonListener, int negativeButtonTitle, OnClickListener negativeButtonListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity)
                .setTitle(titleId)
                .setMessage(messageId)
                .setCancelable(cancelable)
                .setPositiveButton(positiveButtonTitle, positiveButtonListener);

        if(negativeButtonListener != null) {
            builder.setNegativeButton(negativeButtonTitle, negativeButtonListener);
        }
        return  builder.show();
    }
}

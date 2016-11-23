package cs646.coolcab;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

public class CoolCabHelper {

    public static AlertDialog showErrorAlertDialog(Context context, int titleId, int messageId,
                                                   boolean cancelable, int positiveButtonTitle,
                                                   OnClickListener positiveButtonListener,
                                                   int negativeButtonTitle,
                                                   OnClickListener negativeButtonListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setTitle(titleId)
                .setMessage(messageId)
                .setCancelable(cancelable)
                .setPositiveButton(positiveButtonTitle, positiveButtonListener);

        if(negativeButtonListener != null) {
            builder.setNegativeButton(negativeButtonTitle, negativeButtonListener);
        }
        return builder.create();
    }

    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager cManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cManager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        }
        else {
            return false;
        }
    }

    public static AlertDialog getNetworkErrorAlertDialog(final Context context) {
        OnClickListener positiveButtonListener = new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ((Activity)context).finish();
            }
        };
        return showErrorAlertDialog(context, R.string.error, R.string.network_error, false, R.string.ok,
                positiveButtonListener, 0, null);
    }

    public static void hideKeyboard(Context context) {
        View view = ((Activity)context).getCurrentFocus();

        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
}
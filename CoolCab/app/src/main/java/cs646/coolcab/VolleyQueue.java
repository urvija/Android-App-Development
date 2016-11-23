package cs646.coolcab;

import android.app.Application;
import android.content.Context;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class VolleyQueue extends Application {

    private static VolleyQueue mInstance;
    private static Context mContext;

    private RequestQueue mRequestQueue;

    protected VolleyQueue(Context context) {
        mContext = context;
        mRequestQueue = queue();
    }

    public static synchronized VolleyQueue instance(Context context) {
        if(mInstance == null) {
            mInstance=new VolleyQueue(context);
        }
        return mInstance;
    }

    public RequestQueue queue() {
        if(mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(mContext.getApplicationContext());
        }
        return mRequestQueue;
    }

    public <T> void add(Request<T> req) {
        queue().add(req);
    }
}
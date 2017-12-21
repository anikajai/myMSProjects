package edu.usc.csci571.searchonfb;

import android.app.Application;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class ApplicationCtrl extends Application {

    private RequestQueue reqQ;
    private static ApplicationCtrl instance;
    public static final String TAG = ApplicationCtrl.class.getSimpleName();

    public static synchronized ApplicationCtrl getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public RequestQueue getRequestQueue() {
        if (null == reqQ) {
            reqQ = Volley.newRequestQueue(getApplicationContext());
        }
        return reqQ;
    }

    public void cancelPendingRequests(Object tag) {
        if (null != reqQ) {
            reqQ.cancelAll(tag);
        }
    }

    public <T> void addToRequestQueue(final Request<T> req, final String tag) {
        if (TextUtils.isEmpty(tag)) {
            req.setTag(TAG);
        } else {
            req.setTag(tag);
        }
        getRequestQueue().add(req);
    }
}
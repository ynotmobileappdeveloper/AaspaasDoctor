package com.ynot.aaspaasdoctor.Webservice;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class VolleySingleton {

    private static VolleySingleton mInstance;
    private RequestQueue mRequestQueue;
    private static Context mctx;


    private VolleySingleton(Context context) {

        mctx = context;
        mRequestQueue = getRequestQueue();


    }

    public static synchronized VolleySingleton getmInstance(Context context) {

        if (mInstance == null) {

            mInstance = new VolleySingleton(context);
        }
        return mInstance;

    }

    private RequestQueue getRequestQueue() {

        if (mRequestQueue == null) {


            mRequestQueue = Volley.newRequestQueue(mctx.getApplicationContext());

        }
        return mRequestQueue;

    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }


}

package com.desmond.materialrssreader.net;

import android.content.Context;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;

/**
 * Created by desmond on 9/11/14.
 */
public class VolleySingleton {

    private static final int CACHE_SIZE = 1024 * 1024 * 10;
    private static VolleySingleton instance;

    private static final Object lock = new Object();

    private RequestQueue requestQueue;
    private Context context;

    private VolleySingleton(Context context) {
        this.context = context;
        requestQueue = getRequestQueue();
    }

    public static VolleySingleton getInstance(Context context) {
        synchronized (lock) {
            if (instance == null) {
                instance = new VolleySingleton(context);
            }

            return instance;
        }
    }

    public RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            Cache cache = new DiskBasedCache(context.getCacheDir(), CACHE_SIZE);
            Network network = new BasicNetwork(new HurlStack());
            requestQueue = new RequestQueue(cache, network);
            requestQueue.start();
        }
        return requestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }

    public void stop() {
        getRequestQueue().stop();
    }
}

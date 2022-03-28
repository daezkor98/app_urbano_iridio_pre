package com.urbanoexpress.iridio3.util.network.volley;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by mick on 19/05/16.
 */
public class ManagerVolley {

    private static ManagerVolley managerVolley;
    private final RequestQueue requestQueue;

    private ManagerVolley(Context context) {
        requestQueue = Volley.newRequestQueue(context.getApplicationContext());
    }

    public static synchronized ManagerVolley getInstance(Context context) {
        if (managerVolley == null) {
            managerVolley = new ManagerVolley(context);
        }
        return managerVolley;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        requestQueue.add(req);
    }

}

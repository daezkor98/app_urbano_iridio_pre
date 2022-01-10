package com.urbanoexpress.iridio.model.interactor.callback;

import com.android.volley.VolleyError;

import org.json.JSONObject;

/**
 * Created by mick on 20/05/16.
 */
public interface RequestCallback {

    void onSuccess(JSONObject response);
    void onError(VolleyError error);

}

package com.urbanoexpress.iridio.util.network;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by mick on 19/05/16.
 */
//TODO: find replacememt
public class TaskPing extends AsyncTask<Object, Integer, ArrayList> {

    @Override
    protected ArrayList doInBackground(Object... params) {
        Boolean isReachable = false;
        try {
            Process process = java.lang.Runtime.getRuntime().exec("ping -c 1 www.google.com");
            int returnVal = process.waitFor();
            isReachable = (returnVal == 0);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new ArrayList<>(Arrays.asList(isReachable, params[0], params[1]));
    }

    @Override
    protected void onPostExecute(ArrayList data) {
        if ( (Boolean) data.get(0) ) {
            Log.d("Connection", "Internet access");
        } else {
            Log.d("Connection", "No Internet access");
            Connection.showMessageNotConnectedToNetwork((Context) data.get(1), (View) data.get(2));
        }
    }

}

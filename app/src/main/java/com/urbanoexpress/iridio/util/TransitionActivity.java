package com.urbanoexpress.iridio.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;
import android.util.Log;
import android.view.View;
import android.view.Window;

import java.util.ArrayList;
import java.util.List;

import com.urbanoexpress.iridio.R;

/**
 * Created by mick on 01/06/16.
 */
public class TransitionActivity {

    private static final String TAG = TransitionActivity.class.getSimpleName();

    private Intent intent;
    private List<Pair<View, String>> pairs;
    private Activity activity;

    private View statusBar, navigationBar;

    public TransitionActivity(Context context, Class<?> cls) {
        this.intent = new Intent(context, cls);
        this.activity = (Activity) context;
        pairs = new ArrayList<>();
        initializePairsSharedDefault();
    }

    public void putExtraIntent(String name, Bundle value) {
        this.intent.putExtra(name, value);
    }

    public void addPair(View view, String str) {
        pairs.add(new Pair<View, String>(view, str));
    }

    public void startActivity() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
                && !Build.VERSION.RELEASE.equals("5.1.1")
                && !Build.VERSION.RELEASE.equals("5.0")) {
            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    // the context of the activity
                    activity,
                    // For each shared element, add to this method a new Pair item,
                    // which contains the reference of the view we are transitioning *from*,
                    // and the value of the transitionName attribute
                    getPairsToArray()
            );

            ActivityCompat.startActivity(activity, intent, options.toBundle());
        } else {
            activity.startActivity(intent);
            activity.overridePendingTransition(R.anim.slide_enter_from_bottom, R.anim.not_slide);
        }
    }

    private Pair<View, String>[] getPairsToArray() {
        Pair<View, String>[] newPairs = new Pair[pairs.size()];
        System.arraycopy(pairs.toArray(), 0, newPairs, 0, pairs.size());
        Log.d("TransitionActivity", "PairSize: " + pairs.size());
        Log.d("TransitionActivity", "NewPairsSize: " + newPairs.length);
        return newPairs;
    }

    private void initializePairsSharedDefault() {
        statusBar = activity.findViewById(android.R.id.statusBarBackground);
        navigationBar = activity.findViewById(android.R.id.navigationBarBackground);
        addPair(statusBar, Window.STATUS_BAR_BACKGROUND_TRANSITION_NAME);
        addPair(navigationBar, Window.NAVIGATION_BAR_BACKGROUND_TRANSITION_NAME);
    }

}

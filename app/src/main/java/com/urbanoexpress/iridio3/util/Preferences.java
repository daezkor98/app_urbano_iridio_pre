package com.urbanoexpress.iridio3.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import androidx.annotation.Nullable;

import java.util.Map;
import java.util.Set;

/**
 * Created by mick on 23/05/16.
 */
//TODO desacoplar GlobalConfigApp - UserProfile and PreferencesHelper
public class Preferences implements SharedPreferences {

    private static Preferences myPreferences;
    private static SharedPreferences sharedPreferences;
    private static SharedPreferences.Editor editor;

    private Preferences() {}

    public static Preferences getInstance() {
        if (myPreferences == null) {
            myPreferences = new Preferences();
        }
        return myPreferences;
    }

    public Preferences init(Activity activity, String namePreferences) {
        sharedPreferences = activity.getSharedPreferences(namePreferences, Context.MODE_PRIVATE); // Default MODE_PRIVATE
        editor = sharedPreferences.edit();
        return getInstance();
    }

    public Preferences init(Context context, String namePreferences) {
        sharedPreferences = context.getSharedPreferences(namePreferences, Context.MODE_PRIVATE); // Default MODE_PRIVATE
        editor = sharedPreferences.edit();
        return getInstance();
    }

    @Override
    public Map<String, ?> getAll() {
        return sharedPreferences.getAll();
    }

    @Override
    public String getString(String key, String defValue) {
        return sharedPreferences.getString(key, defValue);
    }

    @Nullable
    @Override
    public Set<String> getStringSet(String key, Set<String> defValues) {
        return sharedPreferences.getStringSet(key, defValues);
    }

    @Override
    public int getInt(String key, int defValue) {
        return sharedPreferences.getInt(key, defValue);
    }

    @Override
    public long getLong(String key, long defValue) {
        return sharedPreferences.getLong(key, defValue);
    }

    @Override
    public float getFloat(String key, float defValue) {
        return sharedPreferences.getFloat(key, defValue);
    }

    @Override
    public boolean getBoolean(String key, boolean defValue) {
        return sharedPreferences.getBoolean(key, defValue);
    }

    @Override
    public boolean contains(String key) {
        return sharedPreferences.contains(key);
    }

    @Override
    public Editor edit() {
        return editor;
    }

    @Override
    public void registerOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener) {

    }

    @Override
    public void unregisterOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener) {

    }
}

//PK: PreferencesKeys
interface  PK{

}

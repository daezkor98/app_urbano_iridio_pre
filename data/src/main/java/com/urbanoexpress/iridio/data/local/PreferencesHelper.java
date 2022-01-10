package com.urbanoexpress.iridio.data.local;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Set;

public class PreferencesHelper {

    private final SharedPreferences preferences;

    public PreferencesHelper(Context context) {
        preferences = context.getSharedPreferences(
                PreferenceKey.PREF_APP_SETTINGS, Context.MODE_PRIVATE);
    }

    public SharedPreferences getPreferences() {
        return preferences;
    }

    public void clear() {
        getPreferences().edit().clear().apply();
    }

    public int getInt(String preferenceKey, int preferenceDefaultValue) {
        return getPreferences().getInt(preferenceKey, preferenceDefaultValue);
    }

    public void putInt(String preferenceKey, int preferenceValue) {
        getPreferences().edit().putInt(preferenceKey, preferenceValue).apply();
    }

    public long getLong(String preferenceKey, long preferenceDefaultValue) {
        return getPreferences().getLong(preferenceKey, preferenceDefaultValue);
    }

    public void putLong(String preferenceKey, long preferenceValue) {
        getPreferences().edit().putLong(preferenceKey, preferenceValue).apply();
    }

    public float getFloat(String preferenceKey, float preferenceDefaultValue) {
        return getPreferences().getFloat(preferenceKey, preferenceDefaultValue);
    }

    public void putFloat(String preferenceKey, float preferenceValue) {
        getPreferences().edit().putFloat(preferenceKey, preferenceValue).apply();
    }

    public boolean getBoolean(String preferenceKey, boolean preferenceDefaultValue) {
        return getPreferences().getBoolean(preferenceKey, preferenceDefaultValue);
    }

    public void putBoolean(String preferenceKey, boolean preferenceValue) {
        getPreferences().edit().putBoolean(preferenceKey, preferenceValue).apply();
    }

    public String getString(String preferenceKey, String preferenceDefaultValue) {
        return getPreferences().getString(preferenceKey, preferenceDefaultValue);
    }

    public void putString(String preferenceKey, String preferenceValue) {
        getPreferences().edit().putString(preferenceKey, preferenceValue).apply();
    }

    public void putStringSet(String preferenceKey, Set<String> stringSet) {
        getPreferences().edit().putStringSet(preferenceKey, stringSet).apply();
    }

    public Set<String> getStringSet(String preferenceKey) {
        return getPreferences().getStringSet(preferenceKey, null);
    }

    public void putUrbanoUserId(long userId) {
        getPreferences().edit().putLong(PreferenceKey.PREF_KEY_URBANO_USER_ID, userId).apply();
    }

    public long getUrbanoUserId() {
        return getPreferences().getLong(PreferenceKey.PREF_KEY_URBANO_USER_ID, 0L);
    }

    public void putUserName(String username) {
        getPreferences().edit().putString(PreferenceKey.PREF_KEY_USER_NAME, username).apply();
    }

    public String getUserName() {
        return getPreferences().getString(PreferenceKey.PREF_KEY_USER_NAME, null);
    }

    public void putExistsSession(boolean value) {
        getPreferences().edit().putBoolean(PreferenceKey.PREF_KEY_EXISTS_SESSION, value).apply();
    }

    public boolean existsSession() {
        return getPreferences().getBoolean(PreferenceKey.PREF_KEY_EXISTS_SESSION, false);
    }

    public void putApiEnvironment(int apiEnvironment) {
        getPreferences().edit().putInt(PreferenceKey.PREF_KEY_API_ENVIRONMENT, apiEnvironment).apply();
    }

    public int getApiEnvironment() {
        return getPreferences().getInt(PreferenceKey.PREF_KEY_API_ENVIRONMENT, -1);
    }

    public void putCountry(int country) {
        getPreferences().edit().putInt(PreferenceKey.PREF_KEY_COUNTRY, country).apply();
    }

    public int getCountry() {
        return getPreferences().getInt(PreferenceKey.PREF_KEY_COUNTRY, -1);
    }
}

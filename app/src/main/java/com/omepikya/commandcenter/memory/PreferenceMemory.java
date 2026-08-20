package com.omepikya.commandcenter.memory;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Map;

public class PreferenceMemory {

    private static final String PREF_NAME =
            "omepikya_preference_memory";

    private final SharedPreferences preferences;

    public PreferenceMemory(Context context) {
        Context appContext =
                context.getApplicationContext();

        preferences = appContext.getSharedPreferences(
                PREF_NAME,
                Context.MODE_PRIVATE
        );
    }

    public void put(String key, String value) {
        if (key == null || value == null) {
            return;
        }

        preferences.edit()
                .putString(key, value)
                .apply();
    }

    public void putBoolean(String key, boolean value) {
        if (key == null) {
            return;
        }

        preferences.edit()
                .putBoolean(key, value)
                .apply();
    }

    public void putInt(String key, int value) {
        if (key == null) {
            return;
        }

        preferences.edit()
                .putInt(key, value)
                .apply();
    }

    public String get(String key) {
        if (key == null) {
            return null;
        }

        return preferences.getString(key, null);
    }

    public String get(String key, String defaultValue) {
        if (key == null) {
            return defaultValue;
        }

        return preferences.getString(
                key,
                defaultValue
        );
    }

    public boolean getBoolean(
            String key,
            boolean defaultValue
    ) {
        if (key == null) {
            return defaultValue;
        }

        return preferences.getBoolean(
                key,
                defaultValue
        );
    }

    public int getInt(
            String key,
            int defaultValue
    ) {
        if (key == null) {
            return defaultValue;
        }

        return preferences.getInt(
                key,
                defaultValue
        );
    }

    public boolean contains(String key) {
        return key != null
                && preferences.contains(key);
    }

    public void remove(String key) {
        if (key == null) {
            return;
        }

        preferences.edit()
                .remove(key)
                .apply();
    }

    public void clear() {
        preferences.edit()
                .clear()
                .apply();
    }

    public Map<String, ?> getAll() {
        return preferences.getAll();
    }
}
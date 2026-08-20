package com.omepikya.commandcenter.plugins;

import android.content.Context;
import android.content.SharedPreferences;

public final class PluginSettingsStore {

    private static final String PREFS =
            "omepikya_plugin_settings";

    private static final String PREFIX =
            "enabled_";

    private final SharedPreferences preferences;

    public PluginSettingsStore(
            Context context
    ) {

        if (context == null) {
            throw new IllegalArgumentException(
                    "Context cannot be null"
            );
        }

        preferences =
                context
                        .getApplicationContext()
                        .getSharedPreferences(
                                PREFS,
                                Context.MODE_PRIVATE
                        );
    }

    public boolean hasState(
            String id
    ) {

        return id != null
                && preferences.contains(
                        PREFIX + id
                );
    }

    public boolean isEnabled(
            String id,
            boolean defaultValue
    ) {

        if (id == null) {
            return defaultValue;
        }

        return preferences.getBoolean(
                PREFIX + id,
                defaultValue
        );
    }

    public void setEnabled(
            String id,
            boolean enabled
    ) {

        if (id == null
                || id.trim().isEmpty()) {

            return;
        }

        preferences
                .edit()
                .putBoolean(
                        PREFIX + id,
                        enabled
                )
                .apply();
    }

    public void remove(
            String id
    ) {

        if (id == null) {
            return;
        }

        preferences
                .edit()
                .remove(
                        PREFIX + id
                )
                .apply();
    }
}
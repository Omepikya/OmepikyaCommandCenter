package com.omepikya.commandcenter.automation;

import android.content.Context;
import android.content.SharedPreferences;

public class ProactiveSettings {

    private static final String PREF_NAME =
            "omepikya_proactive_settings";

    private static final String KEY_ENABLED =
            "enabled";

    private static final String KEY_AUTOMATION =
            "automation_suggestions";

    private static final String KEY_BEHAVIOR =
            "behavior_suggestions";

    private static final String KEY_CONTEXT =
            "context_suggestions";

    private static final String KEY_CONFIRM =
            "confirm_sensitive";

    private static final String KEY_COOLDOWN =
            "cooldown";

    public static final long
            DEFAULT_COOLDOWN =
            60_000L;

    private final SharedPreferences preferences;

    public ProactiveSettings(
            Context context
    ) {

        if (context == null) {

            throw new IllegalArgumentException(
                    "Context cannot be null"
            );
        }

        preferences =
                context.getApplicationContext()
                        .getSharedPreferences(
                                PREF_NAME,
                                Context.MODE_PRIVATE
                        );
    }

    public boolean isEnabled() {

        return preferences.getBoolean(
                KEY_ENABLED,
                true
        );
    }

    public void setEnabled(
            boolean enabled
    ) {

        preferences.edit()
                .putBoolean(
                        KEY_ENABLED,
                        enabled
                )
                .apply();
    }

    public boolean
    isAutomationSuggestionsEnabled() {

        return preferences.getBoolean(
                KEY_AUTOMATION,
                true
        );
    }

    public void
    setAutomationSuggestionsEnabled(
            boolean enabled
    ) {

        preferences.edit()
                .putBoolean(
                        KEY_AUTOMATION,
                        enabled
                )
                .apply();
    }

    public boolean
    isBehaviorSuggestionsEnabled() {

        return preferences.getBoolean(
                KEY_BEHAVIOR,
                true
        );
    }

    public void
    setBehaviorSuggestionsEnabled(
            boolean enabled
    ) {

        preferences.edit()
                .putBoolean(
                        KEY_BEHAVIOR,
                        enabled
                )
                .apply();
    }

    public boolean
    isContextSuggestionsEnabled() {

        return preferences.getBoolean(
                KEY_CONTEXT,
                true
        );
    }

    public void
    setContextSuggestionsEnabled(
            boolean enabled
    ) {

        preferences.edit()
                .putBoolean(
                        KEY_CONTEXT,
                        enabled
                )
                .apply();
    }

    public boolean
    shouldConfirmSensitiveActions() {

        return preferences.getBoolean(
                KEY_CONFIRM,
                true
        );
    }

    public void
    setConfirmSensitiveActions(
            boolean enabled
    ) {

        preferences.edit()
                .putBoolean(
                        KEY_CONFIRM,
                        enabled
                )
                .apply();
    }

    public long getCooldown() {

        return preferences.getLong(
                KEY_COOLDOWN,
                DEFAULT_COOLDOWN
        );
    }

    public void setCooldown(
            long milliseconds
    ) {

        if (milliseconds < 10_000L) {

            milliseconds =
                    10_000L;
        }

        if (milliseconds
                > 24L * 60L * 60L * 1000L) {

            milliseconds =
                    24L * 60L * 60L * 1000L;
        }

        preferences.edit()
                .putLong(
                        KEY_COOLDOWN,
                        milliseconds
                )
                .apply();
    }

    public void resetDefaults() {

        preferences.edit()
                .clear()
                .apply();
    }
}
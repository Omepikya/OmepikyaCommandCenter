package com.omepikya.commandcenter.intelligence;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Phase 7D / 7G:
 *
 * Persistent command success learning
 * and command aliases.
 */
public final class AdaptiveLearning {

    private static final String PREFS =
            "omepikya_phase7_learning";

    private final SharedPreferences prefs;

    private final Map<String, Integer>
            successes =
            new HashMap<>();

    public AdaptiveLearning(
            Context context) {

        if (context == null) {
            throw new IllegalArgumentException(
                    "Context cannot be null");
        }

        prefs =
                context.getApplicationContext()
                        .getSharedPreferences(
                                PREFS,
                                Context.MODE_PRIVATE);

        load();
    }

    public synchronized void recordSuccess(
            String command) {

        String key =
                key(command);

        if (key.isEmpty()) {
            return;
        }

        Integer count =
                successes.get(key);

        int next =
                count == null
                        ? 1
                        : count + 1;

        successes.put(
                key,
                next);

        prefs.edit()
                .putInt(
                        "s_" + key,
                        next)
                .apply();
    }

    public synchronized int getSuccessCount(
            String command) {

        String key =
                key(command);

        Integer value =
                successes.get(key);

        if (value != null) {
            return value;
        }

        return prefs.getInt(
                "s_" + key,
                0);
    }

    public synchronized void saveAlias(
            String alias,
            String command) {

        String a =
                key(alias);

        String c =
                command == null
                        ? ""
                        : command.trim();

        if (a.isEmpty() ||
                c.isEmpty()) {

            return;
        }

        prefs.edit()
                .putString(
                        "a_" + a,
                        c)
                .apply();
    }

    public synchronized String resolveAlias(
            String input) {

        String a =
                key(input);

        if (a.isEmpty()) {
            return null;
        }

        return prefs.getString(
                "a_" + a,
                null);
    }

    public synchronized void clear() {

        successes.clear();

        prefs.edit()
                .clear()
                .apply();
    }

    private String key(
            String value) {

        if (value == null) {
            return "";
        }

        return value.trim()
                .toLowerCase(Locale.US)
                .replaceAll(
                        "[^a-z0-9_ ]",
                        "")
                .replaceAll(
                        "\\s+",
                        "_");
    }

    private void load() {
        /*
         * SharedPreferences remains the source
         * of truth. Values are loaded lazily.
         */
    }
}
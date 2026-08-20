package com.omepikya.commandcenter.analytics;

import java.util.*;

public final class PreferenceLearner {

    private final Map<String, String> preferences =
            new HashMap<>();

    public synchronized void observe(
            String key,
            String value) {

        if (key != null
                && !key.trim().isEmpty()
                && value != null) {

            preferences.put(
                    key.trim().toLowerCase(Locale.US),
                    value.trim());
        }
    }

    public synchronized String get(String key) {

        return key == null
                ? null
                : preferences.get(
                        key.trim()
                                .toLowerCase(Locale.US));
    }

    public synchronized Map<String, String> snapshot() {

        return Collections.unmodifiableMap(
                new HashMap<>(preferences));
    }

    public synchronized void clear() {
        preferences.clear();
    }
}
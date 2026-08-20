package com.omepikya.commandcenter.intelligence;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class BehaviorLearner {

    private static final String PREFS_NAME =
            "omepikya_behavior";

    private static final String DATA_KEY =
            "command_patterns";

    private final SharedPreferences preferences;

    private final List<CommandPattern> patterns =
            new ArrayList<>();

    public BehaviorLearner(Context context) {

        if (context == null) {
            throw new IllegalArgumentException(
                    "Context cannot be null"
            );
        }

        preferences =
                context.getApplicationContext()
                        .getSharedPreferences(
                                PREFS_NAME,
                                Context.MODE_PRIVATE
                        );

        load();
    }

    /**
     * Records a successfully executed command.
     */
    public synchronized void record(
            String command
    ) {

        if (command == null
                || command.trim().isEmpty()) {
            return;
        }

        String normalized =
                normalize(command);

        if (normalized.isEmpty()) {
            return;
        }

        int hour =
                java.util.Calendar
                        .getInstance()
                        .get(java.util.Calendar.HOUR_OF_DAY);

        CommandPattern existing =
                find(normalized, hour);

        if (existing == null) {

            existing =
                    new CommandPattern(
                            normalized,
                            hour
                    );

            patterns.add(existing);
        }

        existing.increment();

        save();
    }

    /**
     * Returns the most frequently used commands
     * for the current hour.
     */
    public synchronized List<CommandPattern> getPredictions(
            int hour,
            int limit
    ) {

        if (limit <= 0) {
            limit = 5;
        }

        List<CommandPattern> result =
                new ArrayList<>();

        for (CommandPattern pattern : patterns) {

            if (pattern.getHour() == hour) {
                result.add(pattern);
            }
        }

        Collections.sort(
                result,
                new Comparator<CommandPattern>() {
                    @Override
                    public int compare(
                            CommandPattern a,
                            CommandPattern b
                    ) {
                        return Integer.compare(
                                b.getCount(),
                                a.getCount()
                        );
                    }
                }
        );

        if (result.size() > limit) {

            return new ArrayList<>(
                    result.subList(0, limit)
            );
        }

        return result;
    }

    /**
     * Returns all learned patterns.
     */
    public synchronized List<CommandPattern> getAll() {

        return new ArrayList<>(patterns);
    }

    /**
     * Clears learned behavior.
     */
    public synchronized void clear() {

        patterns.clear();

        preferences.edit()
                .remove(DATA_KEY)
                .apply();
    }

    /**
     * Returns how many times a command was seen
     * during a specific hour.
     */
    public synchronized int getUsageCount(
            String command,
            int hour
    ) {

        CommandPattern pattern =
                find(
                        normalize(command),
                        hour
                );

        return pattern == null
                ? 0
                : pattern.getCount();
    }

    private CommandPattern find(
            String command,
            int hour
    ) {

        for (CommandPattern pattern : patterns) {

            if (pattern.getHour() == hour
                    && pattern.getCommand().equals(command)) {

                return pattern;
            }
        }

        return null;
    }

    private String normalize(
            String command
    ) {

        return command
                .trim()
                .toLowerCase(Locale.US)
                .replaceAll("\\s+", " ");
    }

    private void save() {

        try {

            JSONArray array =
                    new JSONArray();

            for (CommandPattern pattern : patterns) {

                JSONObject object =
                        new JSONObject();

                object.put(
                        "command",
                        pattern.getCommand()
                );

                object.put(
                        "hour",
                        pattern.getHour()
                );

                object.put(
                        "count",
                        pattern.getCount()
                );

                array.put(object);
            }

            preferences.edit()
                    .putString(
                            DATA_KEY,
                            array.toString()
                    )
                    .apply();

        } catch (Exception ignored) {
        }
    }

    private void load() {

        patterns.clear();

        String data =
                preferences.getString(
                        DATA_KEY,
                        null
                );

        if (data == null
                || data.trim().isEmpty()) {
            return;
        }

        try {

            JSONArray array =
                    new JSONArray(data);

            for (int i = 0;
                 i < array.length();
                 i++) {

                JSONObject object =
                        array.getJSONObject(i);

                String command =
                        object.optString(
                                "command",
                                ""
                        );

                int hour =
                        object.optInt(
                                "hour",
                                -1
                        );

                int count =
                        object.optInt(
                                "count",
                                0
                        );

                if (!command.isEmpty()
                        && hour >= 0
                        && hour <= 23
                        && count > 0) {

                    patterns.add(
                            new CommandPattern(
                                    command,
                                    hour,
                                    count
                            )
                    );
                }
            }

        } catch (Exception ignored) {
        }
    }

    public static class CommandPattern {

        private final String command;
        private final int hour;
        private int count;

        public CommandPattern(
                String command,
                int hour
        ) {

            this(
                    command,
                    hour,
                    0
            );
        }

        public CommandPattern(
                String command,
                int hour,
                int count
        ) {

            this.command = command;
            this.hour = hour;
            this.count = count;
        }

        public void increment() {
            count++;
        }

        public String getCommand() {
            return command;
        }

        public int getHour() {
            return hour;
        }

        public int getCount() {
            return count;
        }
    }
}
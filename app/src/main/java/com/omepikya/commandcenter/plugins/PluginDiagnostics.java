package com.omepikya.commandcenter.plugins;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class PluginDiagnostics {

    public static final class Record {

        private final String pluginId;
        private final String action;
        private final boolean success;
        private final long durationMs;
        private final String message;
        private final long timestamp;

        private Record(
                String pluginId,
                String action,
                boolean success,
                long durationMs,
                String message
        ) {

            this.pluginId = pluginId;
            this.action = action;
            this.success = success;
            this.durationMs = durationMs;
            this.message = message;

            timestamp =
                    System.currentTimeMillis();
        }

        public String getPluginId() {
            return pluginId;
        }

        public String getAction() {
            return action;
        }

        public boolean isSuccess() {
            return success;
        }

        public long getDurationMs() {
            return durationMs;
        }

        public String getMessage() {
            return message;
        }

        public long getTimestamp() {
            return timestamp;
        }
    }

    private final List<Record> records =
            new ArrayList<>();

    private int executions;
    private int successes;
    private int failures;

    public synchronized void record(
            String pluginId,
            String action,
            boolean success,
            long durationMs,
            String message
    ) {

        executions++;

        if (success) {
            successes++;
        } else {
            failures++;
        }

        records.add(
                new Record(
                        pluginId,
                        action,
                        success,
                        durationMs,
                        message
                )
        );

        if (records.size() > 100) {
            records.remove(0);
        }
    }

    public synchronized int
    getExecutions() {

        return executions;
    }

    public synchronized int
    getSuccesses() {

        return successes;
    }

    public synchronized int
    getFailures() {

        return failures;
    }

    public synchronized List<Record>
    getRecentRecords() {

        return Collections.unmodifiableList(
                new ArrayList<>(records)
        );
    }
}
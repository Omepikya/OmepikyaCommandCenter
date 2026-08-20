package com.omepikya.commandcenter.execution;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONObject;

/**
 * Crash-safe snapshot of the latest execution state.
 *
 * The snapshot is deliberately small and independent
 * from execution history.
 */
public final class ExecutionStateStore {

    private static final String PREF_NAME =
            "omepikya_execution_state";

    private static final String KEY_STATE =
            "state";

    private final SharedPreferences preferences;

    public ExecutionStateStore(
            Context context) {

        if (context == null) {

            throw new IllegalArgumentException(
                    "Context cannot be null");
        }

        preferences =
                context.getApplicationContext()
                        .getSharedPreferences(
                                PREF_NAME,
                                Context.MODE_PRIVATE);
    }

    public synchronized void save(
            ExecutionTrace trace) {

        if (trace == null) {
            return;
        }

        try {

            JSONObject object =
                    new JSONObject();

            object.put(
                    "executionId",
                    trace.getExecutionId());

            object.put(
                    "command",
                    trace.getCommand());

            object.put(
                    "startedAt",
                    trace.getStartedAt());

            object.put(
                    "completedAt",
                    trace.getCompletedAt());

            object.put(
                    "state",
                    trace.getState().name());

            object.put(
                    "attemptCount",
                    trace.getAttemptCount());

            ExecutionFailure failure =
                    trace.getFailure();

            if (failure != null) {

                object.put(
                        "failureReason",
                        failure.getReason());

                object.put(
                        "failedStep",
                        failure.getFailedStep());

                object.put(
                        "retryable",
                        failure.isRetryable());

                object.put(
                        "requiresConfirmation",
                        failure.requiresConfirmation());
            }

            /*
             * commit() is intentional here because the
             * snapshot is used as crash-recovery state.
             */
            preferences.edit()
                    .putString(
                            KEY_STATE,
                            object.toString())
                    .commit();

        } catch (Exception ignored) {
        }
    }

    public synchronized Snapshot load() {

        String raw =
                preferences.getString(
                        KEY_STATE,
                        null);

        if (raw == null ||
                raw.trim().isEmpty()) {

            return null;
        }

        try {

            JSONObject object =
                    new JSONObject(raw);

            return new Snapshot(
                    object.optString(
                            "executionId",
                            ""),

                    object.optString(
                            "command",
                            ""),

                    object.optLong(
                            "startedAt",
                            0L),

                    object.optLong(
                            "completedAt",
                            0L),

                    object.optString(
                            "state",
                            ExecutionStatus.FAILED.name()),

                    object.optInt(
                            "attemptCount",
                            0),

                    object.optString(
                            "failureReason",
                            ""));

        } catch (Exception ignored) {

            return null;
        }
    }

    public synchronized void clear() {

        preferences.edit()
                .remove(KEY_STATE)
                .commit();
    }

    public static final class Snapshot {

        private final String executionId;

        private final String command;

        private final long startedAt;

        private final long completedAt;

        private final String state;

        private final int attemptCount;

        private final String failureReason;

        Snapshot(
                String executionId,
                String command,
                long startedAt,
                long completedAt,
                String state,
                int attemptCount,
                String failureReason) {

            this.executionId =
                    executionId;

            this.command =
                    command;

            this.startedAt =
                    startedAt;

            this.completedAt =
                    completedAt;

            this.state =
                    state;

            this.attemptCount =
                    attemptCount;

            this.failureReason =
                    failureReason;
        }

        public String getExecutionId() {
            return executionId;
        }

        public String getCommand() {
            return command;
        }

        public long getStartedAt() {
            return startedAt;
        }

        public long getCompletedAt() {
            return completedAt;
        }

        public String getState() {
            return state;
        }

        public int getAttemptCount() {
            return attemptCount;
        }

        public String getFailureReason() {
            return failureReason;
        }
    }
}
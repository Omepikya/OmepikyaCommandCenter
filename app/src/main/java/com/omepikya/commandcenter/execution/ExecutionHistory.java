package com.omepikya.commandcenter.execution;

import android.content.Context;
import android.content.SharedPreferences;

import com.omepikya.commandcenter.core.CommandResult;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * Persistent, bounded execution history with query helpers.
 */
public class ExecutionHistory {

    private static final String PREF_NAME =
            "omepikya_execution_history";

    private static final String KEY_HISTORY =
            "history";

    private static final int DEFAULT_MAX_ENTRIES =
            100;

    private final List<ExecutionResult> history;

    private final int maxEntries;

    private final SharedPreferences preferences;

    public ExecutionHistory() {

        maxEntries =
                DEFAULT_MAX_ENTRIES;

        history =
                new ArrayList<>();

        preferences =
                null;
    }

    public ExecutionHistory(
            Context context) {

        this(
                context,
                DEFAULT_MAX_ENTRIES);
    }

    public ExecutionHistory(
            Context context,
            int maxEntries) {

        if (context == null) {

            throw new IllegalArgumentException(
                    "Context cannot be null");
        }

        if (maxEntries <= 0) {

            throw new IllegalArgumentException(
                    "maxEntries must be greater than zero");
        }

        this.maxEntries =
                maxEntries;

        preferences =
                context.getApplicationContext()
                        .getSharedPreferences(
                                PREF_NAME,
                                Context.MODE_PRIVATE);

        history =
                new ArrayList<>();

        load();
    }

    public synchronized void add(
            ExecutionResult result) {

        if (result == null) {
            return;
        }

        history.add(result);

        trim();

        save();
    }

    public synchronized ExecutionResult
    getLatest() {

        return history.isEmpty()
                ? null
                : history.get(
                        history.size() - 1);
    }

    public synchronized ExecutionResult
    getById(String executionId) {

        if (executionId == null ||
                executionId.trim().isEmpty()) {

            return null;
        }

        for (int i =
                history.size() - 1;
             i >= 0;
             i--) {

            ExecutionResult result =
                    history.get(i);

            if (result != null &&
                    executionId.equals(
                            result.getExecutionId())) {

                return result;
            }
        }

        return null;
    }

    public synchronized List<ExecutionResult>
    getAll() {

        return Collections.unmodifiableList(
                new ArrayList<>(
                        history));
    }

    public synchronized List<ExecutionResult>
    getRecent(int count) {

        if (count <= 0 ||
                history.isEmpty()) {

            return Collections.emptyList();
        }

        int start =
                Math.max(
                        0,
                        history.size() - count);

        return Collections.unmodifiableList(
                new ArrayList<>(
                        history.subList(
                                start,
                                history.size())));
    }

    public synchronized List<ExecutionResult>
    search(
            String query,
            int limit) {

        if (query == null ||
                query.trim().isEmpty() ||
                limit <= 0) {

            return Collections.emptyList();
        }

        String needle =
                query.trim()
                        .toLowerCase(
                                Locale.US);

        List<ExecutionResult> matches =
                new ArrayList<>();

        for (int i =
                history.size() - 1;

             i >= 0 &&
             matches.size() < limit;

             i--) {

            ExecutionResult result =
                    history.get(i);

            if (result == null) {
                continue;
            }

            String command =
                    result.getCommand() == null
                            ? ""
                            : result.getCommand();

            String message =
                    result.getMessage() == null
                            ? ""
                            : result.getMessage();

            String executionId =
                    result.getExecutionId() == null
                            ? ""
                            : result.getExecutionId();

            if (command
                    .toLowerCase(Locale.US)
                    .contains(needle)

                    ||

                    message
                    .toLowerCase(Locale.US)
                    .contains(needle)

                    ||

                    executionId
                    .toLowerCase(Locale.US)
                    .contains(needle)) {

                matches.add(result);
            }
        }

        return Collections.unmodifiableList(
                matches);
    }

    public synchronized List<ExecutionResult>
    getByStatus(
            ExecutionStatus status) {

        if (status == null) {

            return Collections.emptyList();
        }

        List<ExecutionResult> matches =
                new ArrayList<>();

        for (int i =
                history.size() - 1;
             i >= 0;
             i--) {

            ExecutionResult result =
                    history.get(i);

            if (result != null &&
                    result.getStatus() == status) {

                matches.add(result);
            }
        }

        return Collections.unmodifiableList(
                matches);
    }

    public synchronized int size() {
        return history.size();
    }

    public synchronized void clear() {

        history.clear();

        if (preferences != null) {

            preferences.edit()
                    .remove(KEY_HISTORY)
                    .apply();
        }
    }

    public synchronized int
    getSuccessfulCount() {

        int count = 0;

        for (ExecutionResult result :
                history) {

            if (result != null &&
                    result.isSuccess()) {

                count++;
            }
        }

        return count;
    }

    public synchronized int
    getFailedCount() {

        int count = 0;

        for (ExecutionResult result :
                history) {

            if (result != null &&
                    !result.isSuccess()) {

                count++;
            }
        }

        return count;
    }

    public synchronized double
    getSuccessRate() {

        if (history.isEmpty()) {
            return 0.0;
        }

        return (
                (double) getSuccessfulCount()
                        /
                (double) history.size()
        ) * 100.0;
    }

    private void trim() {

        while (history.size() >
                maxEntries) {

            history.remove(0);
        }
    }

    private void save() {

        if (preferences == null) {
            return;
        }

        JSONArray array =
                new JSONArray();

        for (ExecutionResult result :
                history) {

            if (result == null) {
                continue;
            }

            try {

                JSONObject object =
                        new JSONObject();

                object.put(
                        "executionId",
                        result.getExecutionId());

                object.put(
                        "command",
                        result.getCommand());

                object.put(
                        "success",
                        result.isSuccess());

                object.put(
                        "message",
                        result.getMessage());

                object.put(
                        "timestamp",
                        result.getTimestamp());

                object.put(
                        "attemptCount",
                        result.getAttemptCount());

                object.put(
                        "status",
                        result.getStatus().name());

                array.put(object);

            } catch (Exception ignored) {
            }
        }

        preferences.edit()
                .putString(
                        KEY_HISTORY,
                        array.toString())
                .apply();
    }

    private void load() {

        if (preferences == null) {
            return;
        }

        String raw =
                preferences.getString(
                        KEY_HISTORY,
                        null);

        if (raw == null ||
                raw.trim().isEmpty()) {

            return;
        }

        try {

            JSONArray array =
                    new JSONArray(raw);

            for (int i = 0;
                 i < array.length();
                 i++) {

                JSONObject object =
                        array.optJSONObject(i);

                if (object == null) {
                    continue;
                }

                String executionId =
                        object.optString(
                                "executionId",
                                "");

                String command =
                        object.optString(
                                "command",
                                "");

                boolean success =
                        object.optBoolean(
                                "success",
                                false);

                String message =
                        object.optString(
                                "message",
                                "");

                long timestamp =
                        object.optLong(
                                "timestamp",
                                0L);

                int attempts =
                        object.optInt(
                                "attemptCount",
                                0);

                ExecutionStatus status;

                try {

                    status =
                            ExecutionStatus.valueOf(
                                    object.optString(
                                            "status",
                                            success
                                                    ? "SUCCESS"
                                                    : "FAILED"));

                } catch (Exception ignored) {

                    status =
                            success
                                    ? ExecutionStatus.SUCCESS
                                    : ExecutionStatus.FAILED;
                }

                CommandResult result =
                        success
                                ? CommandResult.success(
                                        message)
                                : CommandResult.failure(
                                        message);

                history.add(
                        new ExecutionResult(
                                executionId,
                                command,
                                success,
                                message,
                                timestamp,
                                attempts,
                                result,
                                status));
            }

            trim();

        } catch (Exception ignored) {

            history.clear();
        }
    }
}
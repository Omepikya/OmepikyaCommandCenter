package com.omepikya.commandcenter.automation;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Persistent storage for automation tasks.
 *
 * All persistence writes are performed on a dedicated background executor
 * so the Android main/UI thread is never blocked by JSON serialization
 * or SharedPreferences writes.
 */
public class PersistentTaskStore {

    private static final String TAG =
            "PersistentTaskStore";

    private static final String PREF_NAME =
            "omepikya_automation_store";

    private static final String KEY_TASKS =
            "tasks";

    private final SharedPreferences preferences;

    private final ExecutorService ioExecutor;

    public PersistentTaskStore(Context context) {

        if (context == null) {
            throw new IllegalArgumentException(
                    "Context cannot be null"
            );
        }

        Context appContext =
                context.getApplicationContext();

        preferences =
                appContext.getSharedPreferences(
                        PREF_NAME,
                        Context.MODE_PRIVATE
                );

        ioExecutor =
                Executors.newSingleThreadExecutor(
                        runnable -> {

                            Thread thread =
                                    new Thread(
                                            runnable,
                                            "Omepikya-TaskStore"
                                    );

                            thread.setDaemon(true);

                            return thread;
                        }
                );
    }

    /**
     * Saves or replaces a single task.
     *
     * The actual disk write is asynchronous.
     */
    public synchronized void saveTask(
            AutomationTask task
    ) {

        if (task == null
                || isEmpty(task.getId())) {

            return;
        }

        List<AutomationTask> tasks =
                loadTasks();

        boolean replaced = false;

        for (int i = 0; i < tasks.size(); i++) {

            AutomationTask existing =
                    tasks.get(i);

            if (existing != null
                    && task.getId().equals(
                    existing.getId())) {

                tasks.set(i, task);

                replaced = true;

                break;
            }
        }

        if (!replaced) {
            tasks.add(task);
        }

        sortTasks(tasks);

        enqueueWrite(tasks);
    }

    /**
     * Saves the complete task list asynchronously.
     */
    public synchronized void saveTasks(
            List<AutomationTask> tasks
    ) {

        enqueueWrite(tasks);
    }

    /**
     * Loads tasks from SharedPreferences.
     *
     * SharedPreferences values are cached by Android, so this operation
     * does not perform a raw file read on every call. JSON parsing is
     * performed here and callers that require fully asynchronous loading
     * should use loadTasksAsync().
     */
    public synchronized List<AutomationTask> loadTasks() {

        String rawJson =
                preferences.getString(
                        KEY_TASKS,
                        null
                );

        return parseTasks(rawJson);
    }

    /**
     * Asynchronously loads all stored tasks.
     */
    public void loadTasksAsync(
            StoreCallback<List<AutomationTask>> callback
    ) {

        ioExecutor.execute(() -> {

            List<AutomationTask> result;

            synchronized (PersistentTaskStore.this) {

                result = loadTasks();
            }

            if (callback != null) {

                callback.onComplete(result);
            }
        });
    }

    /**
     * Finds a task by ID.
     */
    public synchronized AutomationTask getTask(
            String taskId
    ) {

        if (isEmpty(taskId)) {
            return null;
        }

        List<AutomationTask> tasks =
                loadTasks();

        for (AutomationTask task : tasks) {

            if (task != null
                    && taskId.equals(
                    task.getId())) {

                return task;
            }
        }

        return null;
    }

    /**
     * Deletes a task asynchronously.
     *
     * Returns true when the task existed in the current snapshot.
     */
    public synchronized boolean deleteTask(
            String taskId
    ) {

        if (isEmpty(taskId)) {
            return false;
        }

        List<AutomationTask> tasks =
                loadTasks();

        boolean removed = false;

        for (int i = tasks.size() - 1;
             i >= 0;
             i--) {

            AutomationTask task =
                    tasks.get(i);

            if (task != null
                    && taskId.equals(
                    task.getId())) {

                tasks.remove(i);

                removed = true;
            }
        }

        if (removed) {

            enqueueWrite(tasks);
        }

        return removed;
    }

    /**
     * Clears all persisted automation tasks.
     */
    public void clear() {

        ioExecutor.execute(() -> {

            try {

                preferences
                        .edit()
                        .remove(KEY_TASKS)
                        .apply();

            } catch (Exception e) {

                Log.e(
                        TAG,
                        "Failed to clear automation storage",
                        e
                );
            }
        });
    }

    /**
     * Releases the background executor.
     */
    public void shutdown() {

        ioExecutor.shutdown();
    }

    public interface StoreCallback<T> {

        void onComplete(T result);
    }

    /**
     * Serializes and persists a task snapshot on the background executor.
     */
    private void enqueueWrite(
            List<AutomationTask> tasks
    ) {

        final List<AutomationTask> snapshot;

        if (tasks == null) {

            snapshot =
                    new ArrayList<>();

        } else {

            snapshot =
                    new ArrayList<>(
                            tasks
                    );
        }

        sortTasks(snapshot);

        ioExecutor.execute(() -> {

            try {

                JSONArray array =
                        new JSONArray();

                for (AutomationTask task
                        : snapshot) {

                    if (task == null) {
                        continue;
                    }

                    array.put(
                            toJson(task)
                    );
                }

                /*
                 * apply() is intentionally used instead of commit().
                 * commit() blocks the calling thread.
                 */
                preferences
                        .edit()
                        .putString(
                                KEY_TASKS,
                                array.toString()
                        )
                        .apply();

            } catch (Exception e) {

                Log.e(
                        TAG,
                        "Failed to persist automation tasks",
                        e
                );
            }
        });
    }

    /**
     * Converts persisted JSON into AutomationTask objects.
     */
    private List<AutomationTask> parseTasks(
            String rawJson
    ) {

        List<AutomationTask> result =
                new ArrayList<>();

        if (rawJson == null
                || rawJson.trim().isEmpty()) {

            return result;
        }

        try {

            JSONArray array =
                    new JSONArray(rawJson);

            for (int i = 0;
                 i < array.length();
                 i++) {

                JSONObject object =
                        array.optJSONObject(i);

                if (object == null) {
                    continue;
                }

                AutomationTask task =
                        fromJson(object);

                if (task != null) {

                    result.add(task);
                }
            }

        } catch (Exception e) {

            Log.e(
                    TAG,
                    "Failed to parse persisted automation tasks",
                    e
            );
        }

        sortTasks(result);

        return result;
    }

    /**
     * Converts an AutomationTask into JSON.
     */
    private JSONObject toJson(
            AutomationTask task
    ) throws Exception {

        JSONObject object =
                new JSONObject();

        object.put(
                "id",
                task.getId()
        );

        object.put(
                "name",
                task.getName()
        );

        object.put(
                "command",
                task.getCommand()
        );

        object.put(
                "triggerTime",
                task.getTriggerTime()
        );

        object.put(
                "createdTime",
                task.getCreatedTime()
        );

        object.put(
                "enabled",
                task.isEnabled()
        );

        object.put(
                "status",
                task.getStatus()
        );

        object.put(
                "attemptCount",
                task.getAttemptCount()
        );

        object.put(
                "lastExecutionTime",
                task.getLastExecutionTime()
        );

        object.put(
                "lastError",
                task.getLastError()
        );

        return object;
    }

    /**
     * Reconstructs an AutomationTask from JSON.
     */
    private AutomationTask fromJson(
            JSONObject object
    ) {

        try {

            String id =
                    object.optString(
                            "id",
                            null
                    );

            String name =
                    object.optString(
                            "name",
                            "Omepikya automation"
                    );

            String command =
                    object.optString(
                            "command",
                            ""
                    );

            long triggerTime =
                    object.optLong(
                            "triggerTime",
                            0L
                    );

            long createdTime =
                    object.optLong(
                            "createdTime",
                            System.currentTimeMillis()
                    );

            boolean enabled =
                    object.optBoolean(
                            "enabled",
                            true
                    );

            String status =
                    object.optString(
                            "status",
                            AutomationTask.STATUS_PENDING
                    );

            int attemptCount =
                    object.optInt(
                            "attemptCount",
                            0
                    );

            long lastExecutionTime =
                    object.optLong(
                            "lastExecutionTime",
                            0L
                    );

            String lastError =
                    object.optString(
                            "lastError",
                            null
                    );

            if (id == null
                    || id.trim().isEmpty()) {

                return null;
            }

            return new AutomationTask(
                    id,
                    name,
                    command,
                    triggerTime,
                    createdTime,
                    enabled,
                    status,
                    attemptCount,
                    lastExecutionTime,
                    lastError
            );

        } catch (Exception e) {

            Log.e(
                    TAG,
                    "Skipping invalid persisted automation task",
                    e
            );

            return null;
        }
    }

    private void sortTasks(
            List<AutomationTask> taskList
    ) {

        if (taskList == null
                || taskList.size() < 2) {

            return;
        }

        Collections.sort(
                taskList,
                new Comparator<AutomationTask>() {

                    @Override
                    public int compare(
                            AutomationTask first,
                            AutomationTask second
                    ) {

                        if (first == null
                                && second == null) {

                            return 0;
                        }

                        if (first == null) {
                            return 1;
                        }

                        if (second == null) {
                            return -1;
                        }

                        return Long.compare(
                                first.getTriggerTime(),
                                second.getTriggerTime()
                        );
                    }
                }
        );
    }

    private boolean isEmpty(
            String value
    ) {

        return value == null
                || value.trim().isEmpty();
    }
}
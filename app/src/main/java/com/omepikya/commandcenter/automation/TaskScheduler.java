package com.omepikya.commandcenter.automation;

import android.content.Context;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Thread-safe task repository used by AutomationCore.
 *
 * AlarmManager/PendingIntent work intentionally lives in AutomationCore so
 * there is a single scheduling implementation and no duplicate alarms.
 */
public class TaskScheduler {

    private final List<AutomationTask> tasks;
    private final PersistentTaskStore store;

    public TaskScheduler(Context context) {
        if (context == null) {
            throw new IllegalArgumentException("Context cannot be null");
        }

        store = new PersistentTaskStore(context.getApplicationContext());
        tasks = new ArrayList<>(store.loadTasks());
        sortTasks();
    }

    public synchronized boolean addTask(AutomationTask task) {
        if (task == null || isEmpty(task.getId())) {
            return false;
        }

        removeTaskInternal(task.getId());
        tasks.add(task);
        sortTasks();
        store.saveTasks(tasks);
        return true;
    }

    public synchronized boolean updateTask(AutomationTask task) {
        if (task == null || isEmpty(task.getId())) {
            return false;
        }

        for (int i = 0; i < tasks.size(); i++) {
            AutomationTask existing = tasks.get(i);

            if (existing != null && task.getId().equals(existing.getId())) {
                tasks.set(i, task);
                sortTasks();
                store.saveTasks(tasks);
                return true;
            }
        }

        return addTask(task);
    }

    public synchronized boolean removeTask(String taskId) {
        if (isEmpty(taskId)) {
            return false;
        }

        boolean removed = removeTaskInternal(taskId);
        if (removed) {
            store.saveTasks(tasks);
        }
        return removed;
    }

    private boolean removeTaskInternal(String taskId) {
        boolean removed = false;

        for (int i = tasks.size() - 1; i >= 0; i--) {
            AutomationTask task = tasks.get(i);

            if (task != null && taskId.equals(task.getId())) {
                tasks.remove(i);
                removed = true;
            }
        }

        return removed;
    }

    public synchronized AutomationTask getTask(String taskId) {
        if (isEmpty(taskId)) {
            return null;
        }

        for (AutomationTask task : tasks) {
            if (task != null && taskId.equals(task.getId())) {
                return task;
            }
        }

        return null;
    }

    public synchronized List<AutomationTask> getTasks() {
        return Collections.unmodifiableList(new ArrayList<>(tasks));
    }

    public synchronized List<AutomationTask> getDueTasks() {
        List<AutomationTask> dueTasks = new ArrayList<>();

        for (AutomationTask task : tasks) {
            if (task != null && task.isDue()) {
                dueTasks.add(task);
            }
        }

        return dueTasks;
    }

    public synchronized AutomationTask getNextTask() {
        for (AutomationTask task : tasks) {
            if (task != null && task.isEnabled()) {
                return task;
            }
        }
        return null;
    }

    public synchronized int getTaskCount() {
        return tasks.size();
    }

    public synchronized int getEnabledTaskCount() {
        int count = 0;
        for (AutomationTask task : tasks) {
            if (task != null && task.isEnabled()) {
                count++;
            }
        }
        return count;
    }

    public synchronized void clear() {
        tasks.clear();
        store.clear();
    }

    public synchronized boolean contains(String taskId) {
        return getTask(taskId) != null;
    }

    public synchronized void persist(AutomationTask task) {
        if (task != null) {
            updateTask(task);
        }
    }

    private void sortTasks() {
        Collections.sort(tasks, new Comparator<AutomationTask>() {
            @Override
            public int compare(AutomationTask first, AutomationTask second) {
                if (first == null && second == null) {
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
        });
    }

    private boolean isEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }
}
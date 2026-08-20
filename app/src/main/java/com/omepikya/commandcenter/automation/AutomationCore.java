package com.omepikya.commandcenter.automation;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;

import java.util.List;
import java.util.UUID;

/**
 * Persistent automation manager.
 *
 * AutomationCore owns all AlarmManager/PendingIntent scheduling. This keeps
 * scheduling logic in one place and prevents duplicate alarm implementations.
 */
public class AutomationCore {

    private final Context context;
    private final AlarmManager alarmManager;
    private final TaskScheduler taskScheduler;
    private final TriggerManager triggerManager;

    public AutomationCore(Context context) {
        if (context == null) {
            throw new IllegalArgumentException("Context cannot be null");
        }

        this.context = context.getApplicationContext();
        this.alarmManager = (AlarmManager) this.context.getSystemService(
                Context.ALARM_SERVICE
        );
        this.taskScheduler = new TaskScheduler(this.context);
        this.triggerManager = new TriggerManager();
    }

    public synchronized String createTask(
            String name,
            String command,
            long triggerTime
    ) {
        if (command == null || command.trim().isEmpty()) {
            return null;
        }

        if (triggerTime <= System.currentTimeMillis()) {
            return null;
        }

        if (alarmManager == null) {
            return null;
        }

        String id = UUID.randomUUID().toString();
        String taskName = name == null || name.trim().isEmpty()
                ? "Omepikya automation"
                : name.trim();

        AutomationTask task = new AutomationTask(
                id,
                taskName,
                command.trim(),
                triggerTime
        );

        if (!taskScheduler.addTask(task)) {
            return null;
        }

        if (!scheduleAlarm(task)) {
            taskScheduler.removeTask(id);
            return null;
        }

        return id;
    }

    public synchronized boolean cancelTask(String taskId) {
        if (isEmpty(taskId)) {
            return false;
        }

        AutomationTask task = taskScheduler.getTask(taskId);
        if (task == null) {
            return false;
        }

        cancelAlarm(task);
        task.markCancelled();
        taskScheduler.removeTask(taskId);
        return true;
    }

    /**
     * Restores all pending future alarms. Due tasks are marked ready instead
     * of being incorrectly marked ready merely because an alarm was restored.
     */
    public synchronized int restoreAlarms() {
        int restored = 0;
        long now = System.currentTimeMillis();

        List<AutomationTask> tasks = taskScheduler.getTasks();

        for (AutomationTask task : tasks) {
            if (task == null || !task.isEnabled() || task.isTerminal()) {
                continue;
            }

            if (task.getTriggerTime() <= now) {
                if (!AutomationTask.STATUS_READY.equals(task.getStatus())) {
                    task.markReady();
                    taskScheduler.persist(task);
                }
                continue;
            }

            if (scheduleAlarm(task)) {
                restored++;
            }
        }

        return restored;
    }

    public synchronized void checkTasks() {
        List<AutomationTask> dueTasks = taskScheduler.getDueTasks();

        if (dueTasks == null || dueTasks.isEmpty()) {
            return;
        }

        for (AutomationTask task : dueTasks) {
            if (task != null) {
                triggerManager.notifyTaskDue(task);
            }
        }
    }

    public synchronized AutomationTask getTask(String taskId) {
        if (isEmpty(taskId)) {
            return null;
        }
        return taskScheduler.getTask(taskId);
    }

    public synchronized List<AutomationTask> getTasks() {
        return taskScheduler.getTasks();
    }

    public TaskScheduler getTaskScheduler() {
        return taskScheduler;
    }

    public TriggerManager getTriggerManager() {
        return triggerManager;
    }

    private boolean scheduleAlarm(AutomationTask task) {
        if (task == null || alarmManager == null) {
            return false;
        }

        long triggerTime = task.getTriggerTime();
        if (triggerTime <= System.currentTimeMillis()) {
            return false;
        }

        PendingIntent pendingIntent = null;

        try {
            Intent intent = new Intent(context, AutomationReceiver.class);
            intent.setAction("com.omepikya.commandcenter.EXECUTE_TASK");
            intent.setPackage(context.getPackageName());
            intent.setData(Uri.parse("omepikya://automation/" + task.getId()));
            intent.putExtra(AutomationReceiver.EXTRA_TASK_ID, task.getId());
            intent.putExtra(AutomationReceiver.EXTRA_TASK_NAME, task.getName());
            intent.putExtra(AutomationReceiver.EXTRA_COMMAND, task.getCommand());

            int flags = PendingIntent.FLAG_UPDATE_CURRENT;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                flags |= PendingIntent.FLAG_IMMUTABLE;
            }

            pendingIntent = PendingIntent.getBroadcast(
                    context,
                    requestCode(task.getId()),
                    intent,
                    flags
            );

            if (pendingIntent == null) {
                return false;
            }

            if (isExactAlarmAccessDenied()) {
                scheduleInexact(triggerTime, pendingIntent);
                return true;
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        triggerTime,
                        pendingIntent
                );
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                alarmManager.setExact(
                        AlarmManager.RTC_WAKEUP,
                        triggerTime,
                        pendingIntent
                );
            } else {
                alarmManager.set(
                        AlarmManager.RTC_WAKEUP,
                        triggerTime,
                        pendingIntent
                );
            }

            return true;

        } catch (SecurityException deniedExactAlarm) {
            // Do not delete the user's task just because exact-alarm access is denied.
            try {
                if (pendingIntent == null) {
                    return false;
                }
                scheduleInexact(triggerTime, pendingIntent);
                return true;
            } catch (Exception ignored) {
                return false;
            }
        } catch (Exception ignored) {
            return false;
        }
    }

    private void scheduleInexact(
            long triggerTime,
            PendingIntent pendingIntent
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerTime,
                    pendingIntent
            );
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            alarmManager.set(
                    AlarmManager.RTC_WAKEUP,
                    triggerTime,
                    pendingIntent
            );
        } else {
            alarmManager.set(
                    AlarmManager.RTC_WAKEUP,
                    triggerTime,
                    pendingIntent
            );
        }
    }

    private void cancelAlarm(AutomationTask task) {
        if (alarmManager == null || task == null) {
            return;
        }

        try {
            Intent intent = new Intent(context, AutomationReceiver.class);
            intent.setAction("com.omepikya.commandcenter.EXECUTE_TASK");
            intent.setPackage(context.getPackageName());
            intent.setData(Uri.parse("omepikya://automation/" + task.getId()));

            int flags = PendingIntent.FLAG_UPDATE_CURRENT;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                flags |= PendingIntent.FLAG_IMMUTABLE;
            }

            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    context,
                    requestCode(task.getId()),
                    intent,
                    flags
            );

            if (pendingIntent != null) {
                alarmManager.cancel(pendingIntent);
                pendingIntent.cancel();
            }
        } catch (Exception ignored) {
        }
    }

    /**
     * Uses reflection so this project can keep its existing compileSdk/AGP
     * toolchain while still avoiding exact-alarm crashes on Android 12+.
     */
    private boolean isExactAlarmAccessDenied() {
        if (Build.VERSION.SDK_INT < 31 || alarmManager == null) {
            return false;
        }

        try {
            java.lang.reflect.Method method =
                    AlarmManager.class.getMethod("canScheduleExactAlarms");
            Object result = method.invoke(alarmManager);
            return result instanceof Boolean && !((Boolean) result);
        } catch (Exception ignored) {
            // If the API cannot be queried, let setExactAndAllowWhileIdle
            // attempt the operation and fall back through SecurityException.
            return false;
        }
    }

    private int requestCode(String taskId) {
        if (isEmpty(taskId)) {
            return 1;
        }

        int hash = taskId.hashCode();
        if (hash == Integer.MIN_VALUE) {
            return 1;
        }

        hash = Math.abs(hash);
        return hash == 0 ? 1 : hash;
    }

    private boolean isEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }
}
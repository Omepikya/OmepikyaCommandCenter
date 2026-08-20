package com.omepikya.commandcenter.automation;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.omepikya.commandcenter.MainActivity;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Receives scheduled automation alarms.
 *
 * The receiver does not launch an Activity directly. It updates the task,
 * posts a notification, and lets the user open MainActivity from that
 * notification. Work that touches persistence is moved off the receiver's
 * main thread.
 */
public class AutomationReceiver extends BroadcastReceiver {

    public static final String EXTRA_TASK_ID =
            "omepikya.automation.TASK_ID";

    public static final String EXTRA_TASK_NAME =
            "omepikya.automation.TASK_NAME";

    public static final String EXTRA_COMMAND =
            "omepikya.automation.COMMAND";

    public static final String EXTRA_AUTO_EXECUTE =
            "omepikya.automation.AUTO_EXECUTE";

    private static final String CHANNEL_ID =
            "omepikya_automation";

    private static final int NOTIFICATION_ID_BASE =
            41000;

    private static final ExecutorService EXECUTOR =
            Executors.newSingleThreadExecutor(runnable -> {
                Thread thread = new Thread(runnable, "Omepikya-AutomationReceiver");
                thread.setDaemon(true);
                return thread;
            });

    @Override
    public void onReceive(Context context, Intent intent) {
        if (context == null || intent == null) {
            return;
        }

        final PendingResult pendingResult = goAsync();
        final Context appContext = context.getApplicationContext();
        final String taskId = intent.getStringExtra(EXTRA_TASK_ID);
        final String taskName = normalizeTaskName(
                intent.getStringExtra(EXTRA_TASK_NAME)
        );
        final String command = intent.getStringExtra(EXTRA_COMMAND) == null
                ? ""
                : intent.getStringExtra(EXTRA_COMMAND);

        EXECUTOR.execute(() -> {
            try {
                AutomationCore automationCore = new AutomationCore(appContext);
                AutomationTask task = automationCore.getTask(taskId);

                if (task != null) {
                    task.markReady();
                    automationCore.getTaskScheduler().persist(task);
                }

                showNotification(
                        appContext,
                        taskId,
                        taskName,
                        command
                );

            } finally {
                pendingResult.finish();
            }
        });
    }

    private void showNotification(
            Context context,
            String taskId,
            String taskName,
            String command
    ) {
        NotificationManager manager =
                (NotificationManager) context.getSystemService(
                        Context.NOTIFICATION_SERVICE
                );

        if (manager == null) {
            return;
        }

        createNotificationChannel(manager);

        Intent openIntent = new Intent(context, MainActivity.class);
        openIntent.putExtra(EXTRA_TASK_ID, taskId);
        openIntent.putExtra(EXTRA_TASK_NAME, taskName);
        openIntent.putExtra(EXTRA_COMMAND, command);
        openIntent.putExtra(EXTRA_AUTO_EXECUTE, true);
        openIntent.addFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_SINGLE_TOP
                        | Intent.FLAG_ACTIVITY_CLEAR_TOP
        );

        int requestCode = makeNotificationId(taskId);
        int flags = PendingIntent.FLAG_UPDATE_CURRENT;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            flags |= PendingIntent.FLAG_IMMUTABLE;
        }

        PendingIntent pendingIntent;
        try {
            pendingIntent = PendingIntent.getActivity(
                    context,
                    requestCode,
                    openIntent,
                    flags
            );
        } catch (Exception ignored) {
            return;
        }

        Notification.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder = new Notification.Builder(context, CHANNEL_ID);
        } else {
            builder = new Notification.Builder(context);
        }

        builder
                .setContentTitle(taskName)
                .setContentText(
                        command.isEmpty()
                                ? "Scheduled automation is ready."
                                : "Tap to run: " + command
                )
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setPriority(Notification.PRIORITY_HIGH);

        try {
            manager.notify(requestCode, builder.build());
        } catch (SecurityException ignored) {
            // Notifications can be denied on Android 13+.
        }
    }

    private void createNotificationChannel(NotificationManager manager) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return;
        }

        NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                "Omepikya Automation",
                NotificationManager.IMPORTANCE_HIGH
        );

        channel.setDescription("Scheduled Omepikya commands");
        channel.setShowBadge(true);
        manager.createNotificationChannel(channel);
    }

    private int makeNotificationId(String taskId) {
        if (taskId == null || taskId.trim().isEmpty()) {
            return NOTIFICATION_ID_BASE;
        }

        int hash = taskId.hashCode();
        if (hash == Integer.MIN_VALUE) {
            hash = 1;
        }

        return NOTIFICATION_ID_BASE + Math.abs(hash % 10000);
    }

    private String normalizeTaskName(String taskName) {
        if (taskName == null || taskName.trim().isEmpty()) {
            return "Omepikya automation";
        }
        return taskName.trim();
    }
}
package com.omepikya.commandcenter.automation;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Restores Omepikya alarms after Android restarts without doing task-store
 * parsing or scheduling work on the broadcast receiver's main thread.
 */
public class BootReceiver extends BroadcastReceiver {

    private static final ExecutorService BOOT_EXECUTOR =
            Executors.newSingleThreadExecutor(runnable -> {
                Thread thread = new Thread(runnable, "Omepikya-BootRestore");
                thread.setDaemon(true);
                return thread;
            });

    @Override
    public void onReceive(Context context, Intent intent) {
        if (context == null || intent == null) {
            return;
        }

        String action = intent.getAction();
        if (!Intent.ACTION_BOOT_COMPLETED.equals(action)
                && !Intent.ACTION_LOCKED_BOOT_COMPLETED.equals(action)) {
            return;
        }

        final PendingResult pendingResult = goAsync();
        final Context appContext = context.getApplicationContext();

        BOOT_EXECUTOR.execute(() -> {
            try {
                AutomationCore core = new AutomationCore(appContext);
                core.restoreAlarms();
            } catch (Exception ignored) {
                // Boot restoration must never prevent Android from completing boot.
            } finally {
                pendingResult.finish();
            }
        });
    }
}
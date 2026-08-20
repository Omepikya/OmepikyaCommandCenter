package com.omepikya.commandcenter.automation;

/**
 * Persistent scheduled task used by Omepikya automation.
 */
public class AutomationTask {

    public static final String STATUS_PENDING = "PENDING";
    public static final String STATUS_READY = "READY";
    public static final String STATUS_RUNNING = "RUNNING";
    public static final String STATUS_SUCCESS = "SUCCESS";
    public static final String STATUS_FAILED = "FAILED";
    public static final String STATUS_CANCELLED = "CANCELLED";

    private final String id;
    private final String name;
    private final String command;
    private final long triggerTime;
    private final long createdTime;

    private boolean enabled;
    private String status;
    private int attemptCount;
    private long lastExecutionTime;
    private String lastError;

    public AutomationTask(
            String id,
            String name,
            String command,
            long triggerTime
    ) {
        this(
                id,
                name,
                command,
                triggerTime,
                System.currentTimeMillis(),
                true,
                STATUS_PENDING,
                0,
                0L,
                null
        );
    }

    public AutomationTask(
            String id,
            String name,
            String command,
            long triggerTime,
            long createdTime,
            boolean enabled,
            String status,
            int attemptCount,
            long lastExecutionTime,
            String lastError
    ) {
        this.id = id;
        this.name = name;
        this.command = command;
        this.triggerTime = triggerTime;
        this.createdTime = createdTime;
        this.enabled = enabled;

        this.status =
                isValidStatus(status)
                        ? status
                        : STATUS_PENDING;

        this.attemptCount =
                Math.max(0, attemptCount);

        this.lastExecutionTime =
                Math.max(0L, lastExecutionTime);

        this.lastError = lastError;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCommand() {
        return command;
    }

    public long getTriggerTime() {
        return triggerTime;
    }

    public long getCreatedTime() {
        return createdTime;
    }

    public synchronized boolean isEnabled() {
        return enabled;
    }

    public synchronized void setEnabled(
            boolean enabled
    ) {
        this.enabled = enabled;
    }

    public synchronized String getStatus() {
        return status;
    }

    public synchronized void setStatus(
            String status
    ) {
        if (isValidStatus(status)) {
            this.status = status;
        }
    }

    public synchronized int getAttemptCount() {
        return attemptCount;
    }

    public synchronized long getLastExecutionTime() {
        return lastExecutionTime;
    }

    public synchronized String getLastError() {
        return lastError;
    }

    public synchronized void markReady() {
        status = STATUS_READY;
        lastError = null;
    }

    public synchronized void markRunning() {
        status = STATUS_RUNNING;
        attemptCount++;
        lastExecutionTime =
                System.currentTimeMillis();
        lastError = null;
    }

    public synchronized void markSuccess() {
        status = STATUS_SUCCESS;
        enabled = false;
        lastExecutionTime =
                System.currentTimeMillis();
        lastError = null;
    }

    public synchronized void markFailed(
            String error
    ) {
        status = STATUS_FAILED;
        enabled = false;
        lastExecutionTime =
                System.currentTimeMillis();
        lastError = error;
    }

    public synchronized void markCancelled() {
        status = STATUS_CANCELLED;
        enabled = false;
        lastExecutionTime =
                System.currentTimeMillis();
    }

    public synchronized boolean isDue() {
        return enabled
                && System.currentTimeMillis()
                >= triggerTime;
    }

    public synchronized boolean isTerminal() {
        return STATUS_SUCCESS.equals(status)
                || STATUS_FAILED.equals(status)
                || STATUS_CANCELLED.equals(status);
    }

    public synchronized boolean canBeRestored() {
        return enabled
                && !isTerminal()
                && triggerTime >
                System.currentTimeMillis();
    }

    private boolean isValidStatus(
            String value
    ) {
        return STATUS_PENDING.equals(value)
                || STATUS_READY.equals(value)
                || STATUS_RUNNING.equals(value)
                || STATUS_SUCCESS.equals(value)
                || STATUS_FAILED.equals(value)
                || STATUS_CANCELLED.equals(value);
    }
}
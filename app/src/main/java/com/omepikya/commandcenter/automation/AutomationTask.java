package com.omepikya.commandcenter.automation;

/**
 * Persistent scheduled task used by Omepikya automation.
 *
 * The task owns its lifecycle state and exposes controlled state
 * transitions so callers cannot accidentally move a task from a
 * terminal state back into an executable state.
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

    /**
     * Generic status setter.
     *
     * This intentionally refuses to modify a terminal task.
     * Lifecycle transitions should normally use the explicit
     * methods below.
     */
    public synchronized void setStatus(
            String status
    ) {
        if (!isValidStatus(status)) {
            return;
        }

        if (isTerminal()) {
            return;
        }

        this.status = status;
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

    /**
     * Moves a pending task into READY state.
     *
     * Returns false when the task is disabled or terminal.
     */
    public synchronized boolean markReady() {

        if (!enabled || isTerminal()) {
            return false;
        }

        if (STATUS_RUNNING.equals(status)) {
            return false;
        }

        status = STATUS_READY;
        lastError = null;

        return true;
    }

    /**
     * Claims a READY task for execution.
     *
     * This is deliberately synchronized so two callers cannot
     * simultaneously claim the same automation.
     */
    public synchronized boolean markRunning() {

        if (!enabled || isTerminal()) {
            return false;
        }

        if (!STATUS_READY.equals(status)
                && !STATUS_PENDING.equals(status)) {
            return false;
        }

        status = STATUS_RUNNING;
        attemptCount++;
        lastExecutionTime =
                System.currentTimeMillis();
        lastError = null;

        return true;
    }

    /**
     * Marks a running automation as successfully completed.
     */
    public synchronized boolean markSuccess() {

        if (!STATUS_RUNNING.equals(status)) {
            return false;
        }

        status = STATUS_SUCCESS;
        enabled = false;
        lastExecutionTime =
                System.currentTimeMillis();
        lastError = null;

        return true;
    }

    /**
     * Marks a running automation as failed.
     */
    public synchronized boolean markFailed(
            String error
    ) {

        if (!STATUS_RUNNING.equals(status)
                && !STATUS_READY.equals(status)) {
            return false;
        }

        status = STATUS_FAILED;
        enabled = false;
        lastExecutionTime =
                System.currentTimeMillis();

        if (error == null
                || error.trim().isEmpty()) {

            lastError =
                    "Automation execution failed.";

        } else {

            lastError =
                    error.trim();
        }

        return true;
    }

    /**
     * Cancels a task unless it has already reached a terminal state.
     */
    public synchronized boolean markCancelled() {

        if (isTerminal()) {
            return false;
        }

        status = STATUS_CANCELLED;
        enabled = false;
        lastExecutionTime =
                System.currentTimeMillis();

        return true;
    }

    public synchronized boolean isDue() {

        return enabled
                && !isTerminal()
                && System.currentTimeMillis()
                >= triggerTime;
    }

    public synchronized boolean isTerminal() {

        return STATUS_SUCCESS.equals(status)
                || STATUS_FAILED.equals(status)
                || STATUS_CANCELLED.equals(status);
    }

    /**
     * Returns true when the task can safely be restored into
     * AlarmManager after process/device restart.
     */
    public synchronized boolean canBeRestored() {

        return enabled
                && !isTerminal()
                && !STATUS_RUNNING.equals(status)
                && triggerTime >
                System.currentTimeMillis();
    }

    /**
     * Returns true when this task can be claimed for execution.
     */
    public synchronized boolean canStartExecution() {

        return enabled
                && !isTerminal()
                && (
                STATUS_READY.equals(status)
                        || STATUS_PENDING.equals(status)
        );
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
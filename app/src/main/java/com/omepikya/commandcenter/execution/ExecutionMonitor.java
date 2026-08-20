package com.omepikya.commandcenter.execution;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * ============================================================
 * OMEPIKYA COMMAND CENTER
 * EXECUTION MONITOR
 * ============================================================
 *
 * Phase 9I
 *
 * Live, thread-safe execution observer.
 */
public final class ExecutionMonitor
        implements ExecutionEventBus.Listener {

    private final Object lock =
            new Object();

    private final List<ExecutionEvent> events =
            new ArrayList<>();

    private String executionId = "";

    private String command = "";

    private String currentStep = "";

    private String state = "IDLE";

    private String lastMessage = "";

    private String lastFailure = "";

    private int currentStepIndex = -1;

    private int totalSteps = 0;

    private int attempts = 0;

    private int recoveryAttempts = 0;

    private long startedAt = 0L;

    private long finishedAt = 0L;

    private boolean active = false;

    private boolean paused = false;

    private boolean cancelled = false;

    private boolean safetyHold = false;

    private int maximumEvents = 300;

    public void start(
            String executionId,
            String command,
            int totalSteps) {

        synchronized (lock) {

            this.executionId =
                    safe(executionId);

            this.command =
                    safe(command);

            currentStep = "";

            state = "RECEIVED";

            lastMessage = "";

            lastFailure = "";

            currentStepIndex = -1;

            this.totalSteps =
                    Math.max(
                            0,
                            totalSteps);

            attempts = 0;
            recoveryAttempts = 0;

            startedAt =
                    System.currentTimeMillis();

            finishedAt = 0L;

            active = true;
            paused = false;
            cancelled = false;
            safetyHold = false;

            events.clear();
        }
    }

    public void updateStep(
            String step,
            int stepIndex,
            int totalSteps) {

        synchronized (lock) {

            currentStep =
                    safe(step);

            currentStepIndex =
                    Math.max(
                            -1,
                            stepIndex);

            this.totalSteps =
                    Math.max(
                            0,
                            totalSteps);

            state = "EXECUTING";

            lastMessage =
                    currentStep;
        }
    }

    public void updateAttempts(
            int attempts) {

        synchronized (lock) {

            this.attempts =
                    Math.max(
                            0,
                            attempts);
        }
    }

    public void recordRecovery() {

        synchronized (lock) {

            recoveryAttempts++;

            state = "RECOVERING";
        }
    }

    public void recordFailure(
            String message) {

        synchronized (lock) {

            lastFailure =
                    safe(message);

            lastMessage =
                    lastFailure;

            state = "FAILED";
        }
    }

    public void setState(
            String state) {

        synchronized (lock) {

            this.state =
                    normalize(state);

            if ("PAUSED".equals(this.state)) {
                paused = true;
            }

            if ("CANCELLED".equals(this.state)) {
                cancelled = true;
            }

            if ("SAFETY_HOLD".equals(this.state)) {
                safetyHold = true;
            }
        }
    }

    public void finish(
            boolean success,
            String message) {

        synchronized (lock) {

            active = false;

            finishedAt =
                    System.currentTimeMillis();

            lastMessage =
                    safe(message);

            if (success) {

                state = "SUCCESS";

            } else if (cancelled) {

                state = "CANCELLED";

            } else if (safetyHold) {

                state = "SAFETY_HOLD";

            } else {

                state = "FAILED";
            }
        }
    }

    public void pause() {

        synchronized (lock) {

            if (!active) {
                return;
            }

            paused = true;
            state = "PAUSED";
        }
    }

    public void resume() {

        synchronized (lock) {

            if (!active) {
                return;
            }

            paused = false;
            state = "EXECUTING";
        }
    }

    public void cancel(
            String reason) {

        synchronized (lock) {

            cancelled = true;

            state = "CANCELLED";

            lastMessage =
                    safe(reason);
        }
    }

    public void safetyHold(
            String reason) {

        synchronized (lock) {

            safetyHold = true;

            state = "SAFETY_HOLD";

            lastMessage =
                    safe(reason);
        }
    }

    @Override
    public void onEvent(
            ExecutionEvent event) {

        if (event == null) {
            return;
        }

        synchronized (lock) {

            if (active &&
                    !executionId.isEmpty() &&
                    !event.getExecutionId()
                            .isEmpty() &&
                    !executionId.equals(
                            event.getExecutionId())) {

                return;
            }

            events.add(event);

            while (events.size() >
                    maximumEvents) {

                events.remove(0);
            }

            String type =
                    event.getType();

            lastMessage =
                    safe(event.getMessage());

            if ("RECEIVED".equals(type)) {

                state = "RECEIVED";

            } else if ("RUNNING".equals(type) ||
                    "EXECUTION_START".equals(type)) {

                state = "EXECUTING";

            } else if ("RECOVERY_START".equals(type) ||
                    "RECOVERY_STARTED".equals(type)) {

                state = "RECOVERING";
                recoveryAttempts++;

            } else if ("REPLAN_STARTED".equals(type)) {

                state = "REPLANNING";

            } else if ("CANCEL_REQUESTED".equals(type) ||
                    "CANCELLED".equals(type)) {

                cancelled = true;
                state = "CANCELLED";

            } else if ("SAFETY_HOLD".equals(type)) {

                safetyHold = true;
                state = "SAFETY_HOLD";

            } else if ("TIMEOUT".equals(type)) {

                state = "TIMEOUT";

            } else if ("FAILED".equals(type) ||
                    "EXECUTION_FAILED".equals(type)) {

                lastFailure =
                        safe(event.getMessage());

                state = "FAILED";

            } else if ("SUCCESS".equals(type) ||
                    "COMPLETED".equals(type) ||
                    "EXECUTION_COMPLETED".equals(type)) {

                state = "SUCCESS";
            }
        }
    }

    public String getExecutionId() {

        synchronized (lock) {
            return executionId;
        }
    }

    public String getCommand() {

        synchronized (lock) {
            return command;
        }
    }

    public String getCurrentStep() {

        synchronized (lock) {
            return currentStep;
        }
    }

    public String getState() {

        synchronized (lock) {
            return state;
        }
    }

    public String getLastMessage() {

        synchronized (lock) {
            return lastMessage;
        }
    }

    public String getLastFailure() {

        synchronized (lock) {
            return lastFailure;
        }
    }

    public int getCurrentStepIndex() {

        synchronized (lock) {
            return currentStepIndex;
        }
    }

    public int getTotalSteps() {

        synchronized (lock) {
            return totalSteps;
        }
    }

    public int getAttempts() {

        synchronized (lock) {
            return attempts;
        }
    }

    public int getRecoveryAttempts() {

        synchronized (lock) {
            return recoveryAttempts;
        }
    }

    public boolean isActive() {

        synchronized (lock) {
            return active;
        }
    }

    public boolean isPaused() {

        synchronized (lock) {
            return paused;
        }
    }

    public boolean isCancelled() {

        synchronized (lock) {
            return cancelled;
        }
    }

    public boolean isSafetyHold() {

        synchronized (lock) {
            return safetyHold;
        }
    }

    public double getProgress() {

        synchronized (lock) {

            if (totalSteps <= 0) {
                return 0.0;
            }

            if ("SUCCESS".equals(state)) {
                return 1.0;
            }

            int completed =
                    Math.max(
                            0,
                            currentStepIndex);

            return Math.min(
                    1.0,
                    Math.max(
                            0.0,
                            (double) completed /
                                    (double) totalSteps));
        }
    }

    public long getElapsedMs() {

        synchronized (lock) {

            if (startedAt <= 0L) {
                return 0L;
            }

            long end =
                    finishedAt > 0L
                            ? finishedAt
                            : System.currentTimeMillis();

            return Math.max(
                    0L,
                    end - startedAt);
        }
    }

    public List<ExecutionEvent>
    getEvents() {

        synchronized (lock) {

            return Collections.unmodifiableList(
                    new ArrayList<>(
                            events));
        }
    }

    public ExecutionEvent
    getLatestEvent() {

        synchronized (lock) {

            if (events.isEmpty()) {
                return null;
            }

            return events.get(
                    events.size() - 1);
        }
    }

    public void clearEvents() {

        synchronized (lock) {
            events.clear();
        }
    }

    public void setMaximumEvents(
            int value) {

        synchronized (lock) {

            maximumEvents =
                    Math.max(
                            50,
                            Math.min(
                                    2000,
                                    value));

            while (events.size() >
                    maximumEvents) {

                events.remove(0);
            }
        }
    }

    public int getMaximumEvents() {

        synchronized (lock) {
            return maximumEvents;
        }
    }

    public void reset() {

        synchronized (lock) {

            executionId = "";
            command = "";
            currentStep = "";
            state = "IDLE";
            lastMessage = "";
            lastFailure = "";

            currentStepIndex = -1;
            totalSteps = 0;
            attempts = 0;
            recoveryAttempts = 0;

            startedAt = 0L;
            finishedAt = 0L;

            active = false;
            paused = false;
            cancelled = false;
            safetyHold = false;

            events.clear();
        }
    }

    private String safe(
            String value) {

        return value == null
                ? ""
                : value.trim();
    }

    private String normalize(
            String value) {

        String result =
                safe(value);

        return result.isEmpty()
                ? "UNKNOWN"
                : result.toUpperCase();
    }
}
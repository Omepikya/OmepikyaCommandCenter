package com.omepikya.commandcenter.execution;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * Trace of one complete execution lifecycle.
 *
 * Lightweight and compatible with the current
 * Android project.
 */
public final class ExecutionTrace {

    private final String executionId;

    private final String command;

    private final long startedAt;

    private final List<String> events =
            new ArrayList<>();

    private ExecutionStatus state =
            ExecutionStatus.PENDING;

    private long completedAt;

    private int attemptCount;

    private ExecutionFailure failure;

    public ExecutionTrace(
            String command) {

        this(
                UUID.randomUUID().toString(),
                command);
    }

    public ExecutionTrace(
            String executionId,
            String command) {

        if (executionId == null ||
                executionId.trim().isEmpty()) {

            this.executionId =
                    UUID.randomUUID().toString();

        } else {

            this.executionId =
                    executionId;
        }

        this.command =
                command == null
                        ? ""
                        : command;

        this.startedAt =
                System.currentTimeMillis();

        addEvent("RECEIVED");
    }

    public synchronized void transition(
            ExecutionStatus next) {

        if (next == null) {
            return;
        }

        state = next;

        addEvent(
                next.name());

        if (next ==
                        ExecutionStatus.SUCCESS ||

                next ==
                        ExecutionStatus.FAILED ||

                next ==
                        ExecutionStatus.CANCELLED) {

            completedAt =
                    System.currentTimeMillis();
        }
    }

    public synchronized void addEvent(
            String event) {

        if (event == null ||
                event.trim().isEmpty()) {

            return;
        }

        events.add(
                System.currentTimeMillis() +
                        ": " +
                        event.trim());
    }

    public synchronized void setAttemptCount(
            int value) {

        attemptCount =
                Math.max(
                        0,
                        value);
    }

    public synchronized void setFailure(
            ExecutionFailure value) {

        failure = value;
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

    public synchronized long
    getCompletedAt() {

        return completedAt;
    }

    public synchronized ExecutionStatus
    getState() {

        return state;
    }

    public synchronized int
    getAttemptCount() {

        return attemptCount;
    }

    public synchronized ExecutionFailure
    getFailure() {

        return failure;
    }

    public synchronized long
    getDurationMs() {

        long end =
                completedAt == 0L
                        ? System.currentTimeMillis()
                        : completedAt;

        return Math.max(
                0L,
                end - startedAt);
    }

    public synchronized List<String>
    getEvents() {

        return Collections.unmodifiableList(
                new ArrayList<>(events));
    }

    @Override
    public synchronized String
    toString() {

        return "ExecutionTrace{" +
                "executionId='" +
                executionId +
                '\'' +
                ", command='" +
                command +
                '\'' +
                ", state=" +
                state +
                ", startedAt=" +
                startedAt +
                ", completedAt=" +
                completedAt +
                ", attemptCount=" +
                attemptCount +
                ", events=" +
                events.size() +
                '}';
    }
}
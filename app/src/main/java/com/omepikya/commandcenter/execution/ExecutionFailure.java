package com.omepikya.commandcenter.execution;

/**
 * Structured description of an execution failure.
 *
 * Keeps recovery decisions separate from user-facing text.
 */
public final class ExecutionFailure {

    private final String executionId;

    private final String command;

    private final String reason;

    private final String failedStep;

    private final boolean retryable;

    private final boolean requiresConfirmation;

    private final int attemptCount;

    public ExecutionFailure(
            String executionId,
            String command,
            String reason,
            String failedStep,
            boolean retryable,
            boolean requiresConfirmation,
            int attemptCount) {

        this.executionId =
                executionId == null
                        ? ""
                        : executionId;

        this.command =
                command == null
                        ? ""
                        : command;

        this.reason =
                reason == null
                        ? ""
                        : reason;

        this.failedStep =
                failedStep == null
                        ? ""
                        : failedStep;

        this.retryable =
                retryable;

        this.requiresConfirmation =
                requiresConfirmation;

        this.attemptCount =
                Math.max(
                        0,
                        attemptCount);
    }

    public String getExecutionId() {
        return executionId;
    }

    public String getCommand() {
        return command;
    }

    public String getReason() {
        return reason;
    }

    public String getFailedStep() {
        return failedStep;
    }

    public boolean isRetryable() {
        return retryable;
    }

    public boolean requiresConfirmation() {
        return requiresConfirmation;
    }

    public int getAttemptCount() {
        return attemptCount;
    }

    @Override
    public String toString() {

        return "ExecutionFailure{" +
                "executionId='" +
                executionId +
                '\'' +
                ", command='" +
                command +
                '\'' +
                ", reason='" +
                reason +
                '\'' +
                ", failedStep='" +
                failedStep +
                '\'' +
                ", retryable=" +
                retryable +
                ", requiresConfirmation=" +
                requiresConfirmation +
                ", attemptCount=" +
                attemptCount +
                '}';
    }
}
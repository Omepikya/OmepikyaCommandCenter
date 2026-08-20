package com.omepikya.commandcenter.execution;

import com.omepikya.commandcenter.core.CommandResult;

/**
 * Structured result of a command execution.
 */
public final class ExecutionResult {

    private final String executionId;

    private final String command;

    private final boolean success;

    private final String message;

    private final long timestamp;

    private final int attemptCount;

    private final CommandResult commandResult;

    private final ExecutionStatus status;

    public ExecutionResult(
            String executionId,
            String command,
            boolean success,
            String message,
            long timestamp,
            int attemptCount,
            CommandResult commandResult) {

        this(
                executionId,
                command,
                success,
                message,
                timestamp,
                attemptCount,
                commandResult,
                success
                        ? ExecutionStatus.SUCCESS
                        : ExecutionStatus.FAILED);
    }

    public ExecutionResult(
            String executionId,
            String command,
            boolean success,
            String message,
            long timestamp,
            int attemptCount,
            CommandResult commandResult,
            ExecutionStatus status) {

        this.executionId =
                executionId;

        this.command =
                command;

        this.success =
                success;

        this.message =
                message;

        this.timestamp =
                timestamp;

        this.attemptCount =
                attemptCount;

        this.commandResult =
                commandResult;

        this.status =
                status == null
                        ? (
                        success
                                ? ExecutionStatus.SUCCESS
                                : ExecutionStatus.FAILED)
                        : status;
    }

    public static ExecutionResult success(
            String executionId,
            String command,
            String message,
            int attempts,
            CommandResult result) {

        return new ExecutionResult(
                executionId,
                command,
                true,
                message,
                System.currentTimeMillis(),
                attempts,
                result,
                ExecutionStatus.SUCCESS);
    }

    public static ExecutionResult failure(
            String executionId,
            String command,
            String message,
            int attempts,
            CommandResult result) {

        return new ExecutionResult(
                executionId,
                command,
                false,
                message,
                System.currentTimeMillis(),
                attempts,
                result,
                ExecutionStatus.FAILED);
    }

    public String getExecutionId() {
        return executionId;
    }

    public String getCommand() {
        return command;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public int getAttemptCount() {
        return attemptCount;
    }

    public CommandResult getCommandResult() {
        return commandResult;
    }

    public ExecutionStatus getStatus() {
        return status;
    }
}
package com.omepikya.commandcenter.execution;

/**
 * Immutable event emitted during an execution lifecycle.
 */
public final class ExecutionEvent {

    private final String executionId;
    private final String type;
    private final String message;
    private final long timestamp;

    public ExecutionEvent(
            String executionId,
            String type,
            String message) {

        this.executionId =
                executionId == null
                        ? ""
                        : executionId;

        this.type =
                type == null
                        ? "UNKNOWN"
                        : type;

        this.message =
                message == null
                        ? ""
                        : message;

        this.timestamp =
                System.currentTimeMillis();
    }

    public String getExecutionId() {
        return executionId;
    }

    public String getType() {
        return type;
    }

    public String getMessage() {
        return message;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
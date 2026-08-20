package com.omepikya.commandcenter.execution;

/**
 * Cooperative cancellation token for command execution.
 * Cancellation is checked at lifecycle boundaries and never
 * bypasses safety or cleanup logic.
 */
public final class ExecutionCancellation {

    private volatile boolean cancelled;
    private volatile String reason;

    public synchronized void cancel() {
        cancel("Execution cancelled by user.");
    }

    public synchronized void cancel(String reason) {
        cancelled = true;

        this.reason =
                reason == null ||
                reason.trim().isEmpty()
                        ? "Execution cancelled."
                        : reason.trim();
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public String getReason() {
        return reason == null
                ? "Execution cancelled."
                : reason;
    }

    public void throwIfCancelled() {

        if (cancelled) {
            throw new ExecutionCancelledException(
                    getReason());
        }
    }

    public static final class ExecutionCancelledException
            extends RuntimeException {

        public ExecutionCancelledException(
                String message) {

            super(message);
        }
    }
}
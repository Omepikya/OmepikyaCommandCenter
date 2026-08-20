package com.omepikya.commandcenter.execution;

/**
 * OMEPIKYA COMMAND CENTER
 * Phase 10: UI-facing execution control bridge.
 *
 * Bridges the existing ExecutionCoordinator/EventBus/Monitor
 * to the Command Center UI without creating a second execution engine.
 */
public final class ExecutionControlCenter
        implements ExecutionEventBus.Listener {

    public interface Listener {
        void onExecutionEvent(ExecutionEvent event);
        void onExecutionFinished(ExecutionResult result, ExecutionTrace trace);
    }

    private final ExecutionCoordinator coordinator;
    private final ExecutionMonitor monitor;
    private volatile Listener listener;
    private volatile boolean active;
    private volatile String activeExecutionId = "";

    public ExecutionControlCenter(
            ExecutionCoordinator coordinator) {

        if (coordinator == null) {
            throw new IllegalArgumentException(
                    "ExecutionCoordinator cannot be null");
        }

        this.coordinator = coordinator;
        this.monitor = new ExecutionMonitor();
        coordinator.getEventBus().subscribe(this);
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    @Override
    public void onEvent(ExecutionEvent event) {

        if (event == null) {
            return;
        }

        String type = event.getType();

        if ("RECEIVED".equals(type)) {
            activeExecutionId = event.getExecutionId();
            active = true;
            monitor.start(
                    event.getExecutionId(),
                    event.getMessage(),
                    0);
        }

        monitor.onEvent(event);

        Listener current = listener;
        if (current != null) {
            try {
                current.onExecutionEvent(event);
            } catch (Exception ignored) {
            }
        }
    }

    public void complete(
            ExecutionResult result,
            ExecutionTrace trace) {

        active = false;

        if (result != null) {
            monitor.finish(
                    result.isSuccess(),
                    result.getMessage());
        }

        Listener current = listener;
        if (current != null) {
            try {
                current.onExecutionFinished(
                        result,
                        trace);
            } catch (Exception ignored) {
            }
        }
    }

    public void cancel() {
        coordinator.cancelActive(
                "Execution cancelled by user.");
    }

    public boolean isActive() {
        return active;
    }

    public String getActiveExecutionId() {
        return activeExecutionId;
    }

    public ExecutionMonitor getMonitor() {
        return monitor;
    }

    public ExecutionTrace getLastTrace() {
        return coordinator.getLastTrace();
    }

    public ExecutionResult getLastResult() {
        return coordinator.getLastResult();
    }

    public void destroy() {
        coordinator.getEventBus().unsubscribe(this);
        listener = null;
        active = false;
    }
}

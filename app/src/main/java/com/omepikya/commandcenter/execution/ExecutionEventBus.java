package com.omepikya.commandcenter.execution;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * ============================================================
 * OMEPIKYA COMMAND CENTER
 * EXECUTION EVENT BUS
 * ============================================================
 *
 * Phase 9E
 *
 * Shared execution lifecycle event bus.
 *
 * Observer failures are isolated so that monitoring
 * can never break command execution.
 */
public final class ExecutionEventBus {

    public interface Listener {

        void onEvent(
                ExecutionEvent event);
    }

    private final List<Listener> listeners =
            new ArrayList<>();

    private final List<ExecutionEvent>
            recentEvents =
            new ArrayList<>();

    private int maxRecentEvents = 200;

    public synchronized void subscribe(
            Listener listener) {

        if (listener != null &&
                !listeners.contains(listener)) {

            listeners.add(listener);
        }
    }

    public synchronized void unsubscribe(
            Listener listener) {

        listeners.remove(listener);
    }

    public void publish(
            String executionId,
            String type,
            String message) {

        ExecutionEvent event =
                new ExecutionEvent(
                        executionId,
                        type,
                        message);

        publish(event);
    }

    public void publish(
            ExecutionEvent event) {

        if (event == null) {
            return;
        }

        List<Listener> snapshot;

        synchronized (this) {

            recentEvents.add(event);

            while (recentEvents.size() >
                    maxRecentEvents) {

                recentEvents.remove(0);
            }

            snapshot =
                    new ArrayList<>(
                            listeners);
        }

        for (Listener listener :
                snapshot) {

            try {

                listener.onEvent(event);

            } catch (Exception ignored) {

                /*
                 * Never allow an observer to
                 * interrupt execution.
                 */
            }
        }
    }

    public synchronized List<ExecutionEvent>
    getRecentEvents(
            int count) {

        if (count <= 0 ||
                recentEvents.isEmpty()) {

            return Collections.emptyList();
        }

        int start =
                Math.max(
                        0,
                        recentEvents.size() - count);

        return Collections.unmodifiableList(
                new ArrayList<>(
                        recentEvents.subList(
                                start,
                                recentEvents.size())));
    }

    public synchronized List<ExecutionEvent>
    getEventsForExecution(
            String executionId) {

        if (executionId == null ||
                executionId.trim().isEmpty()) {

            return Collections.emptyList();
        }

        List<ExecutionEvent> result =
                new ArrayList<>();

        for (ExecutionEvent event :
                recentEvents) {

            if (event != null &&
                    executionId.equals(
                            event.getExecutionId())) {

                result.add(event);
            }
        }

        return Collections.unmodifiableList(
                result);
    }

    public synchronized void clear() {

        recentEvents.clear();
    }

    public synchronized int
    getMaxRecentEvents() {

        return maxRecentEvents;
    }

    public synchronized void
    setMaxRecentEvents(
            int value) {

        maxRecentEvents =
                Math.max(
                        20,
                        Math.min(
                                1000,
                                value));

        while (recentEvents.size() >
                maxRecentEvents) {

            recentEvents.remove(0);
        }
    }
}
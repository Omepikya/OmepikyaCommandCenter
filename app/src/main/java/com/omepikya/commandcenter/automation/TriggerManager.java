package com.omepikya.commandcenter.automation;

import java.util.ArrayList;
import java.util.List;

public class TriggerManager {

    public interface TriggerListener {
        void onTaskDue(AutomationTask task);
    }

    private final List<TriggerListener> listeners;

    public TriggerManager() {
        listeners = new ArrayList<>();
    }

    public void addListener(
            TriggerListener listener
    ) {

        if (listener == null) {
            return;
        }

        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    public void removeListener(
            TriggerListener listener
    ) {

        if (listener == null) {
            return;
        }

        listeners.remove(listener);
    }

    public void notifyTaskDue(
            AutomationTask task
    ) {

        if (task == null) {
            return;
        }

        for (TriggerListener listener :
                new ArrayList<>(listeners)) {

            try {
                listener.onTaskDue(task);
            } catch (Exception ignored) {
                // One listener must not break the others.
            }
        }
    }

    public void clearListeners() {
        listeners.clear();
    }

    public int getListenerCount() {
        return listeners.size();
    }
}
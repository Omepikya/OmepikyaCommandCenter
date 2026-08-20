package com.omepikya.commandcenter.router;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ActionRegistry {

    private final List<Action> actions;

    public ActionRegistry() {
        actions = new ArrayList<>();
    }

    public void register(Action action) {
        if (action == null) {
            return;
        }

        if (!actions.contains(action)) {
            actions.add(action);
        }
    }

    public void unregister(Action action) {
        if (action == null) {
            return;
        }

        actions.remove(action);
    }

    public List<Action> getActions() {
        return Collections.unmodifiableList(actions);
    }

    public void clear() {
        actions.clear();
    }

    public int size() {
        return actions.size();
    }
}
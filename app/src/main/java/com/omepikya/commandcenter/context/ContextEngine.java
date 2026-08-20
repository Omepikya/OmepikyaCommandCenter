package com.omepikya.commandcenter.context;

import android.content.Context;

import com.omepikya.commandcenter.core.CommandContext;

import java.util.HashMap;
import java.util.Map;

public class ContextEngine {

    private static final long DEFAULT_TIMEOUT =
            5 * 60 * 1000L;

    private final Context appContext;

    private final ContextState state;

    private final Map<String, Object> values =
            new HashMap<>();

    private long lastUpdated;

    private long timeout =
            DEFAULT_TIMEOUT;

    public ContextEngine(
            Context context
    ) {

        if (context == null) {

            throw new IllegalArgumentException(
                    "Context cannot be null"
            );
        }

        appContext =
                context.getApplicationContext();

        state =
                new ContextState();

        lastUpdated =
                System.currentTimeMillis();
    }

    public synchronized void update(
            String key,
            Object value
    ) {

        if (key == null
                || key.trim().isEmpty()) {

            return;
        }

        if (isExpired()) {

            clearInternal();
        }

        values.put(
                key,
                value
        );

        updateState(
                key,
                value
        );

        touch();
    }

    public synchronized void update(
            CommandContext context
    ) {

        if (context == null) {

            return;
        }

        if (isExpired()) {

            clearInternal();
        }

        if (context.getRawCommand() != null) {

            setLastCommandInternal(
                    context.getRawCommand()
            );
        }

        if (context.getCommandType() != null) {

            setLastIntentInternal(
                    context.getCommandType()
                            .name()
            );
        }

        if (context.getParameters() != null) {

            for (
                    Map.Entry<String, String> entry
                    : context.getParameters()
                    .entrySet()
            ) {

                if (entry == null) {

                    continue;
                }

                putInternal(
                        entry.getKey(),
                        entry.getValue()
                );
            }
        }

        touch();
    }

    public synchronized void put(
            String key,
            Object value
    ) {

        update(
                key,
                value
        );
    }

    @SuppressWarnings("unchecked")
    public synchronized <T> T get(
            String key
    ) {

        if (isExpired()) {

            clearInternal();

            return null;
        }

        return (T)
                values.get(
                        key
                );
    }

    public synchronized boolean contains(
            String key
    ) {

        if (isExpired()) {

            clearInternal();

            return false;
        }

        return values.containsKey(
                key
        );
    }

    public synchronized void remove(
            String key
    ) {

        if (key == null) {

            return;
        }

        values.remove(
                key
        );

        touch();
    }

    public synchronized void clear() {

        clearInternal();

        touch();
    }

    public synchronized void reset() {

        clear();
    }

    private void clearInternal() {

        values.clear();

        state.lastCommand = null;
        state.lastIntent = null;
        state.lastEntity = null;
        state.lastAction = null;
        state.currentScreen = null;
        state.lastResult = null;
        state.lastExecutionSuccessful = false;
        state.status = "IDLE";
    }

    public synchronized boolean isExpired() {

        return System.currentTimeMillis()
                - lastUpdated
                > timeout;
    }

    public synchronized boolean isFresh() {

        return !isExpired();
    }

    public synchronized long getContextAge() {

        return Math.max(
                0L,
                System.currentTimeMillis()
                        - lastUpdated
        );
    }

    public synchronized long getLastUpdated() {

        return lastUpdated;
    }

    public synchronized long getTimeout() {

        return timeout;
    }

    public synchronized void setTimeout(
            long timeoutMillis
    ) {

        if (timeoutMillis > 0) {

            timeout =
                    timeoutMillis;
        }
    }

    public synchronized Map<String, Object>
    snapshot() {

        if (isExpired()) {

            clearInternal();
        }

        return new HashMap<>(
                values
        );
    }

    public synchronized ContextState
    getState() {

        return state;
    }

    public synchronized void setLastCommand(
            String command
    ) {

        if (command == null) {

            return;
        }

        setLastCommandInternal(
                command
        );

        touch();
    }

    private void setLastCommandInternal(
            String command
    ) {

        state.lastCommand =
                command;

        values.put(
                "last_command",
                command
        );
    }

    public synchronized String
    getLastCommand() {

        return state.lastCommand;
    }

    public synchronized void setLastIntent(
            String intent
    ) {

        if (intent == null) {

            return;
        }

        setLastIntentInternal(
                intent
        );

        touch();
    }

    private void setLastIntentInternal(
            String intent
    ) {

        state.lastIntent =
                intent;

        values.put(
                "last_intent",
                intent
        );
    }

    public synchronized String
    getLastIntent() {

        return state.lastIntent;
    }

    public synchronized void setLastEntity(
            String entity
    ) {

        state.lastEntity =
                entity;

        values.put(
                "last_entity",
                entity
        );

        touch();
    }

    public synchronized String
    getLastEntity() {

        return state.lastEntity;
    }

    public synchronized void setLastAction(
            String action
    ) {

        state.lastAction =
                action;

        values.put(
                "last_action",
                action
        );

        touch();
    }

    public synchronized String
    getLastAction() {

        return state.lastAction;
    }

    public synchronized void setCurrentScreen(
            String screen
    ) {

        if (screen == null) {

            return;
        }

        state.currentScreen =
                screen;

        values.put(
                "current_screen",
                screen
        );

        touch();
    }

    public synchronized String
    getCurrentScreen() {

        return state.currentScreen;
    }

    public synchronized void setLastResult(
            boolean success,
            String message
    ) {

        state.lastExecutionSuccessful =
                success;

        state.lastResult =
                message;

        values.put(
                "last_execution_success",
                success
        );

        values.put(
                "last_execution_message",
                message
        );

        values.put(
                "last_execution_time",
                System.currentTimeMillis()
        );

        touch();
    }

    public synchronized boolean
    hasRecentFailure(
            long windowMillis
    ) {

        Object success =
                values.get(
                        "last_execution_success"
                );

        Object time =
                values.get(
                        "last_execution_time"
                );

        if (!(success instanceof Boolean)
                || !(time instanceof Long)) {

            return false;
        }

        if ((Boolean) success) {

            return false;
        }

        return System.currentTimeMillis()
                - (Long) time
                <= windowMillis;
    }

    public synchronized void waiting() {

        state.status =
                "WAITING";

        touch();
    }

    public synchronized void processing() {

        state.status =
                "PROCESSING";

        touch();
    }

    public synchronized void complete() {

        state.status =
                "COMPLETE";

        touch();
    }

    public synchronized void idle() {

        state.status =
                "IDLE";

        touch();
    }

    private void putInternal(
            String key,
            Object value
    ) {

        if (key == null
                || key.trim().isEmpty()) {

            return;
        }

        values.put(
                key,
                value
        );

        updateState(
                key,
                value
        );
    }

    private void updateState(
            String key,
            Object value
    ) {

        if (value == null) {

            return;
        }

        if ("last_command".equals(key)) {

            state.lastCommand =
                    String.valueOf(value);

        } else if ("last_intent".equals(key)) {

            state.lastIntent =
                    String.valueOf(value);

        } else if ("last_entity".equals(key)) {

            state.lastEntity =
                    String.valueOf(value);

        } else if ("last_action".equals(key)) {

            state.lastAction =
                    String.valueOf(value);

        } else if ("current_screen".equals(key)) {

            state.currentScreen =
                    String.valueOf(value);
        }
    }

    private void touch() {

        lastUpdated =
                System.currentTimeMillis();

        state.timestamp =
                lastUpdated;
    }

    public Context getApplicationContext() {

        return appContext;
    }
}
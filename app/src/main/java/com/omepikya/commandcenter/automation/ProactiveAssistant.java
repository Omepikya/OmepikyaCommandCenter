package com.omepikya.commandcenter.automation;

import android.content.Context;

import com.omepikya.commandcenter.context.ContextAnalyzer;
import com.omepikya.commandcenter.context.ContextEngine;
import com.omepikya.commandcenter.intelligence.CommandSuggestion;
import com.omepikya.commandcenter.intelligence.ProactiveSuggestionEngine;
import com.omepikya.commandcenter.memory.MemoryManager;

import java.util.List;

public class ProactiveAssistant {

    public interface Listener {

        void onSuggestion(
                Suggestion suggestion
        );
    }

    public static class Suggestion {

        private final String message;

        private final String command;

        private final double score;

        private final CommandSuggestion.Type type;

        public Suggestion(
                String message
        ) {

            this(
                    message,
                    null,
                    0.5,
                    CommandSuggestion.Type.CONTEXT
            );
        }

        public Suggestion(
                String message,
                String command,
                double score,
                CommandSuggestion.Type type
        ) {

            this.message =
                    message;

            this.command =
                    command;

            this.score =
                    score;

            this.type =
                    type;
        }

        public String getMessage() {

            return message;
        }

        public String getCommand() {

            return command;
        }

        public double getScore() {

            return score;
        }

        public CommandSuggestion.Type
        getType() {

            return type;
        }
    }

    private final Context appContext;

    /*
     * IMPORTANT:
     * This is the SAME ContextEngine owned by
     * CommandBrain.
     */
    private final ContextEngine contextEngine;

    private final ContextAnalyzer contextAnalyzer;

    private final MemoryManager memoryManager;

    private final ProactiveSuggestionEngine
            suggestionEngine;

    private final ProactiveSettings settings;

    private Listener listener;

    private String lastSuggestion;

    private long lastSuggestionTime;

    public ProactiveAssistant(
            Context context,
            ContextEngine contextEngine,
            MemoryManager memoryManager
    ) {

        if (context == null
                || contextEngine == null
                || memoryManager == null) {

            throw new IllegalArgumentException(
                    "ProactiveAssistant dependencies cannot be null"
            );
        }

        appContext =
                context.getApplicationContext();

        this.contextEngine =
                contextEngine;

        this.memoryManager =
                memoryManager;

        this.contextAnalyzer =
                new ContextAnalyzer(
                        contextEngine
                );

        this.suggestionEngine =
                new ProactiveSuggestionEngine(
                        appContext,
                        contextEngine
                );

        this.settings =
                new ProactiveSettings(
                        appContext
                );
    }

    public synchronized void setListener(
            Listener listener
    ) {

        this.listener =
                listener;
    }

    public synchronized void check() {

        Suggestion suggestion =
                getSuggestion();

        if (suggestion == null) {

            return;
        }

        if (listener != null) {

            listener.onSuggestion(
                    suggestion
            );
        }
    }

    public synchronized String
    checkForSuggestion() {

        Suggestion suggestion =
                getSuggestion();

        if (suggestion == null) {

            return null;
        }

        return suggestion.getMessage();
    }

    public synchronized Suggestion
    getSuggestion() {

        if (!settings.isEnabled()) {

            return null;
        }

        long now =
                System.currentTimeMillis();

        long cooldown =
                settings.getCooldown();

        if (now - lastSuggestionTime
                < cooldown) {

            return null;
        }

        List<CommandSuggestion>
                suggestions =
                suggestionEngine.generate();

        if (suggestions == null
                || suggestions.isEmpty()) {

            return null;
        }

        for (
                CommandSuggestion suggestion
                : suggestions
        ) {

            if (suggestion == null) {

                continue;
            }

            if (!isAllowed(
                    suggestion
            )) {

                continue;
            }

            String message =
                    suggestion.getMessage();

            if (message == null
                    || message.trim().isEmpty()) {

                continue;
            }

            lastSuggestion =
                    message;

            lastSuggestionTime =
                    now;

            return new Suggestion(
                    message,
                    suggestion.getCommand(),
                    suggestion.getScore(),
                    suggestion.getType()
            );
        }

        return null;
    }

    private boolean isAllowed(
            CommandSuggestion suggestion
    ) {

        if (!settings.isEnabled()) {

            return false;
        }

        CommandSuggestion.Type type =
                suggestion.getType();

        if (type ==
                CommandSuggestion.Type.BEHAVIOR) {

            return settings
                    .isBehaviorSuggestionsEnabled();
        }

        if (type ==
                CommandSuggestion.Type.CONTEXT) {

            return settings
                    .isContextSuggestionsEnabled();
        }

        if (type ==
                CommandSuggestion.Type.FOLLOW_UP) {

            return settings
                    .isContextSuggestionsEnabled();
        }

        return true;
    }

    public synchronized String
    resolveFollowUp(
            String command
    ) {

        if (!settings.isEnabled()) {

            return command;
        }

        if (!contextAnalyzer
                .isFollowUpCandidate(
                        command
                )) {

            return command;
        }

        return contextAnalyzer
                .resolveFollowUp(
                        command
                );
    }

    public synchronized void
    recordCommand(
            String command,
            String intent,
            String action,
            String entity,
            boolean success,
            String message
    ) {

        if (command != null) {

            contextEngine.setLastCommand(
                    command
            );
        }

        if (intent != null) {

            contextEngine.setLastIntent(
                    intent
            );
        }

        if (action != null) {

            contextEngine.setLastAction(
                    action
            );
        }

        if (entity != null) {

            contextEngine.setLastEntity(
                    entity
            );
        }

        contextEngine.setLastResult(
                success,
                message
        );
    }

    public synchronized void
    setCurrentScreen(
            String screen
    ) {

        contextEngine.setCurrentScreen(
                screen
        );
    }

    public ProactiveSettings
    getSettings() {

        return settings;
    }

    public boolean isEnabled() {

        return settings.isEnabled();
    }

    public void setEnabled(
            boolean enabled
    ) {

        settings.setEnabled(
                enabled
        );
    }

    public boolean
    isAutomationSuggestionsEnabled() {

        return settings
                .isAutomationSuggestionsEnabled();
    }

    public void
    setAutomationSuggestionsEnabled(
            boolean enabled
    ) {

        settings
                .setAutomationSuggestionsEnabled(
                        enabled
                );
    }

    public boolean
    isBehaviorSuggestionsEnabled() {

        return settings
                .isBehaviorSuggestionsEnabled();
    }

    public void
    setBehaviorSuggestionsEnabled(
            boolean enabled
    ) {

        settings
                .setBehaviorSuggestionsEnabled(
                        enabled
                );
    }

    public boolean
    isContextSuggestionsEnabled() {

        return settings
                .isContextSuggestionsEnabled();
    }

    public void
    setContextSuggestionsEnabled(
            boolean enabled
    ) {

        settings
                .setContextSuggestionsEnabled(
                        enabled
                );
    }

    public boolean
    shouldConfirmSensitiveActions() {

        return settings
                .shouldConfirmSensitiveActions();
    }

    public void
    setConfirmSensitiveActions(
            boolean enabled
    ) {

        settings
                .setConfirmSensitiveActions(
                        enabled
                );
    }

    public void setCooldown(
            long milliseconds
    ) {

        settings.setCooldown(
                milliseconds
        );
    }

    public long getCooldown() {

        return settings.getCooldown();
    }

    public synchronized void dismissSuggestion() {

        lastSuggestionTime =
                System.currentTimeMillis();
    }

    public synchronized void clearSuggestionCooldown() {

        lastSuggestionTime = 0L;
    }

    public String
    getLastSuggestion() {

        return lastSuggestion;
    }

    public Context
    getApplicationContext() {

        return appContext;
    }

    public ContextEngine
    getContextEngine() {

        return contextEngine;
    }

    public ContextAnalyzer
    getContextAnalyzer() {

        return contextAnalyzer;
    }

    public MemoryManager
    getMemoryManager() {

        return memoryManager;
    }

    public ProactiveSuggestionEngine
    getSuggestionEngine() {

        return suggestionEngine;
    }
}
package com.omepikya.commandcenter.context;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

/**
 * Phase 7B:
 * bounded, prioritized conversational context.
 */
public final class AdvancedContextManager {

    private static final int MAX_RECENT = 12;

    private final ContextEngine contextEngine;

    private final Deque<String> recentCommands =
            new ArrayDeque<>();

    private long sequence;

    public AdvancedContextManager(
            ContextEngine contextEngine) {

        if (contextEngine == null) {
            throw new IllegalArgumentException(
                    "ContextEngine cannot be null");
        }

        this.contextEngine = contextEngine;
    }

    public synchronized void observeCommand(
            String command) {

        if (command == null ||
                command.trim().isEmpty()) {

            return;
        }

        recentCommands.addLast(
                command.trim());

        while (recentCommands.size() >
                MAX_RECENT) {

            recentCommands.removeFirst();
        }

        sequence++;

        contextEngine.put(
                "context_sequence",
                sequence);

        contextEngine.put(
                "recent_command_count",
                recentCommands.size());
    }

    public synchronized void observeIntent(
            String intent,
            double confidence) {

        if (intent == null) {
            return;
        }

        contextEngine.put(
                "active_intent",
                intent);

        contextEngine.put(
                "active_intent_confidence",
                confidence);
    }

    public synchronized List<String>
    getRecentCommands() {

        return new ArrayList<>(
                recentCommands);
    }

    public synchronized String
    getPreviousCommand() {

        if (recentCommands.size() < 2) {
            return null;
        }

        String last =
                recentCommands.removeLast();

        String previous =
                recentCommands.peekLast();

        recentCommands.addLast(last);

        return previous;
    }

    public synchronized void clear() {

        recentCommands.clear();

        contextEngine.remove(
                "context_sequence");

        contextEngine.remove(
                "recent_command_count");

        contextEngine.remove(
                "active_intent");

        contextEngine.remove(
                "active_intent_confidence");
    }
}
package com.omepikya.commandcenter.context;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class ContextAnalyzer {

    private final ContextEngine contextEngine;

    public ContextAnalyzer(
            ContextEngine contextEngine
    ) {

        if (contextEngine == null) {

            throw new IllegalArgumentException(
                    "ContextEngine cannot be null"
            );
        }

        this.contextEngine =
                contextEngine;
    }

    public synchronized Map<String, Object>
    analyze() {

        Map<String, Object> result =
                new HashMap<>();

        Calendar calendar =
                Calendar.getInstance();

        int hour =
                calendar.get(
                        Calendar.HOUR_OF_DAY
                );

        int day =
                calendar.get(
                        Calendar.DAY_OF_WEEK
                );

        result.put(
                "hour",
                hour
        );

        result.put(
                "day_of_week",
                day
        );

        result.put(
                "is_morning",
                hour >= 5 && hour < 12
        );

        result.put(
                "is_afternoon",
                hour >= 12 && hour < 17
        );

        result.put(
                "is_evening",
                hour >= 17 && hour < 22
        );

        result.put(
                "is_night",
                hour >= 22 || hour < 5
        );

        result.put(
                "context_fresh",
                contextEngine.isFresh()
        );

        result.put(
                "context_age",
                contextEngine.getContextAge()
        );

        result.put(
                "last_command",
                contextEngine.getLastCommand()
        );

        result.put(
                "last_intent",
                contextEngine.getLastIntent()
        );

        result.put(
                "last_entity",
                contextEngine.getLastEntity()
        );

        result.put(
                "last_action",
                contextEngine.getLastAction()
        );

        result.put(
                "current_screen",
                contextEngine.getCurrentScreen()
        );

        return result;
    }

    public synchronized boolean
    hasLastCommand() {

        String command =
                contextEngine.getLastCommand();

        return command != null
                && !command.trim().isEmpty();
    }

    public synchronized boolean
    isFollowUpCandidate(
            String command
    ) {

        if (command == null
                || command.trim().isEmpty()) {

            return false;
        }

        String text =
                command.trim()
                        .toLowerCase();

        return text.equals("that")
                || text.equals("this")
                || text.equals("it")
                || text.equals("again")
                || text.equals("repeat that")
                || text.equals("repeat it")
                || text.equals("do that again")
                || text.startsWith("now ")
                || text.startsWith("then ")
                || text.startsWith("also ");
    }

    public synchronized String
    resolveFollowUp(
            String command
    ) {

        if (command == null) {

            return null;
        }

        String previous =
                contextEngine.getLastCommand();

        if (previous == null
                || previous.trim().isEmpty()) {

            return command;
        }

        String lower =
                command.trim()
                        .toLowerCase();

        if (lower.equals("again")
                || lower.equals("repeat that")
                || lower.equals("repeat it")
                || lower.equals("do that again")) {

            return previous;
        }

        /*
         * Ambiguous phrases are intentionally left
         * untouched instead of guessing.
         */
        return command;
    }

    public ContextEngine
    getContextEngine() {

        return contextEngine;
    }
}
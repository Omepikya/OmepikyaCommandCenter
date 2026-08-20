package com.omepikya.commandcenter.analytics;

import com.omepikya.commandcenter.core.CommandResult;

public class AnalyticsManager {

    private final EventLogger eventLogger;

    public AnalyticsManager() {
        eventLogger = new EventLogger();
    }

    public void logCommand(
            String command
    ) {

        if (command == null
                || command.trim().isEmpty()) {
            return;
        }

        eventLogger.log(
                "COMMAND_RECEIVED",
                command.trim()
        );
    }

    public void logCommandResult(
            String command,
            CommandResult result
    ) {

        if (result == null) {
            return;
        }

        String data =
                "command=" + command
                        + ", success=" + result.isSuccess()
                        + ", message=" + result.getMessage();

        eventLogger.log(
                "COMMAND_RESULT",
                data
        );
    }

    public void logAction(
            String actionName
    ) {

        eventLogger.log(
                "ACTION_EXECUTED",
                actionName
        );
    }

    public void logError(
            String message
    ) {

        eventLogger.log(
                "ERROR",
                message
        );
    }

    public EventLogger getEventLogger() {
        return eventLogger;
    }

    public int getEventCount() {
        return eventLogger.size();
    }

    public void clearEvents() {
        eventLogger.clear();
    }
}
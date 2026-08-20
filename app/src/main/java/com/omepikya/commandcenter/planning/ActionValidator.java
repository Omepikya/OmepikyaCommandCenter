package com.omepikya.commandcenter.planning;

import com.omepikya.commandcenter.core.CommandContext;
import com.omepikya.commandcenter.core.CommandType;

/**
 * Validates commands before they reach the execution layer.
 *
 * Phase 4:
 * - Rejects invalid contexts.
 * - Rejects unknown commands.
 * - Checks command-specific required parameters.
 * - Identifies commands that should require confirmation.
 */
public class ActionValidator {

    public String validate(CommandContext context) {

        if (context == null) {
            return "Command context is missing.";
        }

        String rawCommand = context.getRawCommand();

        if (rawCommand == null
                || rawCommand.trim().isEmpty()) {
            return "Command is empty.";
        }

        CommandType type =
                context.getCommandType();

        if (type == null
                || type == CommandType.UNKNOWN) {
            return "I don't understand that command yet.";
        }

        switch (type) {

            case OPEN_APP:
                return validateAppCommand(
                        context,
                        "Please specify which app to open."
                );

            case CLOSE_APP:
                return validateAppCommand(
                        context,
                        "Please specify which app to close."
                );

            case COMMUNICATION:
                return validateCommunication(
                        context
                );

            case SYSTEM_SETTING:
                return validateSetting(
                        context
                );

            case NAVIGATION:
                return validateNavigation(
                        context
                );

            case WEB_SEARCH:
                return validateSearch(
                        context
                );

            case AUTOMATION:
                return validateAutomation(
                        context
                );

            case MEDIA:
            case DEVICE_ACTION:
            case INFORMATION:
            case CUSTOM:
                return null;

            default:
                return null;
        }
    }

    private String validateAppCommand(
            CommandContext context,
            String error
    ) {

        String app =
                firstParameter(
                        context,
                        "app",
                        "package",
                        "application"
                );

        if (isEmpty(app)) {
            return error;
        }

        return null;
    }

    private String validateCommunication(
            CommandContext context
    ) {

        String person =
                firstParameter(
                        context,
                        "person",
                        "contact",
                        "target",
                        "phone"
                );

        if (isEmpty(person)) {

            /*
             * Some communication actions parse the raw
             * command themselves, so don't reject a
             * non-empty communication command here.
             */
            String raw =
                    context.getRawCommand();

            if (raw == null
                    || raw.trim().length() < 4) {

                return "Please specify a contact or phone number.";
            }
        }

        return null;
    }

    private String validateSetting(
            CommandContext context
    ) {

        String setting =
                firstParameter(
                        context,
                        "setting",
                        "action"
                );

        if (isEmpty(setting)) {

            /*
             * Existing SystemSettingsAction can parse
             * some commands directly from raw text.
             */
            if (context.getRawCommand() == null
                    || context.getRawCommand()
                    .trim()
                    .isEmpty()) {

                return "Please specify the system setting.";
            }
        }

        return null;
    }

    private String validateNavigation(
            CommandContext context
    ) {

        String destination =
                firstParameter(
                        context,
                        "destination",
                        "place",
                        "location",
                        "query"
                );

        if (isEmpty(destination)
                && isEmpty(context.getRawCommand())) {

            return "Please specify a destination.";
        }

        return null;
    }

    private String validateSearch(
            CommandContext context
    ) {

        String query =
                firstParameter(
                        context,
                        "query",
                        "search"
                );

        if (isEmpty(query)
                && isEmpty(context.getRawCommand())) {

            return "Please specify what you want me to search for.";
        }

        return null;
    }

    private String validateAutomation(
            CommandContext context
    ) {

        String command =
                firstParameter(
                        context,
                        "command",
                        "task"
                );

        if (isEmpty(command)
                && isEmpty(context.getRawCommand())) {

            return "Please specify the automation command.";
        }

        return null;
    }

    /**
     * Returns true when execution should normally
     * require confirmation.
     */
    public boolean requiresConfirmation(
            CommandType type
    ) {

        if (type == null) {
            return true;
        }

        switch (type) {

            case COMMUNICATION:
                return true;

            case CUSTOM:
                return true;

            default:
                return false;
        }
    }

    /**
     * High-impact commands can be treated separately
     * by the execution layer.
     */
    public boolean isHighRisk(
            CommandType type
    ) {

        if (type == null) {
            return true;
        }

        switch (type) {

            case COMMUNICATION:
                return true;

            case CUSTOM:
                return true;

            default:
                return false;
        }
    }

    private String firstParameter(
            CommandContext context,
            String... keys
    ) {

        if (context == null || keys == null) {
            return null;
        }

        for (String key : keys) {

            if (key == null) {
                continue;
            }

            String value =
                    context.getParameter(key);

            if (!isEmpty(value)) {
                return value.trim();
            }
        }

        return null;
    }

    private boolean isEmpty(String value) {

        return value == null
                || value.trim().isEmpty();
    }
}
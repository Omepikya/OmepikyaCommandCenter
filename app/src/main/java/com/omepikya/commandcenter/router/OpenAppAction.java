package com.omepikya.commandcenter.router;

import android.content.Context;

import com.omepikya.commandcenter.bridge.SystemBridge;
import com.omepikya.commandcenter.core.CommandContext;
import com.omepikya.commandcenter.core.CommandResult;
import com.omepikya.commandcenter.core.CommandType;

public class OpenAppAction implements Action {

    private final SystemBridge systemBridge;

    public OpenAppAction(Context context) {

        if (context == null) {
            throw new IllegalArgumentException(
                    "Context cannot be null"
            );
        }

        systemBridge =
                new SystemBridge(context);
    }

    @Override
    public String getName() {
        return "Open App";
    }

    @Override
    public boolean canHandle(
            CommandContext context
    ) {

        if (context == null) {
            return false;
        }

        return context.getCommandType()
                == CommandType.OPEN_APP;
    }

    @Override
    public CommandResult execute(
            CommandContext context
    ) {

        if (context == null) {

            return CommandResult.failure(
                    "Command context is null"
            );
        }

        /*
         * EntityExtractor stores the application
         * name using the "app" parameter.
         */
        String appName =
                context.getParameter("app");

        /*
         * Fallback: if NLP did not extract the
         * application name, try the raw command.
         */
        if (appName == null
                || appName.trim().isEmpty()) {

            appName =
                    extractAppName(
                            context.getRawCommand()
                    );
        }

        if (appName == null
                || appName.trim().isEmpty()) {

            return CommandResult.failure(
                    "No application was specified"
            );
        }

        appName = appName.trim();

        try {
            String packageName =
                    systemBridge.findAppPackage(
                            appName
                    );

            if (packageName == null) {

                return CommandResult.failure(
                        "I couldn't find an installed app named "
                                + appName
                );
            }

            boolean opened =
                    systemBridge.openApp(
                            packageName
                    );

            if (opened) {

                return CommandResult.success(
                        "Opening " + appName
                );
            }

            return CommandResult.failure(
                    "I found " + appName
                            + ", but Android could not launch it"
            );

        } catch (Exception e) {

            return CommandResult.failure(
                    "Could not open " + appName
                            + " because Android rejected the launch request"
            );
        }
    }

    private String extractAppName(
            String command
    ) {

        if (command == null) {
            return "";
        }

        String normalized =
                command.trim();

        if (normalized.isEmpty()) {
            return "";
        }

        String lower =
                normalized.toLowerCase();

        String[] prefixes = {
                "open ",
                "launch ",
                "start "
        };

        for (String prefix : prefixes) {

            if (lower.startsWith(prefix)) {

                return normalized
                        .substring(prefix.length())
                        .trim();
            }
        }

        return "";
    }
}
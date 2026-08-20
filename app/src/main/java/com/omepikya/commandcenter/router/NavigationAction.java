package com.omepikya.commandcenter.router;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.omepikya.commandcenter.core.CommandContext;
import com.omepikya.commandcenter.core.CommandResult;
import com.omepikya.commandcenter.core.CommandType;

public class NavigationAction implements Action {

    private final Context context;

    public NavigationAction(Context context) {

        if (context == null) {
            throw new IllegalArgumentException(
                    "Context cannot be null"
            );
        }

        this.context =
                context.getApplicationContext();
    }

    @Override
    public String getName() {
        return "Navigation";
    }

    @Override
    public boolean canHandle(
            CommandContext commandContext
    ) {

        return commandContext != null
                && commandContext.getCommandType()
                == CommandType.NAVIGATION;
    }

    @Override
    public CommandResult execute(
            CommandContext commandContext
    ) {

        String command =
                commandContext.getRawCommand();

        if (command == null
                || command.trim().isEmpty()) {

            return CommandResult.failure(
                    "No destination was specified."
            );
        }

        String destination =
                extractDestination(command);

        if (destination.isEmpty()) {

            return CommandResult.failure(
                    "Please specify a destination."
            );
        }

        try {

            Uri uri =
                    Uri.parse(
                            "google.navigation:q="
                                    + Uri.encode(destination)
                    );

            Intent intent =
                    new Intent(
                            Intent.ACTION_VIEW,
                            uri
                    );

            intent.setPackage(
                    "com.google.android.apps.maps"
            );

            intent.addFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK
            );

            try {

                this.context.startActivity(intent);

            } catch (Exception mapsUnavailable) {

                Intent fallback =
                        new Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse(
                                        "geo:0,0?q="
                                                + Uri.encode(destination)
                                )
                        );

                fallback.addFlags(
                        Intent.FLAG_ACTIVITY_NEW_TASK
                );

                this.context.startActivity(
                        fallback
                );
            }

            return CommandResult.success(
                    "Opening navigation to "
                            + destination
            );

        } catch (Exception e) {

            return CommandResult.failure(
                    "I couldn't open navigation."
            );
        }
    }

    private String extractDestination(
            String command
    ) {

        String result =
                command.trim();

        String[] prefixes = {
                "navigate to ",
                "navigation to ",
                "directions to ",
                "direction to ",
                "take me to ",
                "show me the way to ",
                "map "
        };

        String lower =
                result.toLowerCase();

        for (String prefix : prefixes) {

            if (lower.startsWith(prefix)) {

                return result
                        .substring(prefix.length())
                        .trim();
            }
        }

        return result;
    }
}
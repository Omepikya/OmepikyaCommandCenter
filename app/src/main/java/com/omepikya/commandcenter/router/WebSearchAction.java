package com.omepikya.commandcenter.router;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.omepikya.commandcenter.core.CommandContext;
import com.omepikya.commandcenter.core.CommandResult;
import com.omepikya.commandcenter.core.CommandType;

public class WebSearchAction implements Action {

    private final Context context;

    public WebSearchAction(Context context) {

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
        return "Web Search";
    }

    @Override
    public boolean canHandle(
            CommandContext commandContext
    ) {

        return commandContext != null
                && commandContext.getCommandType()
                == CommandType.WEB_SEARCH;
    }

    @Override
    public CommandResult execute(
            CommandContext commandContext
    ) {

        String query =
                extractQuery(
                        commandContext.getRawCommand()
                );

        if (query.isEmpty()) {

            return CommandResult.failure(
                    "Please specify what you want to search for."
            );
        }

        try {

            Uri uri =
                    Uri.parse(
                            "https://www.google.com/search?q="
                                    + Uri.encode(query)
                    );

            Intent intent =
                    new Intent(
                            Intent.ACTION_VIEW,
                            uri
                    );

            intent.addFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK
            );

            this.context.startActivity(
                    intent
            );

            return CommandResult.success(
                    "Searching the web for "
                            + query
            );

        } catch (Exception e) {

            return CommandResult.failure(
                    "I couldn't open a web browser."
            );
        }
    }

    private String extractQuery(
            String command
    ) {

        String result =
                command == null
                        ? ""
                        : command.trim();

        String lower =
                result.toLowerCase();

        String[] prefixes = {
                "search the web for ",
                "search web for ",
                "search for ",
                "google "
        };

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
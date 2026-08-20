package com.omepikya.commandcenter.plugins.builtin;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.omepikya.commandcenter.core.CommandContext;
import com.omepikya.commandcenter.core.CommandResult;
import com.omepikya.commandcenter.plugins.api.DeveloperPlugin;
import com.omepikya.commandcenter.plugins.api.PluginCapability;
import com.omepikya.commandcenter.plugins.api.PluginIntent;
import com.omepikya.commandcenter.plugins.api.PluginPermission;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public abstract class UriPlugin
        implements DeveloperPlugin {

    private boolean enabled = true;

    private final Context context;

    protected UriPlugin(
            Context c
    ) {

        if (c == null) {
            throw new IllegalArgumentException(
                    "Context cannot be null"
            );
        }

        context =
                c.getApplicationContext();
    }

    protected abstract String[] keywords();

    protected abstract String uri();

    protected String uriForCommand(
            CommandContext c
    ) {

        return uri();
    }

    protected Context getContext() {
        return context;
    }

    protected String extractQuery(
            CommandContext c
    ) {

        if (c == null
                || c.getRawCommand() == null) {

            return "";
        }

        String command =
                c.getRawCommand().trim();

        String lower =
                command.toLowerCase();

        String[] prefixes = {

                "search youtube for ",
                "search spotify for ",
                "search google for ",
                "search the web for ",
                "search maps for ",
                "search for ",
                "find ",
                "look up ",
                "navigate to ",
                "directions to ",
                "map "
        };

        for (String prefix : prefixes) {

            if (lower.startsWith(prefix)) {

                return command
                        .substring(
                                prefix.length()
                        )
                        .trim();
            }
        }

        return "";
    }

    @Override
    public boolean canHandle(
            CommandContext c
    ) {

        if (!enabled
                || c == null
                || c.getRawCommand() == null) {

            return false;
        }

        String command =
                c.getRawCommand().toLowerCase();

        for (String keyword : keywords()) {

            if (keyword != null
                    && command.contains(
                            keyword.toLowerCase()
                    )) {

                return true;
            }
        }

        return false;
    }

    @Override
    public CommandResult execute(
            CommandContext c
    ) {

        try {

            Intent intent =
                    new Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse(
                                    uriForCommand(c)
                            )
                    );

            intent.addFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK
            );

            context.startActivity(
                    intent
            );

            return CommandResult.success(
                    "Opened "
                            + getName()
                            + "."
            );

        } catch (Exception e) {

            return CommandResult.failure(
                    "Unable to open "
                            + getName()
                            + "."
            );
        }
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void setEnabled(
            boolean enabled
    ) {

        this.enabled = enabled;
    }

    @Override
    public String getVersion() {
        return "1.0";
    }

    @Override
    public List<PluginCapability>
    getCapabilities() {

        return Arrays.asList(
                PluginCapability.COMMAND,
                PluginCapability.ACTION,
                PluginCapability.SEARCH
        );
    }

    @Override
    public List<PluginPermission>
    getRequiredPermissions() {

        return Collections.singletonList(
                PluginPermission.INTERNET
        );
    }

    @Override
    public List<PluginIntent>
    getIntents() {

        return Collections.singletonList(
                new PluginIntent(
                        getId(),
                        "Open and interact with "
                                + getName()
                )
        );
    }
}
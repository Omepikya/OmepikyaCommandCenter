package com.omepikya.commandcenter.plugins.api;

import android.content.Context;

import com.omepikya.commandcenter.core.CommandContext;

public final class PluginContext {

    private final Context androidContext;
    private final CommandContext commandContext;

    public PluginContext(
            Context androidContext,
            CommandContext commandContext
    ) {

        if (androidContext == null) {
            throw new IllegalArgumentException(
                    "Android context cannot be null"
            );
        }

        this.androidContext =
                androidContext
                        .getApplicationContext();

        this.commandContext =
                commandContext;
    }

    public Context getAndroidContext() {
        return androidContext;
    }

    public CommandContext
    getCommandContext() {

        return commandContext;
    }
}
package com.omepikya.commandcenter.plugins;

import com.omepikya.commandcenter.core.CommandContext;
import com.omepikya.commandcenter.core.CommandResult;

public interface Plugin {

    String getId();

    String getName();

    String getVersion();

    boolean isEnabled();

    void setEnabled(boolean enabled);

    boolean canHandle(CommandContext context);

    CommandResult execute(CommandContext context);
}
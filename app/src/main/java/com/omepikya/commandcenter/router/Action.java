package com.omepikya.commandcenter.router;

import com.omepikya.commandcenter.core.CommandContext;
import com.omepikya.commandcenter.core.CommandResult;

public interface Action {

    String getName();

    boolean canHandle(CommandContext context);

    CommandResult execute(CommandContext context);
}
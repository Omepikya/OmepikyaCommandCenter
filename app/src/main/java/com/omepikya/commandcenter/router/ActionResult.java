package com.omepikya.commandcenter.router;

import com.omepikya.commandcenter.core.CommandResult;

public class ActionResult {

    private final boolean handled;
    private final CommandResult result;

    private ActionResult(boolean handled, CommandResult result) {
        this.handled = handled;
        this.result = result;
    }

    public static ActionResult handled(CommandResult result) {
        return new ActionResult(true, result);
    }

    public static ActionResult notHandled() {
        return new ActionResult(false, null);
    }

    public boolean isHandled() {
        return handled;
    }

    public CommandResult getResult() {
        return result;
    }
}
package com.omepikya.commandcenter.models;

import com.omepikya.commandcenter.core.CommandType;

public class UserCommand {

    private final String rawText;
    private final CommandType commandType;

    public UserCommand(String rawText, CommandType commandType) {
        this.rawText = rawText;
        this.commandType = commandType;
    }

    public String getRawText() {
        return rawText;
    }

    public CommandType getCommandType() {
        return commandType;
    }

    public boolean isValid() {
        return rawText != null
                && !rawText.trim().isEmpty()
                && commandType != null;
    }
}
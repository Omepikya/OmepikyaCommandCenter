package com.omepikya.commandcenter.core;

import java.util.HashMap;
import java.util.Map;

public class CommandContext {

    private final String rawCommand;
    private final CommandType commandType;
    private final Map<String, String> parameters;

    public CommandContext(String rawCommand, CommandType commandType) {
        this.rawCommand = rawCommand;
        this.commandType = commandType;
        this.parameters = new HashMap<>();
    }

    public String getRawCommand() {
        return rawCommand;
    }

    public CommandType getCommandType() {
        return commandType;
    }

    public void setParameter(String key, String value) {
        if (key == null || key.trim().isEmpty()) {
            return;
        }

        parameters.put(key, value);
    }

    public String getParameter(String key) {
        return parameters.get(key);
    }

    public boolean hasParameter(String key) {
        return parameters.containsKey(key);
    }

    public Map<String, String> getParameters() {
        return new HashMap<>(parameters);
    }
}
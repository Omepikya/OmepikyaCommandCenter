package com.omepikya.commandcenter.plugins.api;

public final class PluginIntent {

    private final String name;
    private final String description;

    public PluginIntent(
            String name,
            String description) {

        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}
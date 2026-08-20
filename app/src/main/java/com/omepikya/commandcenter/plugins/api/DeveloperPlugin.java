package com.omepikya.commandcenter.plugins.api;

import com.omepikya.commandcenter.plugins.Plugin;

import java.util.Collections;
import java.util.List;

public interface DeveloperPlugin
        extends Plugin {

    default List<PluginCapability>
    getCapabilities() {

        return Collections.singletonList(
                PluginCapability.COMMAND
        );
    }

    default List<PluginPermission>
    getRequiredPermissions() {

        return Collections.singletonList(
                PluginPermission.NONE
        );
    }

    default List<PluginIntent>
    getIntents() {

        return Collections.emptyList();
    }

    default boolean isCompatible(
            String apiVersion
    ) {

        return apiVersion != null
                && apiVersion.startsWith("1.");
    }

    default void onInitialize(
            PluginContext context
    ) {
    }

    default void onShutdown() {
    }

    default void onEvent(
            String event,
            PluginContext context
    ) {
    }
}
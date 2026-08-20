package com.omepikya.commandcenter.plugins;

import com.omepikya.commandcenter.plugins.api.DeveloperPlugin;
import com.omepikya.commandcenter.plugins.api.PluginCapability;
import com.omepikya.commandcenter.plugins.api.PluginPermission;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class PluginDescriptor {

    private final String id;
    private final String name;
    private final String version;

    private final DeveloperPlugin plugin;

    private final List<PluginCapability>
            capabilities;

    private final List<PluginPermission>
            permissions;

    public PluginDescriptor(
            DeveloperPlugin plugin
    ) {

        if (plugin == null) {
            throw new IllegalArgumentException(
                    "Plugin cannot be null"
            );
        }

        this.plugin = plugin;

        id = plugin.getId();
        name = plugin.getName();
        version = plugin.getVersion();

        capabilities =
                new ArrayList<>(
                        plugin.getCapabilities()
                );

        permissions =
                new ArrayList<>(
                        plugin.getRequiredPermissions()
                );
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }

    public DeveloperPlugin getPlugin() {
        return plugin;
    }

    public List<PluginCapability>
    getCapabilities() {

        return Collections.unmodifiableList(
                capabilities
        );
    }

    public List<PluginPermission>
    getPermissions() {

        return Collections.unmodifiableList(
                permissions
        );
    }
}
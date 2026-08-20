package com.omepikya.commandcenter.plugins;

import com.omepikya.commandcenter.plugins.api.DeveloperPlugin;
import com.omepikya.commandcenter.plugins.api.OmepikyaPluginApi;

public final class PluginInstaller {

    private final PluginManager manager;

    public PluginInstaller(
            PluginManager manager
    ) {

        if (manager == null) {
            throw new IllegalArgumentException(
                    "PluginManager cannot be null"
            );
        }

        this.manager = manager;
    }

    public boolean install(
            DeveloperPlugin plugin
    ) {

        if (plugin == null
                || !plugin.isCompatible(
                        OmepikyaPluginApi.VERSION
                )) {

            return false;
        }

        return manager.registerPlugin(
                plugin
        );
    }

    public boolean uninstall(
            String pluginId
    ) {

        return manager.removePlugin(
                pluginId
        );
    }

    public boolean replace(
            DeveloperPlugin plugin
    ) {

        if (plugin == null
                || plugin.getId() == null) {

            return false;
        }

        manager.removePlugin(
                plugin.getId()
        );

        return install(plugin);
    }
}
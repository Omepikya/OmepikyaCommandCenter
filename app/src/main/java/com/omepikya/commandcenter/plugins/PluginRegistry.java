package com.omepikya.commandcenter.plugins;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PluginRegistry {

    private final List<Plugin> plugins;

    public PluginRegistry() {
        plugins = new ArrayList<>();
    }

    public synchronized boolean register(
            Plugin plugin
    ) {

        if (plugin == null) {
            return false;
        }

        if (plugin.getId() == null
                || plugin.getId().trim().isEmpty()) {
            return false;
        }

        if (getPlugin(plugin.getId()) != null) {
            return false;
        }

        plugins.add(plugin);
        return true;
    }

    public synchronized boolean unregister(
            String pluginId
    ) {

        if (pluginId == null
                || pluginId.trim().isEmpty()) {

            return false;
        }

        return plugins.removeIf(
                plugin -> pluginId.equals(
                        plugin.getId()
                )
        );
    }

    public synchronized Plugin getPlugin(
            String pluginId
    ) {

        if (pluginId == null) {
            return null;
        }

        for (Plugin plugin : plugins) {

            if (pluginId.equals(
                    plugin.getId()
            )) {

                return plugin;
            }
        }

        return null;
    }

    public synchronized List<Plugin>
    getPlugins() {

        return Collections.unmodifiableList(
                new ArrayList<>(plugins)
        );
    }

    public synchronized List<Plugin>
    getEnabledPlugins() {

        List<Plugin> enabled =
                new ArrayList<>();

        for (Plugin plugin : plugins) {

            if (plugin.isEnabled()) {
                enabled.add(plugin);
            }
        }

        return Collections.unmodifiableList(
                enabled
        );
    }

    public synchronized void clear() {
        plugins.clear();
    }

    public synchronized int size() {
        return plugins.size();
    }
}
package com.omepikya.commandcenter.plugins;

import com.omepikya.commandcenter.plugins.api.DeveloperPlugin;
import com.omepikya.commandcenter.plugins.api.PluginCapability;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class PluginCapabilityRegistry {

    private final List<DeveloperPlugin>
            plugins =
            new ArrayList<>();

    public synchronized void rebuild(
            List<Plugin> source
    ) {

        plugins.clear();

        if (source == null) {
            return;
        }

        for (Plugin plugin : source) {

            if (plugin instanceof DeveloperPlugin) {

                plugins.add(
                        (DeveloperPlugin) plugin
                );
            }
        }
    }

    public synchronized List<DeveloperPlugin>
    findByCapability(
            PluginCapability capability
    ) {

        List<DeveloperPlugin> result =
                new ArrayList<>();

        if (capability == null) {
            return result;
        }

        for (
                DeveloperPlugin plugin :
                plugins
        ) {

            if (plugin
                    .getCapabilities()
                    .contains(capability)) {

                result.add(plugin);
            }
        }

        return Collections.unmodifiableList(
                result
        );
    }

    public synchronized List<DeveloperPlugin>
    findByCapability(
            String capability
    ) {

        if (capability == null) {
            return Collections.emptyList();
        }

        try {

            return findByCapability(
                    PluginCapability.valueOf(
                            capability
                                    .trim()
                                    .toUpperCase()
                    )
            );

        } catch (IllegalArgumentException e) {

            return Collections.emptyList();
        }
    }

    public synchronized List<DeveloperPlugin>
    getPlugins() {

        return Collections.unmodifiableList(
                new ArrayList<>(plugins)
        );
    }
}
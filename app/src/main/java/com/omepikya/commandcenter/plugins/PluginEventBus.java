package com.omepikya.commandcenter.plugins;

import com.omepikya.commandcenter.plugins.api.DeveloperPlugin;
import com.omepikya.commandcenter.plugins.api.PluginContext;

public final class PluginEventBus {

    private final PluginManager manager;

    public PluginEventBus(
            PluginManager manager
    ) {

        this.manager = manager;
    }

    public void publish(
            String event,
            PluginContext context
    ) {

        if (event == null
                || manager == null) {

            return;
        }

        for (
                Plugin plugin :
                manager
                        .getRegistry()
                        .getEnabledPlugins()
        ) {

            if (!(plugin instanceof DeveloperPlugin)) {
                continue;
            }

            try {

                ((DeveloperPlugin) plugin)
                        .onEvent(
                                event,
                                context
                        );

            } catch (Exception e) {

                manager
                        .getDiagnostics()
                        .record(
                                plugin.getId(),
                                "event:" + event,
                                false,
                                0,
                                e.getMessage()
                        );
            }
        }
    }
}
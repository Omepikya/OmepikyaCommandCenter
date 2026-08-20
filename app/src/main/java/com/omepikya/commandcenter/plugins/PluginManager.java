package com.omepikya.commandcenter.plugins;

import android.content.Context;

import com.omepikya.commandcenter.core.CommandContext;
import com.omepikya.commandcenter.core.CommandResult;
import com.omepikya.commandcenter.plugins.api.DeveloperPlugin;
import com.omepikya.commandcenter.plugins.api.OmepikyaPluginApi;
import com.omepikya.commandcenter.plugins.api.PluginContext;
import com.omepikya.commandcenter.plugins.builtin.BrowserPlugin;
import com.omepikya.commandcenter.plugins.builtin.MapsPlugin;
import com.omepikya.commandcenter.plugins.builtin.SpotifyPlugin;
import com.omepikya.commandcenter.plugins.builtin.WhatsAppPlugin;
import com.omepikya.commandcenter.plugins.builtin.YouTubePlugin;

import java.util.List;

public class PluginManager {

    private final Context context;

    private final PluginRegistry registry;

    private final PluginCapabilityRegistry
            capabilityRegistry;

    private final PluginSettingsStore settings;

    private final PluginPermissionGuard
            permissionGuard;

    private final PluginDiagnostics
            diagnostics;

    private final PluginEventBus eventBus;

    public PluginManager(
            Context context
    ) {

        if (context == null) {
            throw new IllegalArgumentException(
                    "Context cannot be null"
            );
        }

        this.context =
                context.getApplicationContext();

        registry =
                new PluginRegistry();

        capabilityRegistry =
                new PluginCapabilityRegistry();

        settings =
                new PluginSettingsStore(
                        this.context
                );

        permissionGuard =
                new PluginPermissionGuard(
                        this.context
                );

        diagnostics =
                new PluginDiagnostics();

        eventBus =
                new PluginEventBus(this);

        registerBuiltIns();
    }

    private void registerBuiltIns() {

        registerPlugin(
                new BrowserPlugin(context)
        );

        registerPlugin(
                new YouTubePlugin(context)
        );

        registerPlugin(
                new SpotifyPlugin(context)
        );

        registerPlugin(
                new MapsPlugin(context)
        );

        registerPlugin(
                new WhatsAppPlugin(context)
        );
    }

    public synchronized boolean
    registerPlugin(
            Plugin plugin
    ) {

        if (!(plugin instanceof DeveloperPlugin)) {
            return false;
        }

        DeveloperPlugin developerPlugin =
                (DeveloperPlugin) plugin;

        if (!developerPlugin.isCompatible(
                OmepikyaPluginApi.VERSION
        )) {

            return false;
        }

        if (plugin.getId() == null
                || plugin.getId().trim().isEmpty()
                || plugin.getName() == null
                || plugin.getVersion() == null) {

            return false;
        }

        if (!registry.register(plugin)) {
            return false;
        }

        boolean enabled =
                settings.hasState(
                        plugin.getId()
                )
                        ? settings.isEnabled(
                                plugin.getId(),
                                true
                        )
                        : true;

        plugin.setEnabled(
                enabled
        );

        settings.setEnabled(
                plugin.getId(),
                enabled
        );

        try {

            developerPlugin.onInitialize(
                    new PluginContext(
                            context,
                            null
                    )
            );

        } catch (Exception e) {

            plugin.setEnabled(false);

            settings.setEnabled(
                    plugin.getId(),
                    false
            );

            diagnostics.record(
                    plugin.getId(),
                    "initialize",
                    false,
                    0,
                    e.getMessage()
            );
        }

        rebuildCapabilities();

        return true;
    }

    public synchronized boolean
    removePlugin(
            String pluginId
    ) {

        Plugin plugin =
                registry.getPlugin(
                        pluginId
                );

        if (plugin == null) {
            return false;
        }

        if (plugin instanceof DeveloperPlugin) {

            try {

                ((DeveloperPlugin) plugin)
                        .onShutdown();

            } catch (Exception e) {

                diagnostics.record(
                        plugin.getId(),
                        "shutdown",
                        false,
                        0,
                        e.getMessage()
                );
            }
        }

        boolean removed =
                registry.unregister(
                        pluginId
                );

        settings.remove(
                pluginId
        );

        rebuildCapabilities();

        return removed;
    }

    public synchronized boolean
    enablePlugin(
            String pluginId
    ) {

        Plugin plugin =
                registry.getPlugin(
                        pluginId
                );

        if (plugin == null) {
            return false;
        }

        if (plugin instanceof DeveloperPlugin
                && !permissionGuard.canExecute(
                        (DeveloperPlugin) plugin
                )) {

            return false;
        }

        plugin.setEnabled(true);

        settings.setEnabled(
                pluginId,
                true
        );

        rebuildCapabilities();

        return true;
    }

    public synchronized boolean
    disablePlugin(
            String pluginId
    ) {

        Plugin plugin =
                registry.getPlugin(
                        pluginId
                );

        if (plugin == null) {
            return false;
        }

        plugin.setEnabled(false);

        settings.setEnabled(
                pluginId,
                false
        );

        rebuildCapabilities();

        return true;
    }

    public CommandResult execute(
            CommandContext context
    ) {

        if (context == null) {

            return CommandResult.failure(
                    "Command context cannot be null"
            );
        }

        for (
                Plugin plugin :
                registry.getEnabledPlugins()
        ) {

            if (!(plugin instanceof DeveloperPlugin)) {
                continue;
            }

            DeveloperPlugin developerPlugin =
                    (DeveloperPlugin) plugin;

            if (!permissionGuard.canExecute(
                    developerPlugin
            )) {

                continue;
            }

            long start =
                    System.currentTimeMillis();

            try {

                if (!developerPlugin.canHandle(
                        context
                )) {

                    continue;
                }

                CommandResult result =
                        developerPlugin.execute(
                                context
                        );

                long duration =
                        System.currentTimeMillis()
                                - start;

                if (result == null) {

                    diagnostics.record(
                            plugin.getId(),
                            context.getRawCommand(),
                            false,
                            duration,
                            "Plugin returned null"
                    );

                    continue;
                }

                diagnostics.record(
                        plugin.getId(),
                        context.getRawCommand(),
                        result.isSuccess(),
                        duration,
                        result.getMessage()
                );

                eventBus.publish(
                        result.isSuccess()
                                ? "PLUGIN_EXECUTION_SUCCESS"
                                : "PLUGIN_EXECUTION_FAILED",
                        new PluginContext(
                                this.context,
                                context
                        )
                );

                if (result.isSuccess()) {
                    return result;
                }

            } catch (Exception e) {

                diagnostics.record(
                        plugin.getId(),
                        context.getRawCommand(),
                        false,
                        System.currentTimeMillis()
                                - start,
                        e.getMessage()
                );
            }
        }

        return CommandResult.failure(
                "No plugin can handle this command"
        );
    }

    private synchronized void
    rebuildCapabilities() {

        capabilityRegistry.rebuild(
                registry.getPlugins()
        );
    }

    public PluginRegistry
    getRegistry() {

        return registry;
    }

    public PluginCapabilityRegistry
    getCapabilityRegistry() {

        return capabilityRegistry;
    }

    public PluginSettingsStore
    getSettings() {

        return settings;
    }

    public PluginPermissionGuard
    getPermissionGuard() {

        return permissionGuard;
    }

    public PluginDiagnostics
    getDiagnostics() {

        return diagnostics;
    }

    public PluginEventBus
    getEventBus() {

        return eventBus;
    }

    public List<Plugin>
    getPlugins() {

        return registry.getPlugins();
    }

    public int getPluginCount() {
        return registry.size();
    }
}
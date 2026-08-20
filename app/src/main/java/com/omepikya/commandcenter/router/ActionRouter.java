package com.omepikya.commandcenter.router;

import com.omepikya.commandcenter.core.CommandContext;
import com.omepikya.commandcenter.core.CommandResult;
import com.omepikya.commandcenter.plugins.PluginManager;

public class ActionRouter {

    private final ActionRegistry registry;

    private final PluginManager pluginManager;

    public ActionRouter(
            ActionRegistry registry
    ) {

        this(
                registry,
                null
        );
    }

    public ActionRouter(
            ActionRegistry registry,
            PluginManager pluginManager
    ) {

        if (registry == null) {

            throw new IllegalArgumentException(
                    "ActionRegistry cannot be null"
            );
        }

        this.registry = registry;
        this.pluginManager = pluginManager;
    }

    public CommandResult route(
            CommandContext context
    ) {

        if (context == null) {

            return CommandResult.failure(
                    "Command context cannot be null"
            );
        }

        /*
         * Core actions have priority.
         */
        for (
                Action action :
                registry.getActions()
        ) {

            if (action == null) {
                continue;
            }

            try {

                if (action.canHandle(
                        context
                )) {

                    CommandResult result =
                            action.execute(
                                    context
                            );

                    if (result == null) {

                        return CommandResult.failure(
                                "Action returned no result: "
                                        + action.getName()
                        );
                    }

                    context.setParameter(
                            "executed_action",
                            action.getName()
                    );

                    return result;
                }

            } catch (Exception e) {

                return CommandResult.failure(
                        "Action failed: "
                                + action.getName()
                );
            }
        }

        /*
         * Plugin actions are the extension layer.
         */
        if (pluginManager != null) {

            CommandResult pluginResult =
                    pluginManager.execute(
                            context
                    );

            if (pluginResult != null
                    && pluginResult.isSuccess()) {

                context.setParameter(
                        "executed_action",
                        "plugin"
                );

                return pluginResult;
            }
        }

        return CommandResult.failure(
                "No action found for command: "
                        + context.getRawCommand()
        );
    }

    public ActionRegistry
    getRegistry() {

        return registry;
    }

    public PluginManager
    getPluginManager() {

        return pluginManager;
    }
}
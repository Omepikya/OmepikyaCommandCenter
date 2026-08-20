package com.omepikya.commandcenter.plugins;

import com.omepikya.commandcenter.plugins.api.OmepikyaPluginApi;

public final class PluginManifest {

    private final String id;
    private final String name;
    private final String version;
    private final String apiVersion;

    public PluginManifest(
            String id,
            String name,
            String version
    ) {

        this(
                id,
                name,
                version,
                OmepikyaPluginApi.VERSION
        );
    }

    public PluginManifest(
            String id,
            String name,
            String version,
            String apiVersion
    ) {

        this.id = id;
        this.name = name;
        this.version = version;
        this.apiVersion = apiVersion;
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

    public String getApiVersion() {
        return apiVersion;
    }
}
package com.omepikya.commandcenter.plugins.api;

public interface PluginTrigger {

    String getId();

    boolean matches(String event);

    void onTrigger(String event);
}
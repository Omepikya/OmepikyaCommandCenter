package com.omepikya.commandcenter.plugins.api;

public interface PluginAction {

    String getId();

    boolean execute(String command);
}
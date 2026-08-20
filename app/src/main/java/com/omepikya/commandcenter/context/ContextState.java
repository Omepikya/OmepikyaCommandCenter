package com.omepikya.commandcenter.context;

public class ContextState {

    public String lastCommand;
    public String lastIntent;
    public String lastEntity;
    public String lastAction;
    public String currentScreen;
    public String lastResult;
    public boolean lastExecutionSuccessful;
    public String status;
    public long timestamp;

    public ContextState() {

        timestamp =
                System.currentTimeMillis();

        status =
                "IDLE";
    }

    public ContextState copy() {

        ContextState copy =
                new ContextState();

        copy.lastCommand =
                lastCommand;

        copy.lastIntent =
                lastIntent;

        copy.lastEntity =
                lastEntity;

        copy.lastAction =
                lastAction;

        copy.currentScreen =
                currentScreen;

        copy.lastResult =
                lastResult;

        copy.lastExecutionSuccessful =
                lastExecutionSuccessful;

        copy.status =
                status;

        copy.timestamp =
                timestamp;

        return copy;
    }
}
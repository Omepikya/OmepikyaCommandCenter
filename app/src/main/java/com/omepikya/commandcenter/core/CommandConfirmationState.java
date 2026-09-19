package com.omepikya.commandcenter.core;

import com.omepikya.commandcenter.nlp.Intent;

/**
 * Encapsulates pending confirmation state for a command.
 *
 * Keeps confirmation bookkeeping out of the main command orchestration
 * while preserving the existing CommandBrain behavior.
 */
final class CommandConfirmationState {

    private String command;
    private Intent intent;
    private boolean safetyConfirmation;

    boolean isPending() {
        return command != null && intent != null;
    }

    void set(String command, Intent intent, boolean safetyConfirmation) {
        this.command = command;
        this.intent = intent;
        this.safetyConfirmation = safetyConfirmation;
    }

    String getCommand() {
        return command;
    }

    Intent getIntent() {
        return intent;
    }

    boolean isSafetyConfirmation() {
        return safetyConfirmation;
    }

    void clear() {
        command = null;
        intent = null;
        safetyConfirmation = false;
    }
}

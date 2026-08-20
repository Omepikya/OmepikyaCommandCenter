package com.omepikya.commandcenter.security;

public final class SafetyGuard {

    private final SafetyPolicy policy =
            new SafetyPolicy();

    public SafetyDecision check(String command) {
        return policy.evaluate(command);
    }

    public boolean canExecute(
            String command,
            boolean confirmed) {

        SafetyDecision d = check(command);

        return d.isAllowed()
                && (!d.requiresConfirmation()
                || confirmed);
    }

    public SafetyPolicy getPolicy() {
        return policy;
    }
}
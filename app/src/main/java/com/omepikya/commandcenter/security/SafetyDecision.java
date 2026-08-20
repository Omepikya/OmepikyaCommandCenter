package com.omepikya.commandcenter.security;

public final class SafetyDecision {

    private final ActionRisk risk;
    private final boolean allowed;
    private final boolean confirmationRequired;
    private final String reason;

    public SafetyDecision(
            ActionRisk risk,
            boolean allowed,
            boolean confirmationRequired,
            String reason) {

        this.risk = risk;
        this.allowed = allowed;
        this.confirmationRequired = confirmationRequired;
        this.reason = reason;
    }

    public ActionRisk getRisk() {
        return risk;
    }

    public boolean isAllowed() {
        return allowed;
    }

    public boolean requiresConfirmation() {
        return confirmationRequired;
    }

    public String getReason() {
        return reason;
    }
}
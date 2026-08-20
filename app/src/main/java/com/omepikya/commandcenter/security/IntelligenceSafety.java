package com.omepikya.commandcenter.security;

import com.omepikya.commandcenter.core.CommandType;

/**
 * Phase 7I:
 * central risk and confirmation decision
 * for intelligent/autonomous execution.
 */
public final class IntelligenceSafety {

    private final SafetyGuard guard =
            new SafetyGuard();

    public Decision evaluate(
            String command,
            CommandType type,
            boolean confirmed) {

        SafetyDecision base =
                guard.check(command);

        if (!base.isAllowed()) {

            return new Decision(
                    false,
                    true,
                    base.getRisk(),
                    base.getReason());
        }

        boolean confirmation =
                base.requiresConfirmation() ||
                type == CommandType.COMMUNICATION ||
                type == CommandType.CUSTOM;

        if (confirmation && !confirmed) {

            return new Decision(
                    false,
                    true,
                    base.getRisk(),
                    base.getReason());
        }

        return new Decision(
                true,
                false,
                base.getRisk(),
                base.getReason());
    }

    public SafetyGuard getGuard() {
        return guard;
    }

    public static final class Decision {

        private final boolean allowed;
        private final boolean confirmationRequired;
        private final ActionRisk risk;
        private final String reason;

        private Decision(
                boolean allowed,
                boolean confirmationRequired,
                ActionRisk risk,
                String reason) {

            this.allowed = allowed;
            this.confirmationRequired =
                    confirmationRequired;
            this.risk = risk;
            this.reason = reason;
        }

        public boolean isAllowed() {
            return allowed;
        }

        public boolean isConfirmationRequired() {
            return confirmationRequired;
        }

        public ActionRisk getRisk() {
            return risk;
        }

        public String getReason() {
            return reason;
        }
    }
}
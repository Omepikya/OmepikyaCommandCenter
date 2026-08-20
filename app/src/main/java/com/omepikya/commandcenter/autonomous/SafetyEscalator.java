package com.omepikya.commandcenter.autonomous;

import com.omepikya.commandcenter.core.CommandResult;

public final class SafetyEscalator {

    public boolean requiresEscalation(
            CommandResult result) {

        if (result == null ||
                result.isSuccess()) {

            return false;
        }

        String message =
                result.getMessage();

        if (message == null) {
            return false;
        }

        String lower =
                message.toLowerCase();

        return lower.contains(
                        "confirmation") ||
                lower.contains(
                        "permission required") ||
                lower.contains(
                        "requires approval") ||
                lower.contains(
                        "safety");
    }

    public CommandResult stop(
            String reason) {

        String safe =
                reason == null ||
                        reason.trim().isEmpty()
                        ? "Autonomous execution stopped for safety."
                        : reason;

        return CommandResult.failure(
                safe);
    }
}
package com.omepikya.commandcenter.execution;

import com.omepikya.commandcenter.core.CommandContext;
import com.omepikya.commandcenter.core.CommandResult;
import com.omepikya.commandcenter.security.ActionRisk;
import com.omepikya.commandcenter.security.SafetyDecision;
import com.omepikya.commandcenter.security.SafetyGuard;

/**
 * ============================================================
 * OMEPIKYA COMMAND CENTER
 * RECOVERY ENGINE
 * ============================================================
 *
 * Phase 9C
 *
 * Bounded, safety-aware recovery.
 *
 * Recovery never bypasses:
 *
 * CommandBrain
 * -> ActionExecutor
 * -> Validator
 * -> ActionRouter
 * -> Action
 */
public final class RecoveryEngine {

    private final SafetyGuard safetyGuard =
            new SafetyGuard();

    private final FailureClassifier
            failureClassifier =
            new FailureClassifier();

    private int maxRetries = 1;

    public synchronized CommandResult recover(
            ActionExecutor executor,
            CommandContext context,
            ExecutionResult first) {

        if (first == null) {

            return CommandResult.failure(
                    "No execution result.");
        }

        if (first.isSuccess()) {

            return first.getCommandResult();
        }

        if (executor == null ||
                context == null) {

            return failureFrom(first);
        }

        FailureClassifier.Analysis
                analysis =
                failureClassifier.analyze(
                        first.getMessage());

        /*
         * Never retry cancellation,
         * safety blocks, validation errors,
         * or confirmation-required actions.
         */
        if (!analysis.isRetryable() ||
                analysis.requiresConfirmation()) {

            return failureFrom(first);
        }

        SafetyDecision decision;

        try {

            decision =
                    safetyGuard.check(
                            context.getRawCommand());

        } catch (Exception ignored) {

            return failureFrom(first);
        }

        if (decision == null ||
                !decision.isAllowed()) {

            return failureFrom(first);
        }

        if (decision.requiresConfirmation() ||
                decision.getRisk() ==
                        ActionRisk.SENSITIVE ||
                decision.getRisk() ==
                        ActionRisk.IRREVERSIBLE) {

            return failureFrom(first);
        }

        if (maxRetries <= 0) {

            return failureFrom(first);
        }

        int oldAttempts =
                executor.getMaxAttempts();

        executor.setMaxAttempts(
                Math.min(
                        2,
                        Math.max(
                                1,
                                maxRetries)));

        ExecutionResult retry = null;

        try {

            retry =
                    executor.execute(
                            context);

        } catch (Exception ignored) {

            /*
             * Preserve the original failure.
             */

        } finally {

            executor.setMaxAttempts(
                    oldAttempts);
        }

        if (retry != null &&
                retry.getCommandResult() != null) {

            return retry.getCommandResult();
        }

        return failureFrom(first);
    }

    private CommandResult failureFrom(
            ExecutionResult result) {

        if (result.getCommandResult() != null) {

            return result.getCommandResult();
        }

        String message =
                result.getMessage();

        if (message == null ||
                message.trim().isEmpty()) {

            message =
                    "Command execution failed.";
        }

        return CommandResult.failure(
                message);
    }

    public synchronized int
    getMaxRetries() {

        return maxRetries;
    }

    public synchronized void
    setMaxRetries(int value) {

        maxRetries =
                Math.max(
                        0,
                        Math.min(
                                2,
                                value));
    }

    public FailureClassifier
    getFailureClassifier() {

        return failureClassifier;
    }
}
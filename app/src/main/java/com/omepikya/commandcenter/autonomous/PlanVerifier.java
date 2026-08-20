package com.omepikya.commandcenter.autonomous;

/**
 * ============================================================
 * OMEPIKYA COMMAND CENTER
 * PLAN VERIFIER
 * ============================================================
 *
 * Phase 9D
 *
 * Verifies autonomous plans before, during and after
 * execution.
 */
public final class PlanVerifier {

    /**
     * Validate an autonomous plan before execution.
     */
    public boolean validate(
            AutonomousPlan plan) {

        if (plan == null) {
            return false;
        }

        if (plan.getGoal() == null) {
            return false;
        }

        if (plan.getGoal()
                .getText() == null ||
                plan.getGoal()
                        .getText()
                        .trim()
                        .isEmpty()) {

            return false;
        }

        if (plan.getSteps() == null ||
                plan.getSteps().isEmpty()) {

            return false;
        }

        for (AutonomousStep step :
                plan.getSteps()) {

            if (step == null ||
                    !step.isValid()) {

                return false;
            }
        }

        /*
         * Detect duplicate commands.
         */
        for (int i = 0;
             i < plan.getSteps().size();
             i++) {

            String first =
                    plan.getSteps()
                            .get(i)
                            .getCommand();

            if (first == null) {
                return false;
            }

            for (int j = i + 1;
                 j < plan.getSteps().size();
                 j++) {

                String second =
                        plan.getSteps()
                                .get(j)
                                .getCommand();

                if (second != null &&
                        first.trim()
                                .equalsIgnoreCase(
                                        second.trim())) {

                    return false;
                }
            }
        }

        return true;
    }

    /**
     * Verify an individual step.
     */
    public boolean verifyStep(
            AutonomousStep step,
            boolean success,
            String message) {

        if (step == null ||
                !step.isValid()) {

            return false;
        }

        step.setResult(message);

        if (success) {

            step.setFailed(false);
            step.setCompleted(true);

            return true;
        }

        step.setCompleted(false);
        step.setFailed(true);

        return false;
    }

    /**
     * Verify the final state of the plan.
     */
    public boolean verifyComplete(
            AutonomousPlan plan) {

        if (plan == null) {
            return false;
        }

        if (plan.isCancelled()) {
            return false;
        }

        if (!plan.isComplete()) {
            return false;
        }

        for (AutonomousStep step :
                plan.getSteps()) {

            if (step == null ||
                    !step.isCompleted() ||
                    step.isFailed()) {

                return false;
            }
        }

        return true;
    }

    /**
     * Checks whether a step is executable.
     */
    public boolean isExecutable(
            AutonomousStep step) {

        if (step == null ||
                !step.isValid()) {

            return false;
        }

        return !step.isCompleted() &&
                !step.isFailed();
    }
}
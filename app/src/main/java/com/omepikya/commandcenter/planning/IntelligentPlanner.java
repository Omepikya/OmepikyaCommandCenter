package com.omepikya.commandcenter.planning;

import java.util.List;

/**
 * Phase 7C:
 * validates and bounds multi-step plans.
 */
public final class IntelligentPlanner {

    private final ActionPlanner basePlanner;

    private int maxSteps = 8;

    public IntelligentPlanner(
            ActionPlanner basePlanner) {

        this.basePlanner =
                basePlanner == null
                        ? new ActionPlanner()
                        : basePlanner;
    }

    public synchronized CommandPlan plan(
            String command) {

        CommandPlan plan =
                basePlanner.plan(command);

        if (plan == null) {
            return null;
        }

        List<CommandStep> steps =
                plan.getSteps();

        if (steps.size() > maxSteps) {

            CommandPlan bounded =
                    new CommandPlan();

            for (int i = 0;
                    i < maxSteps;
                    i++) {

                bounded.add(
                        steps.get(i)
                                .getCommand());
            }

            return bounded;
        }

        return plan;
    }

    public synchronized boolean isValid(
            CommandPlan plan) {

        if (plan == null ||
                plan.getSteps().isEmpty()) {

            return false;
        }

        for (CommandStep step :
                plan.getSteps()) {

            if (step == null ||
                    step.getCommand() == null ||
                    step.getCommand()
                            .trim()
                            .isEmpty()) {

                return false;
            }
        }

        return true;
    }

    public synchronized int getMaxSteps() {
        return maxSteps;
    }

    public synchronized void setMaxSteps(
            int maxSteps) {

        this.maxSteps =
                Math.max(
                        1,
                        Math.min(
                                12,
                                maxSteps));
    }
}
package com.omepikya.commandcenter.autonomous;

import java.util.List;
import java.util.UUID;

public final class AutonomousPlanner {

    private final GoalDecomposer decomposer;

    public AutonomousPlanner() {

        this(new GoalDecomposer());
    }

    public AutonomousPlanner(
            GoalDecomposer decomposer) {

        this.decomposer =
                decomposer == null
                        ? new GoalDecomposer()
                        : decomposer;
    }

    public AutonomousPlan create(
            String goal) {

        AutonomousGoal autonomousGoal =
                new AutonomousGoal(
                        UUID.randomUUID()
                                .toString(),
                        goal);

        AutonomousPlan plan =
                new AutonomousPlan(
                        autonomousGoal);

        List<String> commands =
                decomposer.decompose(goal);

        for (String command :
                commands) {

            plan.addStep(command);
        }

        return plan;
    }

    public boolean canPlan(
            String goal) {

        return goal != null &&
                !goal.trim().isEmpty() &&
                !decomposer
                        .decompose(goal)
                        .isEmpty();
    }

    public GoalDecomposer getDecomposer() {
        return decomposer;
    }
}
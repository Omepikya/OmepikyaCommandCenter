package com.omepikya.commandcenter.autonomous;

public final class Replanner {

    private int maxReplans = 2;

    public AutonomousPlan replan(
            AutonomousPlan failed,
            String failure) {

        if (failed == null ||
                failed.isCancelled() ||
                !failed.hasNext()) {

            return null;
        }

        AutonomousPlan replacement =
                new AutonomousPlan(
                        new AutonomousGoal(
                                failed.getGoal().getId(),
                                failed.getGoal().getText()));

        replacement.getGoal().setStatus(
                AutonomousGoal.RUNNING);

        for (int i =
                failed.getCursor();
                i < failed.getSteps().size();
                i++) {

            AutonomousStep oldStep =
                    failed.getSteps().get(i);

            if (oldStep == null ||
                    !oldStep.isValid()) {

                continue;
            }

            replacement.addStep(
                    oldStep.getCommand());
        }

        return replacement;
    }

    public boolean canReplan(
            int attempts) {

        return attempts < maxReplans;
    }

    public int getMaxReplans() {
        return maxReplans;
    }

    public void setMaxReplans(
            int value) {

        maxReplans =
                Math.max(
                        0,
                        Math.min(
                                3,
                                value));
    }
}
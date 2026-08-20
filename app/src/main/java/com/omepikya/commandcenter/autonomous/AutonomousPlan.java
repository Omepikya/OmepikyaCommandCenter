package com.omepikya.commandcenter.autonomous;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class AutonomousPlan {

    private final AutonomousGoal goal;

    private final List<AutonomousStep> steps =
            new ArrayList<>();

    private int cursor;
    private boolean cancelled;

    public AutonomousPlan(
            AutonomousGoal goal) {

        this.goal = goal;
    }

    public AutonomousGoal getGoal() {
        return goal;
    }

    public synchronized void addStep(
            String command) {

        if (command == null ||
                command.trim().isEmpty()) {

            return;
        }

        steps.add(
                new AutonomousStep(
                        steps.size(),
                        command));
    }

    public synchronized void addStep(
            AutonomousStep step) {

        if (step == null ||
                !step.isValid()) {

            return;
        }

        steps.add(step);
    }

    public synchronized List<AutonomousStep>
    getSteps() {

        return Collections.unmodifiableList(
                new ArrayList<>(steps));
    }

    public synchronized int getCursor() {
        return cursor;
    }

    public synchronized void setCursor(
            int cursor) {

        this.cursor =
                Math.max(
                        0,
                        Math.min(
                                cursor,
                                steps.size()));
    }

    public synchronized boolean hasNext() {

        return !cancelled &&
                cursor < steps.size();
    }

    public synchronized AutonomousStep current() {

        if (!hasNext()) {
            return null;
        }

        return steps.get(cursor);
    }

    public synchronized void advance() {

        if (cursor < steps.size()) {
            cursor++;
        }
    }

    public synchronized void cancel() {

        cancelled = true;

        if (goal != null) {
            goal.setStatus(
                    AutonomousGoal.CANCELLED);
        }
    }

    public synchronized boolean isCancelled() {
        return cancelled;
    }

    public synchronized boolean isComplete() {

        return !cancelled &&
                cursor >= steps.size();
    }

    public synchronized int size() {
        return steps.size();
    }

    public synchronized int completedCount() {

        int count = 0;

        for (AutonomousStep step : steps) {

            if (step != null &&
                    step.isCompleted()) {

                count++;
            }
        }

        return count;
    }

    public synchronized double progress() {

        if (steps.isEmpty()) {
            return 0.0;
        }

        return
                ((double) completedCount() /
                        (double) steps.size()) *
                        100.0;
    }
}
package com.omepikya.commandcenter.intelligence;

import java.util.Calendar;
import java.util.List;

/**
 * Phase 7E:
 * conservative proactive suggestions.
 *
 * This class never executes a command by itself.
 */
public final class ProactiveIntelligence {

    private final BehaviorLearner learner;

    public ProactiveIntelligence(
            BehaviorLearner learner) {

        this.learner = learner;
    }

    public synchronized String suggest() {

        if (learner == null) {
            return null;
        }

        int hour =
                Calendar.getInstance()
                        .get(Calendar.HOUR_OF_DAY);

        List<BehaviorLearner.CommandPattern>
                predictions =
                learner.getPredictions(
                        hour,
                        1);

        if (predictions.isEmpty()) {
            return null;
        }

        String command =
                predictions.get(0)
                        .getCommand();

        if (command == null ||
                command.trim().isEmpty()) {

            return null;
        }

        return "You often use this around now: " +
                command;
    }
}
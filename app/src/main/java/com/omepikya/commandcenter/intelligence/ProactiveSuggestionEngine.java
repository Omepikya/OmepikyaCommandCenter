package com.omepikya.commandcenter.intelligence;

import android.content.Context;

import com.omepikya.commandcenter.automation.AutomationCore;
import com.omepikya.commandcenter.automation.AutomationTask;
import com.omepikya.commandcenter.context.ContextAnalyzer;
import com.omepikya.commandcenter.context.ContextEngine;
import com.omepikya.commandcenter.device.DeviceIntelligence;
import com.omepikya.commandcenter.device.DeviceState;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public final class ProactiveSuggestionEngine {

    private final BehaviorLearner behaviorLearner;

    private final DeviceIntelligence
            deviceIntelligence;

    private final ContextEngine contextEngine;

    private final ContextAnalyzer contextAnalyzer;

    private final AutomationCore automationCore;

    private int lowBatteryThreshold =
            15;

    public ProactiveSuggestionEngine(
            Context context,
            ContextEngine contextEngine
    ) {

        if (context == null) {

            throw new IllegalArgumentException(
                    "Context cannot be null"
            );
        }

        if (contextEngine == null) {

            throw new IllegalArgumentException(
                    "ContextEngine cannot be null"
            );
        }

        this.contextEngine =
                contextEngine;

        this.contextAnalyzer =
                new ContextAnalyzer(
                        contextEngine
                );

        behaviorLearner =
                new BehaviorLearner(
                        context
                );

        deviceIntelligence =
                new DeviceIntelligence(
                        context
                );

        automationCore =
                new AutomationCore(
                        context
                );
    }

    public synchronized List<CommandSuggestion>
    generate() {

        List<CommandSuggestion>
                suggestions =
                new ArrayList<>();

        addBatterySuggestion(
                suggestions
        );

        addFailureSuggestion(
                suggestions
        );

        addBehaviorSuggestions(
                suggestions
        );

        addContextSuggestions(
                suggestions
        );

        addAutomationSuggestion(
                suggestions
        );

        sortSuggestions(
                suggestions
        );

        return suggestions;
    }

    private void addBatterySuggestion(
            List<CommandSuggestion>
                    suggestions
    ) {

        DeviceState state;

        try {

            state =
                    deviceIntelligence.snapshot();

        } catch (Exception ignored) {

            return;
        }

        if (state == null) {

            return;
        }

        int battery =
                state.getBatteryPercent();

        if (battery < 0) {

            return;
        }

        if (state.isCharging()) {

            return;
        }

        if (battery <= lowBatteryThreshold) {

            suggestions.add(
                    new CommandSuggestion(

                            CommandSuggestion.Type
                                    .BATTERY,

                            "Your battery is at "
                                    + battery
                                    + "%. Consider charging your phone.",

                            null,

                            0.95
                    )
            );
        }
    }

    private void addFailureSuggestion(
            List<CommandSuggestion>
                    suggestions
    ) {

        if (!contextEngine
                .hasRecentFailure(
                        120_000L
                )) {

            return;
        }

        String command =
                contextEngine.getLastCommand();

        if (command == null
                || command.trim().isEmpty()) {

            return;
        }

        suggestions.add(
                new CommandSuggestion(

                        CommandSuggestion.Type
                                .FOLLOW_UP,

                        "Your previous command failed. "
                                + "Would you like to try it again?",

                        command,

                        0.90
                )
        );
    }

    private void addBehaviorSuggestions(
            List<CommandSuggestion>
                    suggestions
    ) {

        int currentHour =
                Calendar.getInstance()
                        .get(
                                Calendar.HOUR_OF_DAY
                        );

        List<BehaviorLearner.CommandPattern>
                predictions =
                behaviorLearner
                        .getPredictions(
                                currentHour,
                                3
                        );

        if (predictions == null
                || predictions.isEmpty()) {

            return;
        }

        for (
                BehaviorLearner.CommandPattern pattern
                : predictions
        ) {

            if (pattern == null) {

                continue;
            }

            if (pattern.getCount() < 2) {

                continue;
            }

            String command =
                    pattern.getCommand();

            if (command == null
                    || command.trim().isEmpty()) {

                continue;
            }

            double score =
                    Math.min(
                            0.90,
                            0.45
                                    + pattern.getCount()
                                    * 0.05
                    );

            suggestions.add(
                    new CommandSuggestion(

                            CommandSuggestion.Type
                                    .BEHAVIOR,

                            "You often use \""
                                    + command
                                    + "\" around this time.",

                            command,

                            score
                    )
            );
        }
    }

    private void addContextSuggestions(
            List<CommandSuggestion>
                    suggestions
    ) {

        String intent =
                contextEngine.getLastIntent();

        if (intent == null
                || intent.trim().isEmpty()) {

            return;
        }

        if (intent.equalsIgnoreCase(
                "NAVIGATION"
        )
                || intent.equalsIgnoreCase(
                        "navigation"
                )) {

            suggestions.add(
                    new CommandSuggestion(

                            CommandSuggestion.Type
                                    .CONTEXT,

                            "You just used navigation. "
                                    + "Would you like directions to another place?",

                            null,

                            0.65
                    )
            );

            return;
        }

        if (intent.equalsIgnoreCase(
                "MEDIA"
        )
                || intent.equalsIgnoreCase(
                        "media"
                )) {

            suggestions.add(
                    new CommandSuggestion(

                            CommandSuggestion.Type
                                    .CONTEXT,

                            "Would you like another media command?",

                            null,

                            0.60
                    )
            );
        }

        if (intent.equalsIgnoreCase(
                "ALARM"
        )
                || intent.equalsIgnoreCase(
                        "alarm"
                )) {

            suggestions.add(
                    new CommandSuggestion(

                            CommandSuggestion.Type
                                    .CONTEXT,

                            "Would you like me to set another reminder?",

                            null,

                            0.62
                    )
            );
        }
    }

    private void addAutomationSuggestion(
            List<CommandSuggestion>
                    suggestions
    ) {

        try {

            List<AutomationTask> tasks =
                    automationCore.getTasks();

            if (tasks == null
                    || tasks.isEmpty()) {

                return;
            }

            AutomationTask nearestTask =
                    null;

            long nearestDifference =
                    Long.MAX_VALUE;

            long now =
                    System.currentTimeMillis();

            for (
                    AutomationTask task
                    : tasks
            ) {

                if (task == null
                        || !task.isEnabled()) {

                    continue;
                }

                long difference =
                        task.getTriggerTime()
                                - now;

                if (difference <= 0
                        || difference
                        > 60L * 60L * 1000L) {

                    continue;
                }

                if (difference
                        < nearestDifference) {

                    nearestDifference =
                            difference;

                    nearestTask =
                            task;
                }
            }

            if (nearestTask == null) {

                return;
            }

            long minutes =
                    Math.max(
                            1L,
                            nearestDifference
                                    / 60_000L
                    );

            suggestions.add(
                    new CommandSuggestion(

                            CommandSuggestion.Type
                                    .FOLLOW_UP,

                            "You have \""
                                    + nearestTask.getName()
                                    + "\" scheduled in about "
                                    + minutes
                                    + " minute"
                                    + (
                                    minutes == 1
                                            ? ""
                                            : "s"
                            )
                                    + ".",

                            null,

                            0.80
                    )
            );

        } catch (Exception ignored) {
        }
    }

    private void sortSuggestions(
            List<CommandSuggestion>
                    suggestions
    ) {

        Collections.sort(
                suggestions,
                new Comparator<CommandSuggestion>() {

                    @Override
                    public int compare(
                            CommandSuggestion first,
                            CommandSuggestion second
                    ) {

                        return Double.compare(
                                second.getScore(),
                                first.getScore()
                        );
                    }
                }
        );
    }

    public synchronized void
    setLowBatteryThreshold(
            int threshold
    ) {

        if (threshold <= 0
                || threshold >= 100) {

            return;
        }

        lowBatteryThreshold =
                threshold;
    }

    public synchronized int
    getLowBatteryThreshold() {

        return lowBatteryThreshold;
    }

    public BehaviorLearner
    getBehaviorLearner() {

        return behaviorLearner;
    }

    public DeviceIntelligence
    getDeviceIntelligence() {

        return deviceIntelligence;
    }

    public ContextEngine
    getContextEngine() {

        return contextEngine;
    }

    public ContextAnalyzer
    getContextAnalyzer() {

        return contextAnalyzer;
    }
}
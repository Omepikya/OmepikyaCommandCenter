package com.omepikya.commandcenter.router;

import android.content.Context;

import com.omepikya.commandcenter.automation.AutomationCore;
import com.omepikya.commandcenter.core.CommandContext;
import com.omepikya.commandcenter.core.CommandResult;
import com.omepikya.commandcenter.core.CommandType;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AutomationAction implements Action {

    private final AutomationCore automationCore;

    private static final Pattern RELATIVE_TIME =
            Pattern.compile(
                    "\\b(?:in|after|for)\\s+(\\d+)\\s+(second|seconds|minute|minutes|hour|hours|day|days)\\b",
                    Pattern.CASE_INSENSITIVE
            );

    private static final Pattern CLOCK_TIME =
            Pattern.compile(
                    "\\bat\\s+(\\d{1,2}):(\\d{2})\\s*(am|pm)?\\b",
                    Pattern.CASE_INSENSITIVE
            );

    public AutomationAction(Context context) {

        if (context == null) {
            throw new IllegalArgumentException(
                    "Context cannot be null"
            );
        }

        automationCore =
                new AutomationCore(context);
    }

    @Override
    public String getName() {
        return "Automation";
    }

    @Override
    public boolean canHandle(
            CommandContext context
    ) {

        return context != null
                && context.getCommandType()
                == CommandType.AUTOMATION;
    }

    @Override
    public CommandResult execute(
            CommandContext context
    ) {

        if (context == null) {
            return CommandResult.failure(
                    "Command context is null"
            );
        }

        String rawCommand =
                context.getRawCommand();

        if (rawCommand == null
                || rawCommand.trim().isEmpty()) {

            return CommandResult.failure(
                    "No automation command was specified"
            );
        }

        String command =
                rawCommand.trim();

        long triggerTime =
                parseTriggerTime(command);

        if (triggerTime <= System.currentTimeMillis()) {

            return CommandResult.failure(
                    "I could not determine a future time for that automation."
            );
        }

        String taskCommand =
                extractTaskCommand(command);

        if (taskCommand.isEmpty()) {
            taskCommand = command;
        }

        String taskName =
                "Omepikya reminder";

        String lower =
                command.toLowerCase(Locale.US);

        if (lower.contains("alarm")) {
            taskName = "Omepikya alarm";
        }

        String taskId =
                automationCore.createTask(
                        taskName,
                        taskCommand,
                        triggerTime
                );

        if (taskId == null) {
            return CommandResult.failure(
                    "I couldn't schedule that automation."
            );
        }

        long remaining =
                triggerTime - System.currentTimeMillis();

        return CommandResult.success(
                "Scheduled for "
                        + formatDelay(remaining)
        );
    }

    private long parseTriggerTime(
            String command
    ) {

        Matcher relative =
                RELATIVE_TIME.matcher(command);

        if (relative.find()) {

            long amount;

            try {
                amount = Long.parseLong(
                        relative.group(1)
                );
            } catch (Exception e) {
                return -1L;
            }

            String unit =
                    relative.group(2)
                            .toLowerCase(Locale.US);

            long millis;

            if (unit.startsWith("second")) {
                millis = amount * 1000L;
            } else if (unit.startsWith("minute")) {
                millis = amount * 60_000L;
            } else if (unit.startsWith("hour")) {
                millis = amount * 3_600_000L;
            } else {
                millis = amount * 86_400_000L;
            }

            return System.currentTimeMillis() + millis;
        }

        Matcher clock =
                CLOCK_TIME.matcher(command);

        if (clock.find()) {

            try {

                int hour =
                        Integer.parseInt(
                                clock.group(1)
                        );

                int minute =
                        Integer.parseInt(
                                clock.group(2)
                        );

                String meridiem =
                        clock.group(3);

                if (meridiem != null) {

                    if (hour < 1 || hour > 12) {
                        return -1L;
                    }

                    if (meridiem.equalsIgnoreCase("pm")
                            && hour != 12) {
                        hour += 12;
                    }

                    if (meridiem.equalsIgnoreCase("am")
                            && hour == 12) {
                        hour = 0;
                    }

                } else if (hour > 23) {
                    return -1L;
                }

                java.util.Calendar calendar =
                        java.util.Calendar.getInstance();

                calendar.set(
                        java.util.Calendar.HOUR_OF_DAY,
                        hour
                );

                calendar.set(
                        java.util.Calendar.MINUTE,
                        minute
                );

                calendar.set(
                        java.util.Calendar.SECOND,
                        0
                );

                calendar.set(
                        java.util.Calendar.MILLISECOND,
                        0
                );

                if (calendar.getTimeInMillis()
                        <= System.currentTimeMillis()) {

                    calendar.add(
                            java.util.Calendar.DAY_OF_YEAR,
                            1
                    );
                }

                return calendar.getTimeInMillis();

            } catch (Exception ignored) {
                return -1L;
            }
        }

        return -1L;
    }

    private String extractTaskCommand(
            String command
    ) {

        String result = command.trim();

        result = result.replaceFirst(
                "(?i)^set\\s+(an\\s+)?alarm\\s+",
                ""
        );

        result = result.replaceFirst(
                "(?i)^remind\\s+me\\s+",
                ""
        );

        result = result.replaceFirst(
                "(?i)^schedule\\s+",
                ""
        );

        result = result.replaceFirst(
                "(?i)^(?:in|after|for)\\s+\\d+\\s+"
                        + "(?:seconds?|minutes?|hours?|days?)\\s*",
                ""
        );

        result = result.replaceFirst(
                "(?i)\\s+in\\s+\\d+\\s+"
                        + "(?:seconds?|minutes?|hours?|days?)\\s*$",
                ""
        );

        result = result.replaceFirst(
                "(?i)\\s+at\\s+\\d{1,2}:\\d{2}"
                        + "\\s*(?:am|pm)?\\s*$",
                ""
        );

        result = result.trim();

        if (result.toLowerCase(Locale.US)
                .startsWith("to ")) {

            result = result.substring(3).trim();
        }

        return result;
    }

    private String formatDelay(
            long milliseconds
    ) {

        long minutes =
                Math.max(
                        1L,
                        milliseconds / 60_000L
                );

        if (minutes < 60) {
            return minutes + " minute(s)";
        }

        long hours = minutes / 60L;
        long remainingMinutes = minutes % 60L;

        if (remainingMinutes == 0) {
            return hours + " hour(s)";
        }

        return hours
                + " hour(s) and "
                + remainingMinutes
                + " minute(s)";
    }
}

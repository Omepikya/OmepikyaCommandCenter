package com.omepikya.commandcenter.intelligence;

public class CommandSuggestion {

    public enum Type {
        BATTERY,
        BEHAVIOR,
        CONTEXT,
        FOLLOW_UP
    }

    private final Type type;
    private final String message;
    private final String command;
    private final double score;

    public CommandSuggestion(
            Type type,
            String message,
            String command,
            double score
    ) {

        this.type = type;
        this.message = message;
        this.command = command;
        this.score = score;
    }

    public Type getType() {
        return type;
    }

    public String getMessage() {
        return message;
    }

    public String getCommand() {
        return command;
    }

    public double getScore() {
        return score;
    }
}
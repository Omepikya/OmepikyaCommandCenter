package com.omepikya.commandcenter.autonomous;

public final class AutonomousStep {

    private final int index;
    private final String command;

    private int attempts;
    private boolean completed;
    private boolean failed;

    private String result;
    private long startedAt;
    private long completedAt;

    public AutonomousStep(
            int index,
            String command) {

        this.index = index;

        this.command =
                command == null
                        ? ""
                        : command.trim();
    }

    public int getIndex() {
        return index;
    }

    public String getCommand() {
        return command;
    }

    public int getAttempts() {
        return attempts;
    }

    public void incrementAttempts() {

        attempts++;

        if (startedAt == 0L) {
            startedAt =
                    System.currentTimeMillis();
        }
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(
            boolean completed) {

        this.completed = completed;

        if (completed) {
            this.failed = false;
            this.completedAt =
                    System.currentTimeMillis();
        }
    }

    public boolean isFailed() {
        return failed;
    }

    public void setFailed(
            boolean failed) {

        this.failed = failed;

        if (failed) {
            this.completed = false;
        }
    }

    public String getResult() {
        return result;
    }

    public void setResult(
            String result) {

        this.result = result;
    }

    public long getStartedAt() {
        return startedAt;
    }

    public long getCompletedAt() {
        return completedAt;
    }

    public boolean isValid() {

        return command != null &&
                !command.trim().isEmpty();
    }

    @Override
    public String toString() {

        return "AutonomousStep{" +
                "index=" + index +
                ", command='" + command + '\'' +
                ", attempts=" + attempts +
                ", completed=" + completed +
                ", failed=" + failed +
                '}';
    }
}
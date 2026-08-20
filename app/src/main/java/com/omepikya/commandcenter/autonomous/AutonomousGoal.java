package com.omepikya.commandcenter.autonomous;

public final class AutonomousGoal {

    public static final String CREATED = "CREATED";
    public static final String RUNNING = "RUNNING";
    public static final String WAITING_CONFIRMATION =
            "WAITING_CONFIRMATION";
    public static final String FAILED = "FAILED";
    public static final String COMPLETED = "COMPLETED";
    public static final String CANCELLED = "CANCELLED";

    private final String id;
    private final String text;
    private final long createdAt;

    private String status;
    private long updatedAt;

    public AutonomousGoal(
            String id,
            String text) {

        this.id = id == null ? "" : id;
        this.text = text == null ? "" : text.trim();

        this.createdAt =
                System.currentTimeMillis();

        this.updatedAt =
                this.createdAt;

        this.status = CREATED;
    }

    public String getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(
            String status) {

        this.status =
                status == null
                        ? CREATED
                        : status;

        this.updatedAt =
                System.currentTimeMillis();
    }

    public boolean isFinished() {

        return COMPLETED.equals(status) ||
                FAILED.equals(status) ||
                CANCELLED.equals(status);
    }

    @Override
    public String toString() {

        return "AutonomousGoal{" +
                "id='" + id + '\'' +
                ", text='" + text + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
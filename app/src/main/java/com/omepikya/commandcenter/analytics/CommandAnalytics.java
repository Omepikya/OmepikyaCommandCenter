package com.omepikya.commandcenter.analytics;

public final class CommandAnalytics {

    private final UsageProfile profile =
            new UsageProfile();

    public void record(
            String command,
            boolean success) {

        profile.record(command, success);
    }

    public UsageProfile getProfile() {
        return profile;
    }
}
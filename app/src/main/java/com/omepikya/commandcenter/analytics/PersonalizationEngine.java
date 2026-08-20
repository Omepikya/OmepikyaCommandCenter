package com.omepikya.commandcenter.analytics;

import java.util.*;

public final class PersonalizationEngine {

    private final UsageProfile profile;
    private final PreferenceLearner learner;

    public PersonalizationEngine(
            UsageProfile p,
            PreferenceLearner l) {

        profile = p;
        learner = l;
    }

    public String personalize(String command) {

        if (command == null) {
            return "";
        }

        String c = command.trim();

        String person =
                learner.get("preferred_contact");

        if (person != null
                && c.equalsIgnoreCase("call them")) {

            return "call " + person;
        }

        return c;
    }

    public List<String> suggestions(int limit) {
        return profile.topCommands(limit);
    }

    public UsageProfile getUsageProfile() {
        return profile;
    }

    public PreferenceLearner getPreferenceLearner() {
        return learner;
    }
}
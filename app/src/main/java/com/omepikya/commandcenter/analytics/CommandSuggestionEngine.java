package com.omepikya.commandcenter.analytics;

import java.util.List;

public final class CommandSuggestionEngine {

    private final PersonalizationEngine personalization;

    public CommandSuggestionEngine(
            PersonalizationEngine personalization) {

        this.personalization = personalization;
    }

    public List<String> suggest(int limit) {
        return personalization.suggestions(limit);
    }
}
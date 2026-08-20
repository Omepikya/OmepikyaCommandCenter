package com.omepikya.commandcenter.intelligence;

import com.omepikya.commandcenter.core.CommandType;
import com.omepikya.commandcenter.nlp.Intent;
import com.omepikya.commandcenter.nlp.IntentParser;

import java.util.Locale;

/**
 * Phase 7A:
 * unified intent interpretation.
 */
public final class IntentIntelligence {

    private final IntentParser parser;

    public IntentIntelligence(
            IntentParser parser) {

        this.parser =
                parser == null
                        ? new IntentParser()
                        : parser;
    }

    public String normalize(
            String input) {

        if (input == null) {
            return "";
        }

        return input.trim()
                .replaceAll(
                        "\\s+",
                        " ")
                .toLowerCase(
                        Locale.US);
    }

    public Intent interpret(
            String input) {

        String normalized =
                normalize(input);

        return parser.parse(normalized);
    }

    public boolean isUsable(
            Intent intent) {

        return intent != null &&
                intent.getCommandType() != null &&
                intent.getCommandType() !=
                        CommandType.UNKNOWN;
    }

    public IntentParser getParser() {
        return parser;
    }
}
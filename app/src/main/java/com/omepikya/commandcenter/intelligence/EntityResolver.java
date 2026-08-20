package com.omepikya.commandcenter.intelligence;

import com.omepikya.commandcenter.context.ContextEngine;

public class EntityResolver {
    private final ContextEngine contextEngine;

    public EntityResolver(ContextEngine contextEngine) {
        if (contextEngine == null) throw new IllegalArgumentException("ContextEngine cannot be null");
        this.contextEngine = contextEngine;
    }

    public String resolve(String input) {
        if (input == null) return null;
        String text = input.trim();
        if (text.isEmpty()) return text;
        if (isPronoun(text.toLowerCase())) {
            String entity = getContextEntity();
            return entity != null ? entity : input;
        }
        return input;
    }

    public String resolveEntity(String input) { return resolve(input); }

    public String resolvePerson(String value, String person, Object ignored) {
        if (value == null) return null;
        String v = value.trim().toLowerCase();
        if (isPronoun(v) && person != null && !person.trim().isEmpty()) return person;
        return value;
    }

    public Integer ordinal(String text) {
        if (text == null) return null;
        String v = text.trim().toLowerCase();
        switch (v) {
            case "1": case "one": case "first": return 1;
            case "2": case "two": case "second": return 2;
            case "3": case "three": case "third": return 3;
            case "4": case "four": case "fourth": return 4;
            case "5": case "five": case "fifth": return 5;
            case "6": case "six": case "sixth": return 6;
            case "7": case "seven": case "seventh": return 7;
            case "8": case "eight": case "eighth": return 8;
            case "9": case "nine": case "ninth": return 9;
            case "10": case "ten": case "tenth": return 10;
            default: return null;
        }
    }

    private String getContextEntity() {
        Object entity = contextEngine.get("person");
        if (entity == null) entity = contextEngine.get("last_entity");
        if (entity == null) entity = contextEngine.get("active_entity");
        return entity == null ? null : String.valueOf(entity);
    }

    private boolean isPronoun(String text) {
        return text.equals("him") || text.equals("her") || text.equals("them")
                || text.equals("that person") || text.equals("same person")
                || text.equals("the same person") || text.equals("that") || text.equals("it");
    }
}

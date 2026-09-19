package com.omepikya.commandcenter.core;

import com.omepikya.commandcenter.context.ContextEngine;
import com.omepikya.commandcenter.intelligence.EntityResolver;
import com.omepikya.commandcenter.nlp.Intent;

import java.util.List;
import java.util.Locale;

/** Pure command-brain helpers kept outside the orchestration class. */
public final class CommandBrainSupport {

    private CommandBrainSupport() { }

    public static String extractWorkflowInvocation(String command) {
        if (command == null) return null;
        String lower = command.toLowerCase(Locale.US).trim();
        String prefix = "run workflow ";
        if (!lower.startsWith(prefix)) return null;
        String name = command.trim().substring(prefix.length()).trim();
        return name.isEmpty() ? null : name;
    }

    public static String resolvePronouns(String command, ContextEngine contextEngine, EntityResolver entityResolver) {
        if (command == null) return "";
        String s = command.trim();
        String person = contextEngine.get("person");
        if (person == null || person.trim().isEmpty()) return s;

        String lower = s.toLowerCase(Locale.US);
        String target;
        if (lower.startsWith("call ")) target = lower.substring(5).trim();
        else if (lower.startsWith("message ")) target = lower.substring(8).trim();
        else if (lower.startsWith("text ")) target = lower.substring(5).trim();
        else target = lower;

        String resolved = entityResolver.resolvePerson(target, person, null);
        if (resolved == null || resolved.trim().isEmpty()) resolved = person;

        if (lower.equals("call him") || lower.equals("call her") || lower.equals("call them")) {
            return "call " + resolved;
        }
        if (lower.startsWith("message him ") || lower.startsWith("message her ") || lower.startsWith("message them ")) {
            return "message " + resolved + tailAfterPronoun(s);
        }
        if (lower.startsWith("text him ") || lower.startsWith("text her ") || lower.startsWith("text them ")) {
            return "text " + resolved + tailAfterPronoun(s);
        }
        return s;
    }

    private static String tailAfterPronoun(String s) {
        int first = s.indexOf(' ');
        if (first < 0) return "";
        String rest = s.substring(first + 1).trim();
        int second = rest.indexOf(' ');
        if (second < 0) return "";
        return " " + rest.substring(second + 1).trim();
    }

    public static String describe(Intent intent) {
        if (intent == null || intent.getCommandType() == null) return "an unknown command";
        StringBuilder b = new StringBuilder(intent.getCommandType().name().toLowerCase(Locale.US).replace('_', ' '));
        String person = intent.getEntity("person");
        String app = intent.getEntity("app");
        if (person != null && !person.isEmpty()) b.append(" for ").append(person);
        else if (app != null && !app.isEmpty()) b.append(' ').append(app);
        return b.toString();
    }

    public static String join(List<String> messages, String failure) {
        String f = failure == null || failure.trim().isEmpty() ? "Command failed." : failure;
        if (messages == null || messages.isEmpty()) return f;
        return String.join("\\n", messages) + "\\n" + f;
    }
}

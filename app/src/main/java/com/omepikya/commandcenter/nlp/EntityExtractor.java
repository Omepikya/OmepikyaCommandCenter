package com.omepikya.commandcenter.nlp;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class EntityExtractor {

    public Map<String, String> extract(String command) {
        Map<String, String> entities = new HashMap<>();
        if (command == null || command.trim().isEmpty()) return entities;
        String text = command.trim();
        String lower = text.toLowerCase(Locale.US);
        extractApp(text, lower, entities);
        extractCommunication(text, lower, entities);
        extractTime(text, lower, entities);
        extractSearch(text, lower, entities);
        extractNavigation(text, lower, entities);
        extractMedia(lower, entities);
        return entities;
    }

    private void extractApp(String text, String lower, Map<String, String> entities) {
        for (String prefix : new String[]{"open ", "launch ", "start "}) {
            if (lower.startsWith(prefix)) {
                String app = text.substring(prefix.length()).trim();
                if (!app.isEmpty()) entities.put("app", app);
                return;
            }
        }
    }

    private void extractCommunication(String text, String lower, Map<String, String> entities) {
        for (String prefix : new String[]{"call ", "phone ", "dial ", "ring "}) {
            if (lower.startsWith(prefix)) {
                entities.put("person", text.substring(prefix.length()).trim());
                entities.put("communication_type", "call");
                return;
            }
        }
        if (lower.startsWith("give ") && lower.contains(" a call")) {
            String person = text.substring(5).trim();
            int index = person.toLowerCase(Locale.US).indexOf(" a call");
            if (index >= 0) person = person.substring(0, index).trim();
            entities.put("person", person);
            entities.put("communication_type", "call");
            return;
        }
        for (String prefix : new String[]{
                "send a whatsapp message to ", "send whatsapp message to ",
                "send a message to ", "send message to ", "send sms to ",
                "send an sms to ", "whatsapp ", "message ", "text ", "sms "
        }) {
            if (!lower.startsWith(prefix)) continue;
            String remainder = text.substring(prefix.length()).trim();
            String type = prefix.contains("whatsapp") ? "whatsapp" : "sms";
            int split = messageSplit(remainder);
            if (split > 0) {
                entities.put("person", remainder.substring(0, split).trim());
                entities.put("message", messageAfterSeparator(remainder, split));
            } else {
                entities.put("person", remainder);
            }
            entities.put("communication_type", type);
            return;
        }
    }

    private int messageSplit(String remainder) {
        String lower = remainder.toLowerCase(Locale.US);
        for (String marker : new String[]{" saying ", " that ", " with message ", " message "}) {
            int index = lower.indexOf(marker);
            if (index > 0) return index;
        }
        return -1;
    }

    private String messageAfterSeparator(String remainder, int split) {
        String lower = remainder.toLowerCase(Locale.US);
        String person = remainder.substring(0, split).trim();
        String lowerPerson = person.toLowerCase(Locale.US);
        for (String marker : new String[]{" saying ", " that ", " with message ", " message "}) {
            if (lower.startsWith(lowerPerson + marker)) {
                return remainder.substring(split + marker.length()).trim();
            }
        }
        return remainder.substring(split).trim();
    }

    private void extractTime(String text, String lower, Map<String, String> entities) {
        for (String marker : new String[]{" at ", " tomorrow", " today", " tonight", " morning", " evening", " afternoon"}) {
            int index = lower.indexOf(marker);
            if (index >= 0) {
                String value = text.substring(Math.min(text.length(), index + (marker.startsWith(" ") ? 1 : 0))).trim();
                if (!value.isEmpty()) entities.put("time", value);
                return;
            }
        }
    }

    private void extractSearch(String text, String lower, Map<String, String> entities) {
        for (String prefix : new String[]{"search for ", "search the web for ", "google "}) {
            if (lower.startsWith(prefix)) {
                entities.put("query", text.substring(prefix.length()).trim());
                return;
            }
        }
    }

    private void extractNavigation(String text, String lower, Map<String, String> entities) {
        for (String prefix : new String[]{"navigate to ", "directions to ", "direction to ", "take me to ", "show me the way to ", "map "}) {
            if (lower.startsWith(prefix)) {
                entities.put("destination", text.substring(prefix.length()).trim());
                return;
            }
        }
    }

    private void extractMedia(String lower, Map<String, String> entities) {
        if (lower.contains("next track") || lower.contains("skip track")) {
            entities.put("media_action", "next");
        } else if (lower.contains("previous track")) {
            entities.put("media_action", "previous");
        } else if (lower.contains("pause")) {
            entities.put("media_action", "pause");
        } else if (lower.contains("resume") || lower.contains("play")) {
            entities.put("media_action", "play");
        }
    }
}

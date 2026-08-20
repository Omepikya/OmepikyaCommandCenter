package com.omepikya.commandcenter.security;

import java.util.Locale;

public final class SafetyPolicy {

    public SafetyDecision evaluate(String command) {

        if (command == null
                || command.trim().isEmpty()) {

            return new SafetyDecision(
                    ActionRisk.SAFE,
                    false,
                    false,
                    "Empty command");
        }

        String s =
                command.toLowerCase(Locale.US);

        String[] sensitive = {
                "send money",
                "purchase",
                "buy",
                "delete",
                "erase",
                "factory reset",
                "send sms",
                "send message",
                "call ",
                "cancel alarm",
                "remove account"
        };

        for (String x : sensitive) {

            if (s.contains(x)) {

                return new SafetyDecision(
                        ActionRisk.SENSITIVE,
                        true,
                        true,
                        "This action can have an external or irreversible effect.");
            }
        }

        String[] irreversible = {
                "uninstall",
                "format",
                "reset"
        };

        for (String x : irreversible) {

            if (s.contains(x)) {

                return new SafetyDecision(
                        ActionRisk.IRREVERSIBLE,
                        true,
                        true,
                        "This action may be difficult to undo.");
            }
        }

        String[] reversible = {
                "change",
                "set",
                "turn on",
                "turn off",
                "open",
                "launch",
                "navigate",
                "play",
                "pause"
        };

        for (String x : reversible) {

            if (s.contains(x)) {

                return new SafetyDecision(
                        ActionRisk.REVERSIBLE,
                        true,
                        false,
                        "Reversible device action.");
            }
        }

        return new SafetyDecision(
                ActionRisk.SAFE,
                true,
                false,
                "No elevated risk detected.");
    }
}
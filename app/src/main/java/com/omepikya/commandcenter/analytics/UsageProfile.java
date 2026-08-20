package com.omepikya.commandcenter.analytics;

import java.util.*;

public final class UsageProfile {

    private final Map<String, Integer> commandCounts =
            new HashMap<>();

    private final Map<String, Integer> successCounts =
            new HashMap<>();

    public synchronized void record(
            String command,
            boolean success) {

        String k = normalize(command);

        if (k.isEmpty()) {
            return;
        }

        commandCounts.put(
                k,
                getCount(command) + 1);

        if (success) {

            successCounts.put(
                    k,
                    successCounts.containsKey(k)
                            ? successCounts.get(k) + 1
                            : 1);
        }
    }

    private String normalize(String s) {
        return s == null
                ? ""
                : s.trim().toLowerCase(Locale.US);
    }

    public synchronized int getCount(String c) {

        String k = normalize(c);

        Integer n = commandCounts.get(k);

        return n == null ? 0 : n;
    }

    public synchronized double getSuccessRate(
            String c) {

        int n = getCount(c);

        if (n == 0) {
            return 0;
        }

        Integer s =
                successCounts.get(normalize(c));

        return (s == null ? 0 : s) / (double) n;
    }

    public synchronized List<String> topCommands(
            int limit) {

        List<String> r =
                new ArrayList<>(
                        commandCounts.keySet());

        Collections.sort(
                r,
                (a, b) ->
                        Integer.compare(
                                commandCounts.get(b),
                                commandCounts.get(a)));

        return r.subList(
                0,
                Math.min(limit, r.size()));
    }

    public synchronized void clear() {
        commandCounts.clear();
        successCounts.clear();
    }
}
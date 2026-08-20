package com.omepikya.commandcenter.memory;

import android.content.Context;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MemoryManager {

    private final Context appContext;
    private final PreferenceMemory preferenceMemory;

    private final Map<String, String> memory =
            new HashMap<>();

    private final List<String> commandHistory =
            new ArrayList<>();

    public MemoryManager(Context context) {

        if (context == null) {
            throw new IllegalArgumentException(
                    "Context cannot be null"
            );
        }

        this.appContext =
                context.getApplicationContext();

        this.preferenceMemory =
                new PreferenceMemory(appContext);

        loadPersistentMemory();
    }

    private void loadPersistentMemory() {

        Map<String, ?> stored =
                preferenceMemory.getAll();

        if (stored == null) {
            return;
        }

        for (Map.Entry<String, ?> entry :
                stored.entrySet()) {

            Object value = entry.getValue();

            if (value != null) {
                memory.put(
                        entry.getKey(),
                        String.valueOf(value)
                );
            }
        }
    }

    public synchronized void remember(
            String key,
            String value
    ) {

        if (key == null ||
                key.trim().isEmpty() ||
                value == null) {
            return;
        }

        memory.put(key, value);

        preferenceMemory.put(
                key,
                value
        );
    }

    public synchronized String recall(
            String key
    ) {

        if (key == null) {
            return null;
        }

        String value = memory.get(key);

        if (value != null) {
            return value;
        }

        return preferenceMemory.get(key);
    }

    public synchronized boolean has(
            String key
    ) {

        if (key == null) {
            return false;
        }

        return memory.containsKey(key)
                || preferenceMemory.contains(key);
    }

    public synchronized void forget(
            String key
    ) {

        if (key == null) {
            return;
        }

        memory.remove(key);

        preferenceMemory.remove(key);
    }

    public synchronized void clear() {

        memory.clear();
        commandHistory.clear();

        preferenceMemory.clear();
    }

    public synchronized void recordCommand(
            String command
    ) {

        if (command == null ||
                command.trim().isEmpty()) {
            return;
        }

        commandHistory.add(command);

        if (commandHistory.size() > 100) {
            commandHistory.remove(0);
        }
    }

    public synchronized List<String>
    getCommandHistory() {

        return new ArrayList<>(
                commandHistory
        );
    }

    public synchronized List<String>
    getRecentCommands(int count) {

        if (count <= 0 ||
                commandHistory.isEmpty()) {
            return Collections.emptyList();
        }

        int start =
                Math.max(
                        0,
                        commandHistory.size() - count
                );

        return new ArrayList<>(
                commandHistory.subList(
                        start,
                        commandHistory.size()
                )
        );
    }

    public Context getApplicationContext() {
        return appContext;
    }

    public PreferenceMemory
    getPreferenceMemory() {
        return preferenceMemory;
    }


    // Compatibility API for MemoryIntelligence.
    public synchronized String save(String key, String value) {
        remember(key, value);
        return key;
    }

    public synchronized MemoryItem get(String key) {
        String value = recall(key);
        if (value == null) return null;
        return new MemoryItem(key, key, value, System.currentTimeMillis());
    }

    public synchronized boolean removeByKey(String key) {
        if (!has(key)) return false;
        forget(key);
        return true;
    }

    public synchronized List<MemoryItem> getAll() {
        List<MemoryItem> result = new ArrayList<>();
        for (Map.Entry<String, String> entry : memory.entrySet()) {
            result.add(new MemoryItem(
                    entry.getKey(),
                    entry.getKey(),
                    entry.getValue(),
                    System.currentTimeMillis()
            ));
        }
        return result;
    }
}
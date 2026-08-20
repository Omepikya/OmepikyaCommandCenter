package com.omepikya.commandcenter.memory;

public class MemoryItem {

    private final String id;
    private final String key;
    private final String value;
    private final long timestamp;

    public MemoryItem(
            String id,
            String key,
            String value,
            long timestamp
    ) {
        this.id = id;
        this.key = key;
        this.value = value;
        this.timestamp = timestamp;
    }

    public String getId() {
        return id;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
package com.omepikya.commandcenter.analytics;

import android.util.Log;

import com.omepikya.commandcenter.config.Constants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EventLogger {

    public static class Event {

        private final String name;
        private final String data;
        private final long timestamp;

        public Event(
                String name,
                String data,
                long timestamp
        ) {
            this.name = name;
            this.data = data;
            this.timestamp = timestamp;
        }

        public String getName() {
            return name;
        }

        public String getData() {
            return data;
        }

        public long getTimestamp() {
            return timestamp;
        }
    }

    private final List<Event> events;

    public EventLogger() {
        events = new ArrayList<>();
    }

    public synchronized void log(
            String eventName
    ) {

        log(eventName, null);
    }

    public synchronized void log(
            String eventName,
            String data
    ) {

        if (eventName == null
                || eventName.trim().isEmpty()) {
            return;
        }

        Event event = new Event(
                eventName.trim(),
                data,
                System.currentTimeMillis()
        );

        events.add(event);

        Log.d(
                Constants.TAG,
                "EVENT: " + event.getName()
        );
    }

    public synchronized List<Event> getEvents() {

        return Collections.unmodifiableList(
                new ArrayList<>(events)
        );
    }

    public synchronized Event getLastEvent() {

        if (events.isEmpty()) {
            return null;
        }

        return events.get(
                events.size() - 1
        );
    }

    public synchronized void clear() {
        events.clear();
    }

    public synchronized int size() {
        return events.size();
    }
}
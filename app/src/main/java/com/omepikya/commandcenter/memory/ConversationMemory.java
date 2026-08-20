package com.omepikya.commandcenter.memory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ConversationMemory {

    public static class Message {

        private final String role;
        private final String content;
        private final long timestamp;

        public Message(
                String role,
                String content,
                long timestamp
        ) {
            this.role = role;
            this.content = content;
            this.timestamp = timestamp;
        }

        public String getRole() {
            return role;
        }

        public String getContent() {
            return content;
        }

        public long getTimestamp() {
            return timestamp;
        }
    }

    private final List<Message> messages;
    private final int maxMessages;

    public ConversationMemory() {
        this(100);
    }

    public ConversationMemory(int maxMessages) {

        if (maxMessages <= 0) {
            maxMessages = 100;
        }

        this.maxMessages = maxMessages;
        this.messages = new ArrayList<>();
    }

    public synchronized void addUserMessage(
            String message
    ) {

        addMessage("user", message);
    }

    public synchronized void addAssistantMessage(
            String message
    ) {

        addMessage("assistant", message);
    }

    public synchronized void addMessage(
            String role,
            String content
    ) {

        if (role == null
                || role.trim().isEmpty()
                || content == null
                || content.trim().isEmpty()) {

            return;
        }

        messages.add(
                new Message(
                        role.trim(),
                        content.trim(),
                        System.currentTimeMillis()
                )
        );

        while (messages.size() > maxMessages) {
            messages.remove(0);
        }
    }

    public synchronized List<Message> getMessages() {

        return Collections.unmodifiableList(
                new ArrayList<>(messages)
        );
    }

    public synchronized Message getLastMessage() {

        if (messages.isEmpty()) {
            return null;
        }

        return messages.get(
                messages.size() - 1
        );
    }

    public synchronized void clear() {
        messages.clear();
    }

    public synchronized int size() {
        return messages.size();
    }
}
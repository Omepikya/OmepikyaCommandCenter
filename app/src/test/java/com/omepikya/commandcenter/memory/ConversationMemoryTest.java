package com.omepikya.commandcenter.memory;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ConversationMemoryTest {

    @Test
    public void keepsOrderedConversationMessages() {
        ConversationMemory memory =
                new ConversationMemory(3);

        memory.addUserMessage("open calculator");
        memory.addAssistantMessage("Calculator opened.");

        assertEquals(2, memory.size());
        assertEquals(
                "user",
                memory.getMessages().get(0).getRole());
        assertEquals(
                "open calculator",
                memory.getMessages().get(0).getContent());
        assertEquals(
                "Calculator opened.",
                memory.getLastMessage().getContent());
    }

    @Test
    public void boundsConversationHistory() {
        ConversationMemory memory =
                new ConversationMemory(2);

        memory.addUserMessage("one");
        memory.addUserMessage("two");
        memory.addUserMessage("three");

        assertEquals(2, memory.size());
        assertEquals(
                "two",
                memory.getMessages().get(0).getContent());
        assertNotNull(memory.getLastMessage());
    }
}

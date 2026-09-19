package com.omepikya.commandcenter.core;

import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class CommandBrainSupportTest {

    @Test
    public void extractsWorkflowName() {
        assertEquals(
                "morning routine",
                CommandBrainSupport.extractWorkflowInvocation(
                        "Run Workflow morning routine"));
    }

    @Test
    public void rejectsNonWorkflowCommand() {
        assertNull(
                CommandBrainSupport.extractWorkflowInvocation(
                        "open settings"));
    }

    @Test
    public void joinsMessagesWithRealNewlines() {
        assertEquals(
                "first\nsecond\nfailed",
                CommandBrainSupport.join(
                        Arrays.asList("first", "second"),
                        "failed"));
    }

    @Test
    public void suppliesFallbackFailure() {
        assertEquals(
                "Command failed.",
                CommandBrainSupport.join(
                        null,
                        "  "));
    }
}

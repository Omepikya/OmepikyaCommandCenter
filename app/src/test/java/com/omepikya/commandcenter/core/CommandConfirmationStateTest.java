package com.omepikya.commandcenter.core;

import org.junit.Test;

import com.omepikya.commandcenter.nlp.CommandType;
import com.omepikya.commandcenter.nlp.Intent;

import static org.junit.Assert.*;

public class CommandConfirmationStateTest {

    @Test
    public void storesAndClearsPendingConfirmation() {
        CommandConfirmationState state = new CommandConfirmationState();
        Intent intent = new Intent(
                CommandType.DEVICE_ACTION,
                "open calculator",
                null,
                0.9);

        state.set("open calculator", intent, true);

        assertTrue(state.isPending());
        assertEquals("open calculator", state.getCommand());
        assertSame(intent, state.getIntent());
        assertTrue(state.isSafetyConfirmation());

        state.clear();

        assertFalse(state.isPending());
        assertNull(state.getCommand());
        assertNull(state.getIntent());
        assertFalse(state.isSafetyConfirmation());
    }

    @Test
    public void incompleteStateIsNotPending() {
        CommandConfirmationState state = new CommandConfirmationState();
        Intent intent = new Intent(
                CommandType.DEVICE_ACTION,
                "open calculator",
                null,
                0.9);

        state.set(null, intent, true);
        assertFalse(state.isPending());

        state.set("open calculator", null, true);
        assertFalse(state.isPending());
    }
}

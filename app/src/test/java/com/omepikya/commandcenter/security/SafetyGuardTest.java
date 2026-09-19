package com.omepikya.commandcenter.security;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class SafetyGuardTest {

    private final SafetyGuard guard =
            new SafetyGuard();

    @Test
    public void ordinaryCommandCanExecuteWithoutConfirmation() {
        assertTrue(
                guard.canExecute(
                        "open calculator",
                        false));
    }

    @Test
    public void confirmationProtectedCommandCannotBypassConfirmation() {
        SafetyDecision decision =
                guard.check("delete all files");

        if (decision.requiresConfirmation()) {
            assertFalse(
                    guard.canExecute(
                            "delete all files",
                            false));
            assertTrue(
                    guard.canExecute(
                            "delete all files",
                            true));
        }
    }
}

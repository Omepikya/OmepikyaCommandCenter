package com.omepikya.commandcenter.core;

import com.omepikya.commandcenter.execution.ActionExecutor;
import com.omepikya.commandcenter.execution.ExecutionCoordinator;
import com.omepikya.commandcenter.execution.ExecutionResult;
import com.omepikya.commandcenter.execution.RecoveryEngine;
import com.omepikya.commandcenter.nlp.Intent;
import com.omepikya.commandcenter.nlp.IntentParser;
import com.omepikya.commandcenter.router.Action;
import com.omepikya.commandcenter.router.ActionRegistry;
import com.omepikya.commandcenter.router.ActionRouter;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CommandPipelineIntegrationTest {

    @Test
    public void parsesIntentAndExecutesThroughCoordinator() {
        Intent intent =
                new IntentParser().parse(
                        "open calculator");

        ActionRegistry registry =
                new ActionRegistry();

        registry.register(
                new Action() {
                    @Override
                    public String getName() {
                        return "calculator";
                    }

                    @Override
                    public boolean canHandle(
                            CommandContext context) {
                        return context.getCommandType() ==
                                CommandType.OPEN_APP;
                    }

                    @Override
                    public CommandResult execute(
                            CommandContext context) {
                        return CommandResult.success(
                                "calculator opened");
                    }
                });

        ExecutionCoordinator coordinator =
                new ExecutionCoordinator(
                        new ActionExecutor(
                                new ActionRouter(registry)),
                        new RecoveryEngine());

        ExecutionResult result =
                coordinator.execute(
                        new CommandContext(
                                intent.getRawCommand(),
                                intent.getCommandType()));

        assertTrue(intent.isConfident());
        assertEquals(
                CommandType.OPEN_APP,
                intent.getCommandType());
        assertTrue(result.isSuccess());
        assertEquals(
                "calculator opened",
                result.getMessage());
        assertEquals(
                ExecutionStatus.SUCCESS,
                result.getStatus());
    }
}

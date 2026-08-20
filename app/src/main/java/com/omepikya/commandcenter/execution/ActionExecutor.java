package com.omepikya.commandcenter.execution;

import android.content.Context;

import com.omepikya.commandcenter.core.CommandContext;
import com.omepikya.commandcenter.core.CommandResult;
import com.omepikya.commandcenter.planning.ActionValidator;
import com.omepikya.commandcenter.router.ActionRouter;

import java.util.UUID;

/**
 * Central execution gateway.
 *
 * CommandBrain
 *      ↓
 * ActionExecutor
 *      ↓
 * ActionValidator
 *      ↓
 * ActionRouter
 *      ↓
 * Action
 *      ↓
 * ExecutionResult
 *      ↓
 * Persistent ExecutionHistory
 */
public class ActionExecutor {

    private final Context appContext;

    private final ActionRouter actionRouter;

    private final ActionValidator validator;

    private final ExecutionHistory history;

    private int maxAttempts = 1;

    public ActionExecutor(
            ActionRouter actionRouter) {

        if (actionRouter == null) {

            throw new IllegalArgumentException(
                    "ActionRouter cannot be null");
        }

        this.appContext = null;

        this.actionRouter =
                actionRouter;

        this.validator =
                new ActionValidator();

        this.history =
                new ExecutionHistory();
    }

    public ActionExecutor(
            Context context,
            ActionRouter actionRouter) {

        if (context == null) {

            throw new IllegalArgumentException(
                    "Context cannot be null");
        }

        if (actionRouter == null) {

            throw new IllegalArgumentException(
                    "ActionRouter cannot be null");
        }

        this.appContext =
                context.getApplicationContext();

        this.actionRouter =
                actionRouter;

        this.validator =
                new ActionValidator();

        this.history =
                new ExecutionHistory(
                        context);
    }

    /**
     * Normal execution entry point.
     */
    public synchronized ExecutionResult
    execute(
            CommandContext context) {

        return execute(
                context,
                UUID.randomUUID()
                        .toString());
    }

    /**
     * Execution entry point with an externally
     * supplied execution ID.
     *
     * ExecutionCoordinator uses this method so
     * the same ID is preserved across the
     * complete lifecycle.
     */
    public synchronized ExecutionResult
    execute(
            CommandContext context,
            String executionId) {

        if (executionId == null ||
                executionId.trim().isEmpty()) {

            executionId =
                    UUID.randomUUID()
                            .toString();
        }

        String command =
                context == null
                        ? ""
                        : context.getRawCommand();

        /*
         * Validation happens before routing.
         */
        String validationError =
                validator.validate(
                        context);

        if (validationError != null) {

            ExecutionResult result =
                    ExecutionResult.failure(
                            executionId,
                            command,
                            validationError,
                            0,
                            CommandResult.failure(
                                    validationError));

            history.add(result);

            return result;
        }

        int attempts = 0;

        CommandResult commandResult =
                null;

        while (attempts < maxAttempts) {

            attempts++;

            try {

                commandResult =
                        actionRouter.route(
                                context);

            } catch (Exception e) {

                commandResult =
                        CommandResult.failure(
                                safeMessage(
                                        e,
                                        "Action execution failed."));
            }

            if (commandResult != null &&
                    commandResult.isSuccess()) {

                ExecutionResult result =
                        ExecutionResult.success(
                                executionId,
                                command,
                                commandResult
                                        .getMessage(),
                                attempts,
                                commandResult);

                history.add(result);

                return result;
            }

            /*
             * Do not blindly repeat actions.
             *
             * RecoveryEngine owns retry decisions.
             */
            break;
        }

        String message =
                commandResult == null
                        ? "No execution result was returned."
                        : commandResult.getMessage();

        if (message == null ||
                message.trim().isEmpty()) {

            message =
                    "Command execution failed.";
        }

        ExecutionResult result =
                ExecutionResult.failure(
                        executionId,
                        command,
                        message,
                        attempts,
                        commandResult);

        history.add(result);

        return result;
    }

    /**
     * Compatibility API returning only CommandResult.
     */
    public synchronized CommandResult
    executeCommand(
            CommandContext context) {

        ExecutionResult result =
                execute(context);

        if (result.getCommandResult() != null) {

            return result.getCommandResult();
        }

        if (result.isSuccess()) {

            return CommandResult.success(
                    result.getMessage());
        }

        return CommandResult.failure(
                result.getMessage());
    }

    private String safeMessage(
            Exception e,
            String fallback) {

        if (e != null &&
                e.getMessage() != null &&
                !e.getMessage()
                        .trim()
                        .isEmpty()) {

            return e.getMessage()
                    .trim();
        }

        return fallback;
    }

    public Context getContext() {
        return appContext;
    }

    public ActionValidator
    getValidator() {

        return validator;
    }

    public ExecutionHistory
    getHistory() {

        return history;
    }

    public int getMaxAttempts() {
        return maxAttempts;
    }

    public void setMaxAttempts(
            int maxAttempts) {

        if (maxAttempts < 1) {

            maxAttempts = 1;
        }

        /*
         * Never allow an uncontrolled retry loop.
         */
        if (maxAttempts > 3) {

            maxAttempts = 3;
        }

        this.maxAttempts =
                maxAttempts;
    }
}
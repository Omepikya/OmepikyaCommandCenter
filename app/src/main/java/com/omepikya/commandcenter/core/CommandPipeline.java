package com.omepikya.commandcenter.core;

import com.omepikya.commandcenter.automation.WorkflowEngine;
import com.omepikya.commandcenter.planning.CommandPlan;
import com.omepikya.commandcenter.planning.CommandStep;
import com.omepikya.commandcenter.planning.IntelligentPlanner;

import java.util.ArrayList;
import java.util.List;

/**
 * Owns top-level command orchestration and bounded plan execution.
 *
 * CommandBrain remains the public facade while this component keeps
 * workflow lookup and multi-step orchestration out of the facade.
 */
public final class CommandPipeline {

    public interface SingleCommandExecutor {
        CommandResult execute(String command, boolean confirmed);
    }

    private final IntelligentPlanner intelligentPlanner;
    private final WorkflowEngine workflowEngine;
    private final SingleCommandExecutor singleCommandExecutor;

    public CommandPipeline(
            IntelligentPlanner intelligentPlanner,
            WorkflowEngine workflowEngine,
            SingleCommandExecutor singleCommandExecutor) {

        if (intelligentPlanner == null) {
            throw new IllegalArgumentException(
                    "IntelligentPlanner cannot be null");
        }

        if (workflowEngine == null) {
            throw new IllegalArgumentException(
                    "WorkflowEngine cannot be null");
        }

        if (singleCommandExecutor == null) {
            throw new IllegalArgumentException(
                    "SingleCommandExecutor cannot be null");
        }

        this.intelligentPlanner = intelligentPlanner;
        this.workflowEngine = workflowEngine;
        this.singleCommandExecutor = singleCommandExecutor;
    }

    public CommandResult executeTopLevel(
            String command,
            boolean allowPlan,
            boolean confirmed) {

        String workflowName =
                CommandBrainSupport.extractWorkflowInvocation(command);

        if (workflowName != null) {
            List<String> workflow =
                    workflowEngine.load(workflowName);

            if (!workflow.isEmpty()) {
                CommandPlan workflowPlan =
                        new CommandPlan();

                for (String step : workflow) {
                    workflowPlan.add(step);
                }

                return executePlan(workflowPlan);
            }
        }

        if (allowPlan) {
            CommandPlan plan =
                    intelligentPlanner.plan(command);

            if (intelligentPlanner.isValid(plan) &&
                    plan.getSteps().size() > 1) {
                return executePlan(plan);
            }
        }

        return singleCommandExecutor.execute(
                command,
                confirmed);
    }

    public CommandResult executePlan(
            CommandPlan plan) {

        if (!intelligentPlanner.isValid(plan)) {
            return CommandResult.failure(
                    "Command plan is invalid.");
        }

        List<String> messages =
                new ArrayList<>();

        while (plan.hasNext()) {
            CommandStep step =
                    plan.next();

            if (step == null ||
                    step.getCommand() == null ||
                    step.getCommand().trim().isEmpty()) {

                return CommandResult.failure(
                        CommandBrainSupport.join(
                                messages,
                                "A command step is invalid."));
            }

            step.incrementAttempts();

            CommandResult result =
                    singleCommandExecutor.execute(
                            step.getCommand(),
                            false);

            if (result == null) {
                return CommandResult.failure(
                        CommandBrainSupport.join(
                                messages,
                                "A command step returned no result."));
            }

            step.setResult(result.getMessage());

            if (!result.isSuccess()) {
                return CommandResult.failure(
                        CommandBrainSupport.join(
                                messages,
                                result.getMessage()));
            }

            step.setCompleted(true);

            if (result.getMessage() != null &&
                    !result.getMessage().trim().isEmpty()) {
                messages.add(
                        result.getMessage().trim());
            }

            plan.advance();
        }

        if (messages.isEmpty()) {
            return CommandResult.success(
                    "Command completed.");
        }

        return CommandResult.success(
                String.join("\n", messages));
    }
}

package com.omepikya.commandcenter.core;

import android.content.Context;

import com.omepikya.commandcenter.autonomous.AutonomousEngine;
import com.omepikya.commandcenter.autonomous.AutonomousExecutor;
import com.omepikya.commandcenter.autonomous.AutonomousControlCenter;
import com.omepikya.commandcenter.automation.WorkflowEngine;
import com.omepikya.commandcenter.context.AdvancedContextManager;
import com.omepikya.commandcenter.context.ContextEngine;
import com.omepikya.commandcenter.execution.ActionExecutor;
import com.omepikya.commandcenter.execution.ExecutionCoordinator;
import com.omepikya.commandcenter.execution.ExecutionHistory;
import com.omepikya.commandcenter.execution.ExecutionResult;
import com.omepikya.commandcenter.execution.ExecutionTrace;
import com.omepikya.commandcenter.execution.RecoveryEngine;
import com.omepikya.commandcenter.intelligence.AdaptiveLearning;
import com.omepikya.commandcenter.intelligence.BehaviorLearner;
import com.omepikya.commandcenter.intelligence.ConfidenceEngine;
import com.omepikya.commandcenter.intelligence.EntityResolver;
import com.omepikya.commandcenter.intelligence.FollowUpEngine;
import com.omepikya.commandcenter.intelligence.IntentIntelligence;
import com.omepikya.commandcenter.intelligence.NaturalLanguageNormalizer;
import com.omepikya.commandcenter.intelligence.ProactiveIntelligence;
import com.omepikya.commandcenter.memory.ConversationMemory;
import com.omepikya.commandcenter.memory.MemoryManager;
import com.omepikya.commandcenter.memory.PreferenceMemory;
import com.omepikya.commandcenter.nlp.EntityExtractor;
import com.omepikya.commandcenter.nlp.Intent;
import com.omepikya.commandcenter.nlp.IntentParser;
import com.omepikya.commandcenter.planning.ActionPlanner;
import com.omepikya.commandcenter.planning.CommandPlan;
import com.omepikya.commandcenter.planning.CommandStep;
import com.omepikya.commandcenter.planning.IntelligentPlanner;
import com.omepikya.commandcenter.plugins.PluginManager;
import com.omepikya.commandcenter.router.ActionRegistry;
import com.omepikya.commandcenter.router.ActionRouter;
import com.omepikya.commandcenter.security.IntelligenceSafety;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Omepikya Command Brain.
 *
 * Phase 7 unified pipeline:
 *
 * input
 * -> normalize
 * -> alias resolution
 * -> intent
 * -> context
 * -> planning
 * -> safety
 * -> execute
 * -> bounded recovery
 * -> verify
 * -> learn
 *
 * Phase 8 extends this with:
 *
 * autonomous goal
 * -> goal decomposition
 * -> autonomous planning
 * -> CommandBrain execution
 * -> verification
 * -> recovery / replanning
 * -> persistence
 * -> completion
 *
 * Phase 7 execution hardening adds:
 *
 * CommandBrain
 * -> ExecutionCoordinator
 * -> ActionExecutor
 * -> ActionRouter
 * -> verification
 * -> RecoveryEngine
 * -> ExecutionTrace
 */
public class CommandBrain {

    private final ActionRouter actionRouter;

    private final ActionExecutor actionExecutor;

    private final PluginManager pluginManager;

    private final BehaviorLearner behaviorLearner;

    private final IntentParser intentParser =
            new IntentParser();

    private final EntityExtractor entityExtractor =
            new EntityExtractor();

    private final MemoryManager memoryManager;

    private final ConversationMemory conversationMemory =
            new ConversationMemory();

    private final PreferenceMemory preferenceMemory;

    private final ContextEngine contextEngine;

    private final AdvancedContextManager advancedContext;

    private final ConfidenceEngine confidenceEngine =
            new ConfidenceEngine();

    private final EntityResolver entityResolver;

    private final FollowUpEngine followUpEngine =
            new FollowUpEngine();

    private final NaturalLanguageNormalizer normalizer =
            new NaturalLanguageNormalizer();

    private final ActionPlanner planner =
            new ActionPlanner();

    private final IntelligentPlanner intelligentPlanner;

    private final IntentIntelligence intentIntelligence;

    private final AdaptiveLearning adaptiveLearning;

    private final ProactiveIntelligence proactiveIntelligence;

    private final WorkflowEngine workflowEngine;

    private final IntelligenceSafety intelligenceSafety =
            new IntelligenceSafety();

    private final RecoveryEngine recoveryEngine =
            new RecoveryEngine();

    /**
     * Unified execution lifecycle coordinator.
     *
     * Keeps execution, recovery and tracing behind
     * one boundary.
     */
    private final ExecutionCoordinator executionCoordinator;

    /**
     * Phase 9 autonomous control and diagnostics layer.
     * Reuses the ExecutionCoordinator event bus so there is
     * only one execution lifecycle stream.
     */
    private final AutonomousControlCenter autonomousControlCenter;

    /**
     * Phase 8 autonomous engine.
     *
     * This layer sits above the existing Phase 7 pipeline.
     * Every autonomous step is routed back through process().
     */
    private final AutonomousEngine autonomousEngine;

    private String pendingCommand;

    private Intent pendingIntent;

    private boolean pendingSafetyConfirmation;

    private CommandResult lastResult;

    private String lastCommand;

    public CommandBrain(
            Context context,
            ActionRegistry actionRegistry) {

        if (context == null) {

            throw new IllegalArgumentException(
                    "Context cannot be null");
        }

        if (actionRegistry == null) {

            throw new IllegalArgumentException(
                    "ActionRegistry cannot be null");
        }

        memoryManager =
                new MemoryManager(
                        context);

        preferenceMemory =
                new PreferenceMemory(
                        context);

        contextEngine =
                new ContextEngine(
                        context);

        advancedContext =
                new AdvancedContextManager(
                        contextEngine);

        entityResolver =
                new EntityResolver(
                        contextEngine);

        behaviorLearner =
                new BehaviorLearner(
                        context);

        pluginManager =
                new PluginManager(
                        context);

        actionRouter =
                new ActionRouter(
                        actionRegistry,
                        pluginManager);

        actionExecutor =
                new ActionExecutor(
                        context,
                        actionRouter);

        /*
         * Unified execution layer.
         */
        executionCoordinator =
                new ExecutionCoordinator(
                        actionExecutor,
                        recoveryEngine);

        /*
         * Phase 9:
         * Connect the autonomous control center to the
         * existing execution event bus. This keeps the
         * Phase 8 execution pipeline intact while adding
         * centralized monitoring, recovery visibility,
         * safety state and autonomous diagnostics.
         */
        autonomousControlCenter =
                new AutonomousControlCenter(
                        context,
                        executionCoordinator.getEventBus());

        intentIntelligence =
                new IntentIntelligence(
                        intentParser);

        intelligentPlanner =
                new IntelligentPlanner(
                        planner);

        adaptiveLearning =
                new AdaptiveLearning(
                        context);

        proactiveIntelligence =
                new ProactiveIntelligence(
                        behaviorLearner);

        workflowEngine =
                new WorkflowEngine(
                        context);

        /*
         * Phase 8:
         * autonomous goal engine.
         */
        autonomousEngine =
                new AutonomousEngine(
                        context);
    }

    /**
     * Main command entry point.
     */
    public synchronized CommandResult process(
            String rawCommand) {

        if (rawCommand == null ||
                rawCommand.trim().isEmpty()) {

            return CommandResult.failure(
                    "Command cannot be empty");
        }

        String input =
                normalizer.normalize(
                        rawCommand);

        FollowUpEngine.Reply reply =
                followUpEngine.classify(
                        input);

        /*
         * Handle pending confirmation.
         */
        if (pendingCommand != null &&
                pendingIntent != null) {

            if (reply.type ==
                    FollowUpEngine.Type.YES) {

                String command =
                        pendingCommand;

                boolean safety =
                        pendingSafetyConfirmation;

                clearPending();

                return executeTopLevel(
                        command,
                        true,
                        safety);
            }

            if (reply.type ==
                    FollowUpEngine.Type.NO ||
                    reply.type ==
                    FollowUpEngine.Type.CANCEL) {

                clearPending();

                contextEngine.reset();

                return CommandResult.success(
                        "Cancelled.");
            }
        }

        return executeTopLevel(
                input,
                true,
                false);
    }

    /**
     * Top-level command processing.
     */
    private CommandResult executeTopLevel(
            String command,
            boolean allowPlan,
            boolean confirmed) {

        if (contextEngine.isExpired()) {

            contextEngine.reset();
        }

        conversationMemory.addUserMessage(
                command);

        /*
         * Named workflow execution.
         */
        String workflowName =
                extractWorkflowInvocation(
                        command);

        if (workflowName != null) {

            List<String> workflow =
                    workflowEngine.load(
                            workflowName);

            if (!workflow.isEmpty()) {

                CommandPlan workflowPlan =
                        new CommandPlan();

                for (String step :
                        workflow) {

                    workflowPlan.add(
                            step);
                }

                return executePlan(
                        workflowPlan);
            }
        }

        /*
         * Multi-step planning.
         */
        if (allowPlan) {

            CommandPlan plan =
                    intelligentPlanner.plan(
                            command);

            if (intelligentPlanner.isValid(
                    plan) &&
                    plan.getSteps().size() > 1) {

                return executePlan(
                        plan);
            }
        }

        return executeSingle(
                command,
                confirmed);
    }

    /**
     * Executes a bounded multi-step plan.
     */
    private CommandResult executePlan(
            CommandPlan plan) {

        if (!intelligentPlanner.isValid(
                plan)) {

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
                    step.getCommand()
                            .trim()
                            .isEmpty()) {

                return CommandResult.failure(
                        join(
                                messages,
                                "A command step is invalid."));
            }

            step.incrementAttempts();

            CommandResult result =
                    executeSingle(
                            step.getCommand(),
                            false);

            if (result == null) {

                return CommandResult.failure(
                        join(
                                messages,
                                "A command step returned no result."));
            }

            step.setResult(
                    result.getMessage());

            if (!result.isSuccess()) {

                return CommandResult.failure(
                        join(
                                messages,
                                result.getMessage()));
            }

            step.setCompleted(
                    true);

            if (result.getMessage() != null &&
                    !result.getMessage()
                            .trim()
                            .isEmpty()) {

                messages.add(
                        result.getMessage()
                                .trim());
            }

            plan.advance();
        }

        if (messages.isEmpty()) {

            return CommandResult.success(
                    "Command completed.");
        }

        return CommandResult.success(
                String.join(
                        "\n",
                        messages));
    }

    /**
     * Executes a single command.
     */
    private CommandResult executeSingle(
            String command,
            boolean confirmed) {

        if (command == null ||
                command.trim().isEmpty()) {

            return fail(
                    "Command cannot be empty.");
        }

        /*
         * Learned aliases.
         */
        String alias =
                adaptiveLearning.resolveAlias(
                        command);

        if (alias != null &&
                !alias.equalsIgnoreCase(
                        command)) {

            command =
                    alias;
        }

        /*
         * Resolve contextual references.
         */
        String contextual =
                resolvePronouns(
                        command);

        contextEngine.setLastCommand(
                contextual);

        advancedContext.observeCommand(
                contextual);

        /*
         * Intent intelligence.
         */
        Intent intent =
                intentIntelligence.interpret(
                        contextual);

        if (!intentIntelligence.isUsable(
                intent)) {

            return fail(
                    "I don't understand that command yet.");
        }

        contextEngine.setLastIntent(
                intent.getCommandType()
                        .name());

        advancedContext.observeIntent(
                intent.getCommandType()
                        .name(),
                intent.getConfidence());

        /*
         * Confidence gate.
         */
        ConfidenceEngine.Decision
                confidenceDecision =
                confidenceEngine.decide(
                        intent);

        if (confidenceDecision ==
                ConfidenceEngine.Decision.CLARIFY) {

            return fail(
                    "I'm not confident enough to execute that. " +
                    "Please rephrase it.");
        }

        /*
         * Safety gate.
         */
        IntelligenceSafety.Decision safety =
                intelligenceSafety.evaluate(
                        contextual,
                        intent.getCommandType(),
                        confirmed);

        if (!safety.isAllowed()) {

            pendingCommand =
                    contextual;

            pendingIntent =
                    intent;

            pendingSafetyConfirmation =
                    true;

            contextEngine.waiting();

            String reason =
                    safety.getReason();

            if (reason == null ||
                    reason.trim().isEmpty()) {

                reason =
                        "This action needs confirmation.";
            }

            return CommandResult.failure(
                    "Confirmation required: " +
                    reason +
                    " Please say yes or no.");
        }

        /*
         * Confidence confirmation.
         */
        if (confidenceDecision ==
                ConfidenceEngine.Decision.CONFIRM &&
                !confirmed) {

            pendingCommand =
                    contextual;

            pendingIntent =
                    intent;

            pendingSafetyConfirmation =
                    false;

            contextEngine.waiting();

            return CommandResult.failure(
                    "I understood this as: " +
                    describe(intent) +
                    ". Please say yes or no.");
        }

        /*
         * Build execution context.
         */
        CommandContext context =
                new CommandContext(
                        contextual,
                        intent.getCommandType());

        addEntitiesToContext(
                context,
                intent);

        context.setParameter(
                "original_command",
                command);

        context.setParameter(
                "intent_confidence",
                String.valueOf(
                        intent.getConfidence()));

        context.setParameter(
                "plugin_runtime",
                "enabled");

        context.setParameter(
                "plugin_count",
                String.valueOf(
                        pluginManager
                                .getPluginCount()));

        context.setParameter(
                "phase7",
                "enabled");

        context.setParameter(
                "phase8",
                "enabled");

        context.setParameter(
                "execution_coordinator",
                "enabled");

        contextEngine.processing();

        for (Map.Entry<String, String> entry :
                context.getParameters()
                        .entrySet()) {

            contextEngine.put(
                    entry.getKey(),
                    entry.getValue());
        }

        ExecutionResult executionResult;

        try {

            /*
             * Unified Phase 7 execution lifecycle:
             *
             * execution
             * -> verification
             * -> recovery
             * -> final result
             */
            executionResult =
                    executionCoordinator.execute(
                            context);

        } catch (Exception e) {

            return fail(
                    "Action execution failed.");
        }

        if (executionResult == null) {

            return fail(
                    "No execution result returned.");
        }

        CommandResult result =
                executionResult
                        .getCommandResult();

        if (result == null) {

            if (executionResult.isSuccess()) {

                result =
                        CommandResult.success(
                                executionResult
                                        .getMessage());

            } else {

                result =
                        CommandResult.failure(
                                executionResult
                                        .getMessage());
            }
        }

        lastResult =
                result;

        lastCommand =
                contextual;

        contextEngine.setLastResult(
                executionResult.isSuccess(),
                result.getMessage());

        contextEngine.setLastAction(
                intent.getCommandType()
                        .name());

        /*
         * Adaptive learning.
         */
        if (executionResult.isSuccess()) {

            behaviorLearner.record(
                    contextual);

            adaptiveLearning.recordSuccess(
                    contextual);

            conversationMemory
                    .addAssistantMessage(
                            result.getMessage() == null
                                    ? "Done."
                                    : result.getMessage());

            contextEngine.complete();

        } else {

            contextEngine.update(
                    context);
        }

        return result;
    }

    /**
     * Detect:
     *
     * run workflow <name>
     */
    private String extractWorkflowInvocation(
            String command) {

        if (command == null) {

            return null;
        }

        String lower =
                command.toLowerCase(
                        Locale.US)
                        .trim();

        String prefix =
                "run workflow ";

        if (!lower.startsWith(
                prefix)) {

            return null;
        }

        String name =
                command.trim()
                        .substring(
                                prefix.length())
                        .trim();

        return name.isEmpty()
                ? null
                : name;
    }

    /**
     * Basic contextual pronoun resolution.
     */
    private String resolvePronouns(
            String command) {

        if (command == null) {

            return "";
        }

        String s =
                command.trim();

        String person =
                contextEngine.get(
                        "person");

        if (person == null ||
                person.trim().isEmpty()) {

            return s;
        }

        String lower =
                s.toLowerCase(
                        Locale.US);

        String target;

        if (lower.startsWith(
                "call ")) {

            target =
                    lower.substring(
                            5)
                            .trim();

        } else if (lower.startsWith(
                "message ")) {

            target =
                    lower.substring(
                            8)
                            .trim();

        } else if (lower.startsWith(
                "text ")) {

            target =
                    lower.substring(
                            5)
                            .trim();

        } else {

            target =
                    lower;
        }

        String resolved =
                entityResolver.resolvePerson(
                        target,
                        person,
                        null);

        if (resolved == null ||
                resolved.trim().isEmpty()) {

            resolved =
                    person;
        }

        if (lower.equals(
                "call him") ||
                lower.equals(
                        "call her") ||
                lower.equals(
                        "call them")) {

            return "call " +
                    resolved;
        }

        if (lower.startsWith(
                "message him ") ||
                lower.startsWith(
                        "message her ") ||
                lower.startsWith(
                        "message them ")) {

            return "message " +
                    resolved +
                    tailAfterPronoun(
                            s);
        }

        if (lower.startsWith(
                "text him ") ||
                lower.startsWith(
                        "text her ") ||
                lower.startsWith(
                        "text them ")) {

            return "text " +
                    resolved +
                    tailAfterPronoun(
                            s);
        }

        return s;
    }

    private String tailAfterPronoun(
            String s) {

        int first =
                s.indexOf(' ');

        if (first < 0) {

            return "";
        }

        String rest =
                s.substring(
                        first + 1)
                        .trim();

        int second =
                rest.indexOf(' ');

        if (second < 0) {

            return "";
        }

        return " " +
                rest.substring(
                        second + 1)
                        .trim();
    }

    /**
     * Copy intent entities into execution/context memory.
     */
    private void addEntitiesToContext(
            CommandContext context,
            Intent intent) {

        if (context == null ||
                intent == null ||
                intent.getEntities() == null) {

            return;
        }

        for (Map.Entry<String, String> entry :
                intent.getEntities()
                        .entrySet()) {

            if (entry == null ||
                    entry.getKey() == null ||
                    entry.getKey()
                            .trim()
                            .isEmpty()) {

                continue;
            }

            context.setParameter(
                    entry.getKey(),
                    entry.getValue());

            contextEngine.put(
                    entry.getKey(),
                    entry.getValue());

            if ("person".equalsIgnoreCase(
                    entry.getKey())) {

                contextEngine.setLastEntity(
                        entry.getValue());
            }
        }
    }

    private String describe(
            Intent intent) {

        if (intent == null ||
                intent.getCommandType() == null) {

            return "an unknown command";
        }

        StringBuilder b =
                new StringBuilder(
                        intent.getCommandType()
                                .name()
                                .toLowerCase(
                                        Locale.US)
                                .replace(
                                        '_',
                                        ' '));

        String person =
                intent.getEntity(
                        "person");

        String app =
                intent.getEntity(
                        "app");

        if (person != null &&
                !person.isEmpty()) {

            b.append(
                    " for ")
                    .append(
                            person);

        } else if (app != null &&
                !app.isEmpty()) {

            b.append(' ')
                    .append(
                            app);
        }

        return b.toString();
    }

    private String join(
            List<String> messages,
            String failure) {

        String f =
                failure == null ||
                        failure.trim().isEmpty()
                        ? "Command failed."
                        : failure;

        if (messages == null ||
                messages.isEmpty()) {

            return f;
        }

        return String.join(
                "\n",
                messages) +
                "\n" +
                f;
    }

    private CommandResult fail(
            String message) {

        String safe =
                message == null ||
                        message.trim().isEmpty()
                        ? "Command failed."
                        : message;

        lastResult =
                CommandResult.failure(
                        safe);

        contextEngine.setLastResult(
                false,
                safe);

        contextEngine.update(
                "last_error",
                safe);

        return lastResult;
    }

    /**
     * Returns true when the brain is waiting for the user to confirm
     * the last interpreted command.
     */
    public synchronized boolean isAwaitingConfirmation() {
        return pendingCommand != null && pendingIntent != null;
    }

    private void clearPending() {

        pendingCommand =
                null;

        pendingIntent =
                null;

        pendingSafetyConfirmation =
                false;
    }

    public synchronized void resetContext() {

        clearPending();

        contextEngine.reset();

        advancedContext.clear();
    }

    /*
     * ============================================================
     * PHASE 8
     * AUTONOMOUS COMMAND CENTER API
     * ============================================================
     */

    /**
     * Execute a natural-language goal as an
     * autonomous multi-step task.
     *
     * AutonomousEngine decomposes the goal and
     * routes every generated step back through
     * this CommandBrain.
     */
    public synchronized CommandResult executeGoal(
            String goal) {

        if (goal == null ||
                goal.trim().isEmpty()) {

            return CommandResult.failure(
                    "Autonomous goal cannot be empty.");
        }

        return autonomousEngine.executeGoal(
                goal,
                new AutonomousExecutor
                        .CommandHandler() {

                    @Override
                    public CommandResult execute(
                            String command) {

                        /*
                         * Autonomous steps do NOT bypass
                         * CommandBrain.
                         */
                        return process(
                                command);
                    }
                });
    }

    /**
     * Resume a persisted autonomous task.
     */
    public synchronized CommandResult
    resumeAutonomousTask() {

        return autonomousEngine.resume(
                new AutonomousExecutor
                        .CommandHandler() {

                    @Override
                    public CommandResult execute(
                            String command) {

                        return process(
                                command);
                    }
                });
    }

    /**
     * Cancel the active autonomous task.
     */
    public synchronized void
    cancelAutonomousTask() {

        autonomousEngine.cancel();
    }

    /**
     * Clear persisted autonomous state.
     */
    public synchronized void
    clearAutonomousTask() {

        autonomousEngine.clearSavedTask();
    }

    /**
     * Returns true when an autonomous task
     * has persisted state.
     */
    public boolean hasAutonomousTask() {

        return autonomousEngine
                .hasSavedTask();
    }

    /**
     * Returns the persisted autonomous goal.
     */
    public String getAutonomousGoal() {

        return autonomousEngine
                .getLastGoal();
    }

    /**
     * Returns the persisted autonomous step cursor.
     */
    public int getAutonomousStep() {

        return autonomousEngine
                .getLastCursor();
    }

    /**
     * Returns the persisted autonomous task status.
     */
    public String getAutonomousStatus() {

        return autonomousEngine
                .getLastStatus();
    }

    /**
     * Provides access to the Phase 8 autonomous engine.
     */
    public AutonomousEngine
    getAutonomousEngine() {

        return autonomousEngine;
    }

    /*
     * ============================================================
     * EXISTING PUBLIC API
     * ============================================================
     */

    public ContextEngine
    getContextEngine() {

        return contextEngine;
    }

    public ConversationMemory
    getConversationMemory() {

        return conversationMemory;
    }

    public MemoryManager
    getMemoryManager() {

        return memoryManager;
    }

    public PreferenceMemory
    getPreferenceMemory() {

        return preferenceMemory;
    }

    public CommandResult
    getLastResult() {

        return lastResult;
    }

    public String
    getLastCommand() {

        return lastCommand;
    }

    public BehaviorLearner
    getBehaviorLearner() {

        return behaviorLearner;
    }

    public PluginManager
    getPluginManager() {

        return pluginManager;
    }

    public ActionExecutor
    getActionExecutor() {

        return actionExecutor;
    }

    public ExecutionHistory
    getExecutionHistory() {

        return actionExecutor
                .getHistory();
    }

    public ExecutionResult
    getLastExecutionResult() {

        return actionExecutor
                .getHistory() == null
                ? null
                : actionExecutor
                        .getHistory()
                        .getLatest();
    }

    public IntelligentPlanner
    getIntelligentPlanner() {

        return intelligentPlanner;
    }

    public AdaptiveLearning
    getAdaptiveLearning() {

        return adaptiveLearning;
    }

    public WorkflowEngine
    getWorkflowEngine() {

        return workflowEngine;
    }

    public IntelligenceSafety
    getIntelligenceSafety() {

        return intelligenceSafety;
    }

    public RecoveryEngine
    getRecoveryEngine() {

        return recoveryEngine;
    }

    /**
     * Returns the Phase 9 autonomous control center.
     */
    public AutonomousControlCenter
    getAutonomousControlCenter() {

        return autonomousControlCenter;
    }

    /**
     * Returns the unified execution coordinator.
     */
    public ExecutionCoordinator
    getExecutionCoordinator() {

        return executionCoordinator;
    }

    /**
     * Returns the latest command execution trace.
     */
    public ExecutionTrace
    getLastExecutionTrace() {

        return executionCoordinator
                .getLastTrace();
    }

    public String
    getProactiveSuggestion() {

        return proactiveIntelligence
                .suggest();
    }
}
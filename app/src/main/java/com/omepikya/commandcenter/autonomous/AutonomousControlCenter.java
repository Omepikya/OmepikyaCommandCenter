package com.omepikya.commandcenter.autonomous;

import android.content.Context;

import com.omepikya.commandcenter.execution.ExecutionEvent;
import com.omepikya.commandcenter.execution.ExecutionEventBus;
import com.omepikya.commandcenter.execution.ExecutionHistory;
import com.omepikya.commandcenter.execution.ExecutionMonitor;
import com.omepikya.commandcenter.execution.ExecutionStateStore;
import com.omepikya.commandcenter.execution.ExecutionStatus;
import com.omepikya.commandcenter.execution.ExecutionTrace;
import com.omepikya.commandcenter.execution.RecoveryEngine;
import com.omepikya.commandcenter.security.SafetyDecision;
import com.omepikya.commandcenter.security.SafetyGuard;

import java.util.List;

/**
 * ============================================================
 * OMEPIKYA COMMAND CENTER
 * AUTONOMOUS CONTROL CENTER
 * ============================================================
 *
 * Phase 9K
 *
 * Central control and diagnostic surface for the existing
 * autonomous/execution architecture.
 *
 * It does not replace CommandBrain, ExecutionCoordinator,
 * AutonomousEngine or AutonomousExecutor.
 */
public final class AutonomousControlCenter {

    private final Context context;

    private final ExecutionHistory executionHistory;

    private final ExecutionStateStore stateStore;

    private final ExecutionEventBus eventBus;

    private final ExecutionMonitor monitor;

    private final RecoveryEngine recoveryEngine;

    private final PlanVerifier planVerifier;

    private final SafetyGuard safetyGuard;

    private final SafetyEscalator safetyEscalator;

    private final AutonomousPersistence autonomousPersistence;

    private ExecutionTrace latestTrace;

    public AutonomousControlCenter(
            Context context) {

        this(
                context,
                null);
    }

    /**
     * Allows the existing ExecutionCoordinator event bus
     * to be shared instead of creating a second event stream.
     */
    public AutonomousControlCenter(
            Context context,
            ExecutionEventBus eventBus) {

        if (context == null) {

            throw new IllegalArgumentException(
                    "Context cannot be null.");
        }

        this.context =
                context.getApplicationContext();

        this.executionHistory =
                new ExecutionHistory(
                        this.context);

        this.stateStore =
                new ExecutionStateStore(
                        this.context);

        this.eventBus =
                eventBus == null
                        ? new ExecutionEventBus()
                        : eventBus;

        this.monitor =
                new ExecutionMonitor();

        this.recoveryEngine =
                new RecoveryEngine();

        this.planVerifier =
                new PlanVerifier();

        this.safetyGuard =
                new SafetyGuard();

        this.safetyEscalator =
                new SafetyEscalator();

        this.autonomousPersistence =
                new AutonomousPersistence(
                        this.context);

        this.eventBus.subscribe(
                monitor);
    }

    public Context getContext() {

        return context;
    }

    public ExecutionHistory
    getExecutionHistory() {

        return executionHistory;
    }

    public ExecutionStateStore
    getStateStore() {

        return stateStore;
    }

    public ExecutionEventBus
    getEventBus() {

        return eventBus;
    }

    public ExecutionMonitor
    getMonitor() {

        return monitor;
    }

    public RecoveryEngine
    getRecoveryEngine() {

        return recoveryEngine;
    }

    public PlanVerifier
    getPlanVerifier() {

        return planVerifier;
    }

    public SafetyGuard
    getSafetyGuard() {

        return safetyGuard;
    }

    public SafetyEscalator
    getSafetyEscalator() {

        return safetyEscalator;
    }

    public AutonomousPersistence
    getAutonomousPersistence() {

        return autonomousPersistence;
    }

    public synchronized void registerTrace(
            ExecutionTrace trace) {

        if (trace == null) {
            return;
        }

        latestTrace = trace;

        monitor.start(
                trace.getExecutionId(),
                trace.getCommand(),
                0);

        monitor.setState(
                trace.getState().name());

        publish(
                trace.getExecutionId(),
                "TRACE_REGISTERED",
                trace.getCommand());
    }

    public synchronized void syncTrace(
            ExecutionTrace trace) {

        if (trace == null) {
            return;
        }

        latestTrace = trace;

        monitor.setState(
                trace.getState().name());

        monitor.updateAttempts(
                trace.getAttemptCount());

        if (trace.getFailure() != null) {

            monitor.recordFailure(
                    trace.getFailure()
                            .getReason());
        }

        if (trace.getState() ==
                ExecutionStatus.SUCCESS) {

            monitor.finish(
                    true,
                    "Execution completed.");
        }

        if (trace.getState() ==
                ExecutionStatus.FAILED) {

            monitor.finish(
                    false,
                    trace.getFailure() == null
                            ? "Execution failed."
                            : trace.getFailure()
                                    .getReason());
        }

        if (trace.getState() ==
                ExecutionStatus.CANCELLED) {

            monitor.cancel(
                    trace.getFailure() == null
                            ? "Execution cancelled."
                            : trace.getFailure()
                                    .getReason());

            monitor.finish(
                    false,
                    "Execution cancelled.");
        }
    }

    public synchronized ExecutionTrace
    getLatestTrace() {

        return latestTrace;
    }

    /**
     * Phase 9G crash-recovery detection.
     */
    public boolean
    hasRecoverableExecution() {

        ExecutionStateStore.Snapshot snapshot =
                stateStore.load();

        if (snapshot == null) {
            return false;
        }

        String id =
                snapshot.getExecutionId();

        if (id == null ||
                id.trim().isEmpty()) {

            return false;
        }

        try {

            ExecutionStatus status =
                    ExecutionStatus.valueOf(
                            snapshot.getState());

            return status ==
                            ExecutionStatus.RUNNING ||

                    status ==
                            ExecutionStatus.RECOVERING ||

                    status ==
                            ExecutionStatus.PENDING;

        } catch (Exception ignored) {

            return false;
        }
    }

    public ExecutionStateStore.Snapshot
    getRecoverySnapshot() {

        return stateStore.load();
    }

    /**
     * Phase 9J safety evaluation.
     */
    public SafetyDecision
    evaluateSafety(
            String command) {

        return safetyGuard.check(
                command);
    }

    public boolean
    canExecuteAutonomously(
            String command) {

        SafetyDecision decision =
                evaluateSafety(command);

        return decision != null &&
                decision.isAllowed() &&
                !decision.requiresConfirmation();
    }

    /**
     * Phase 9D plan verification.
     */
    public boolean
    verifyPlan(
            AutonomousPlan plan) {

        return planVerifier.validate(
                plan);
    }

    public boolean
    verifyStep(
            AutonomousStep step,
            boolean success,
            String message) {

        return planVerifier.verifyStep(
                step,
                success,
                message);
    }

    public boolean
    verifyCompletion(
            AutonomousPlan plan) {

        return planVerifier.verifyComplete(
                plan);
    }

    public void publish(
            String executionId,
            String type,
            String message) {

        eventBus.publish(
                executionId,
                type,
                message);
    }

    public void publish(
            String type,
            String message) {

        String executionId = "";

        ExecutionStateStore.Snapshot snapshot =
                stateStore.load();

        if (snapshot != null) {

            executionId =
                    snapshot.getExecutionId();
        }

        eventBus.publish(
                executionId,
                type,
                message);
    }

    /**
     * Phase 9H cancellation request.
     *
     * The actual cancellation is still handled by
     * ExecutionCancellation / ExecutionCoordinator.
     */
    public void requestCancellation(
            String reason) {

        String safe =
                reason == null ||
                        reason.trim().isEmpty()
                        ? "Cancellation requested."
                        : reason.trim();

        monitor.cancel(safe);

        publish(
                "CANCEL_REQUESTED",
                safe);
    }

    /**
     * Phase 9J safety hold.
     */
    public void requestSafetyHold(
            String reason) {

        String safe =
                reason == null ||
                        reason.trim().isEmpty()
                        ? "Execution placed on safety hold."
                        : reason.trim();

        monitor.safetyHold(
                safe);

        publish(
                "SAFETY_HOLD",
                safe);
    }

    public void recoveryStarted(
            String executionId,
            String reason) {

        monitor.recordRecovery();

        publish(
                executionId,
                "RECOVERY_STARTED",
                reason);
    }

    public void replanStarted(
            String executionId,
            String reason) {

        monitor.setState(
                "REPLANNING");

        publish(
                executionId,
                "REPLAN_STARTED",
                reason);
    }

    public List<ExecutionEvent>
    getRecentEvents(
            int count) {

        return eventBus.getRecentEvents(
                count);
    }

    public List<com.omepikya.commandcenter.execution.ExecutionResult>
    getHistory() {

        return executionHistory.getAll();
    }

    public com.omepikya.commandcenter.execution.ExecutionResult
    getLatestExecution() {

        return executionHistory.getLatest();
    }

    public List<com.omepikya.commandcenter.execution.ExecutionResult>
    searchHistory(
            String query,
            int limit) {

        return executionHistory.search(
                query,
                limit);
    }

    public synchronized String
    getStatusSummary() {

        StringBuilder builder =
                new StringBuilder();

        builder.append(
                "OMEPIKYA AUTONOMOUS STATUS\n");

        builder.append(
                "State: ")
                .append(
                        monitor.getState())
                .append('\n');

        builder.append(
                "Execution ID: ")
                .append(
                        monitor.getExecutionId())
                .append('\n');

        builder.append(
                "Command: ")
                .append(
                        monitor.getCommand())
                .append('\n');

        builder.append(
                "Current Step: ")
                .append(
                        monitor.getCurrentStep())
                .append('\n');

        builder.append(
                "Progress: ")
                .append(
                        Math.round(
                                monitor.getProgress()
                                        * 100.0))
                .append('%')
                .append('\n');

        builder.append(
                "Attempts: ")
                .append(
                        monitor.getAttempts())
                .append('\n');

        builder.append(
                "Recovery Attempts: ")
                .append(
                        monitor
                                .getRecoveryAttempts())
                .append('\n');

        builder.append(
                "Elapsed: ")
                .append(
                        monitor.getElapsedMs())
                .append(" ms\n");

        builder.append(
                "Active: ")
                .append(
                        monitor.isActive())
                .append('\n');

        builder.append(
                "Cancelled: ")
                .append(
                        monitor.isCancelled())
                .append('\n');

        builder.append(
                "Safety Hold: ")
                .append(
                        monitor.isSafetyHold())
                .append('\n');

        if (!monitor.getLastFailure()
                .isEmpty()) {

            builder.append(
                    "Last Failure: ")
                    .append(
                            monitor.getLastFailure())
                    .append('\n');
        }

        return builder.toString();
    }

    public synchronized ControlSnapshot
    getSnapshot() {

        return new ControlSnapshot(
                monitor.getExecutionId(),
                monitor.getCommand(),
                monitor.getCurrentStep(),
                monitor.getState(),
                monitor.getProgress(),
                monitor.getAttempts(),
                monitor.getRecoveryAttempts(),
                monitor.getElapsedMs(),
                monitor.isActive(),
                monitor.isPaused(),
                monitor.isCancelled(),
                monitor.isSafetyHold(),
                monitor.getLastMessage(),
                monitor.getLastFailure());
    }

    public void shutdown() {

        eventBus.unsubscribe(
                monitor);

        monitor.reset();
    }

    public static final class ControlSnapshot {

        private final String executionId;
        private final String command;
        private final String currentStep;
        private final String state;
        private final double progress;
        private final int attempts;
        private final int recoveryAttempts;
        private final long elapsedMs;
        private final boolean active;
        private final boolean paused;
        private final boolean cancelled;
        private final boolean safetyHold;
        private final String lastMessage;
        private final String lastFailure;

        private ControlSnapshot(
                String executionId,
                String command,
                String currentStep,
                String state,
                double progress,
                int attempts,
                int recoveryAttempts,
                long elapsedMs,
                boolean active,
                boolean paused,
                boolean cancelled,
                boolean safetyHold,
                String lastMessage,
                String lastFailure) {

            this.executionId = executionId;
            this.command = command;
            this.currentStep = currentStep;
            this.state = state;
            this.progress = progress;
            this.attempts = attempts;
            this.recoveryAttempts =
                    recoveryAttempts;
            this.elapsedMs = elapsedMs;
            this.active = active;
            this.paused = paused;
            this.cancelled = cancelled;
            this.safetyHold = safetyHold;
            this.lastMessage = lastMessage;
            this.lastFailure = lastFailure;
        }

        public String getExecutionId() {
            return executionId;
        }

        public String getCommand() {
            return command;
        }

        public String getCurrentStep() {
            return currentStep;
        }

        public String getState() {
            return state;
        }

        public double getProgress() {
            return progress;
        }

        public int getAttempts() {
            return attempts;
        }

        public int getRecoveryAttempts() {
            return recoveryAttempts;
        }

        public long getElapsedMs() {
            return elapsedMs;
        }

        public boolean isActive() {
            return active;
        }

        public boolean isPaused() {
            return paused;
        }

        public boolean isCancelled() {
            return cancelled;
        }

        public boolean isSafetyHold() {
            return safetyHold;
        }

        public String getLastMessage() {
            return lastMessage;
        }

        public String getLastFailure() {
            return lastFailure;
        }
    }
}
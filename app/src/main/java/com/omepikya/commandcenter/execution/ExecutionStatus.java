package com.omepikya.commandcenter.execution;

/**
 * Lifecycle state of a command execution.
 */
public enum ExecutionStatus {

    PENDING,

    RUNNING,

    RECOVERING,

    SUCCESS,

    FAILED,

    CANCELLED
}
package com.omepikya.commandcenter.intelligence;

import com.omepikya.commandcenter.core.CommandType;
import com.omepikya.commandcenter.nlp.Intent;

public class ConfidenceEngine {
    public enum Decision { EXECUTE, CONFIRM, CLARIFY }
    private double executeThreshold = 0.90;
    private double confirmThreshold = 0.70;

    public ConfidenceEngine() {}
    public ConfidenceEngine(double executeThreshold, double confirmThreshold) {
        this.executeThreshold = executeThreshold;
        this.confirmThreshold = confirmThreshold;
    }

    public Decision decide(Intent intent) {
        if (intent == null || intent.getCommandType() == null || intent.getCommandType() == CommandType.UNKNOWN) return Decision.CLARIFY;
        double c = intent.getConfidence();
        if (c >= executeThreshold) return Decision.EXECUTE;
        if (c >= confirmThreshold) return Decision.CONFIRM;
        return Decision.CLARIFY;
    }
    public double getExecuteThreshold() { return executeThreshold; }
    public double getConfirmThreshold() { return confirmThreshold; }
}

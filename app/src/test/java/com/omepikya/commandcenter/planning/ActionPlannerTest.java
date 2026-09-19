package com.omepikya.commandcenter.planning;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ActionPlannerTest {

    private final ActionPlanner planner = new ActionPlanner();

    @Test
    public void splitsAndThenCommands() {
        CommandPlan plan = planner.plan(
                "open whatsapp and then message vivek");

        assertEquals(2, plan.getSteps().size());
        assertEquals("open whatsapp",
                plan.getSteps().get(0).getCommand());
        assertEquals("message vivek",
                plan.getSteps().get(1).getCommand());
    }

    @Test
    public void splitsSemicolonCommands() {
        CommandPlan plan = planner.plan(
                "open settings; turn on wifi");

        assertEquals(2, plan.getSteps().size());
    }

    @Test
    public void preservesMalformedSeparatorAsSingleCommand() {
        CommandPlan plan = planner.plan(
                "open whatsapp and then");

        assertEquals(1, plan.getSteps().size());
        assertEquals(
                "open whatsapp and then",
                plan.getSteps().get(0).getCommand());
    }

    @Test
    public void ignoresEmptyInput() {
        assertTrue(planner.plan("   ").getSteps().isEmpty());
    }
}

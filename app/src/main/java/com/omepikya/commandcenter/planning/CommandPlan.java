package com.omepikya.commandcenter.planning;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CommandPlan {
    private final List<CommandStep> steps=new ArrayList<>();
    private int currentIndex;
    private boolean cancelled;
    public void add(String command){if(command!=null&&!command.trim().isEmpty())steps.add(new CommandStep(command.trim()));}
    public List<CommandStep> getSteps(){return Collections.unmodifiableList(steps);}
    public int getCurrentIndex(){return currentIndex;}
    public boolean hasNext(){return !cancelled&&currentIndex<steps.size();}
    public CommandStep next(){return hasNext()?steps.get(currentIndex):null;}
    public void advance(){if(currentIndex<steps.size())currentIndex++;}
    public void cancel(){cancelled=true;}
    public boolean isCancelled(){return cancelled;}
    public boolean isComplete(){return !cancelled&&currentIndex>=steps.size();}
}

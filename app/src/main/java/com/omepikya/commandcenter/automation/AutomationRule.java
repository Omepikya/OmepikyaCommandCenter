package com.omepikya.commandcenter.automation;

public class AutomationRule {
    private final String id,name,command;
    private final TriggerType triggerType;
    private final long triggerValue;
    private boolean enabled=true;
    public AutomationRule(String id,String name,String command,TriggerType triggerType,long triggerValue){this.id=id;this.name=name;this.command=command;this.triggerType=triggerType;this.triggerValue=triggerValue;}
    public String getId(){return id;} public String getName(){return name;} public String getCommand(){return command;} public TriggerType getTriggerType(){return triggerType;} public long getTriggerValue(){return triggerValue;} public boolean isEnabled(){return enabled;} public void setEnabled(boolean v){enabled=v;}
}

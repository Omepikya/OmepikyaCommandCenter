package com.omepikya.commandcenter.automation;

import android.content.Context;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/** In-memory rule engine. Scheduling remains delegated to AutomationCore/AlarmManager. */
public class AutomationEngine {
    public interface Executor { void execute(String command); }
    private final List<AutomationRule> rules=new ArrayList<>();
    private final ConditionEngine conditions;
    private Executor executor;
    public AutomationEngine(Context context){conditions=new ConditionEngine(context);}
    public void setExecutor(Executor executor){this.executor=executor;}
    public synchronized String add(String name,String command,TriggerType type,long value){if(command==null||command.trim().isEmpty()||type==null)return null;String id=UUID.randomUUID().toString();rules.add(new AutomationRule(id,name,command,type,value));return id;}
    public synchronized boolean remove(String id){return rules.removeIf(r->id!=null&&id.equals(r.getId()));}
    public synchronized List<AutomationRule> getRules(){return Collections.unmodifiableList(new ArrayList<>(rules));}
    public boolean evaluateBatteryRule(AutomationRule rule){return rule!=null&&rule.isEnabled()&&rule.getTriggerType()==TriggerType.BATTERY&&conditions.batteryBelow((int)rule.getTriggerValue());}
    public boolean evaluateWifiRule(AutomationRule rule){return rule!=null&&rule.isEnabled()&&rule.getTriggerType()==TriggerType.WIFI&&conditions.wifiConnected();}
    public void execute(AutomationRule rule){if(rule!=null&&rule.isEnabled()&&executor!=null)executor.execute(rule.getCommand());}
}

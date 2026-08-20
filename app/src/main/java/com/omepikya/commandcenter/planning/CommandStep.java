package com.omepikya.commandcenter.planning;

public class CommandStep {
    private final String command;
    private int attempts;
    private boolean completed;
    private String result;
    public CommandStep(String command){this.command=command;}
    public String getCommand(){return command;}
    public int getAttempts(){return attempts;}
    public void incrementAttempts(){attempts++;}
    public boolean isCompleted(){return completed;}
    public void setCompleted(boolean v){completed=v;}
    public String getResult(){return result;}
    public void setResult(String v){result=v;}
}

package com.omepikya.commandcenter.voice;

public class ConversationSession {
    private boolean active;
    private long lastActivity;
    private long timeoutMs=20_000L;
    public synchronized void start(){active=true;touch();}
    public synchronized void stop(){active=false;}
    public synchronized void touch(){lastActivity=System.currentTimeMillis();}
    public synchronized boolean isActive(){if(active&&System.currentTimeMillis()-lastActivity>timeoutMs)active=false;return active;}
    public synchronized void setTimeoutMs(long ms){if(ms>0)timeoutMs=ms;}
    public synchronized long getLastActivity(){return lastActivity;}
}

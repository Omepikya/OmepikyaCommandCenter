package com.omepikya.commandcenter.voice;

/** Coordinates short voice turns. It intentionally stops between turns instead of recording forever. */
public class ContinuousVoiceController {
    public interface Listener { void onTurnStarted(); void onTurnEnded(); void onSessionEnded(); }
    private final VoiceEngine engine;
    private final ConversationSession session=new ConversationSession();
    private final VoiceIntelligence intelligence=new VoiceIntelligence();
    private Listener listener;
    public ContinuousVoiceController(VoiceEngine engine){if(engine==null)throw new IllegalArgumentException("VoiceEngine cannot be null");this.engine=engine;}
    public void setListener(Listener l){listener=l;}
    public void start(){session.start(); if(listener!=null)listener.onTurnStarted(); engine.startListening();}
    public void nextTurn(){if(!session.isActive()){if(listener!=null)listener.onSessionEnded();return;}session.touch();if(listener!=null)listener.onTurnStarted();engine.startListening();}
    public void stop(){session.stop();engine.cancelListening();if(listener!=null)listener.onSessionEnded();}
    public String normalize(String transcript){session.touch();return intelligence.normalizeTranscript(transcript);}
    public boolean isActive(){return session.isActive();}
}

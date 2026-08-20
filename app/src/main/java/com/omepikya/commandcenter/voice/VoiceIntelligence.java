package com.omepikya.commandcenter.voice;

import java.util.Locale;

public class VoiceIntelligence {
    private final WakeWordDetector wakeWordDetector;
    public VoiceIntelligence(){wakeWordDetector=new WakeWordDetector();}
    public String normalizeTranscript(String text){
        if(text==null)return "";
        String s=text.trim().replaceAll("\\s+"," ");
        if(wakeWordDetector.containsWakeWord(s)) s=wakeWordDetector.removeWakeWord(s);
        return s.trim();
    }
    public boolean isWakeWord(String text){return wakeWordDetector.isWakeWord(text);}
    public WakeWordDetector getWakeWordDetector(){return wakeWordDetector;}
}

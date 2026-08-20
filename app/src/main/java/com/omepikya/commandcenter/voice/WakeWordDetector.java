package com.omepikya.commandcenter.voice;

import java.util.Locale;

/** Lightweight offline wake-word gate. It does not keep the microphone alive. */
public class WakeWordDetector {
    private final String wakeWord;
    public WakeWordDetector(){this("omepikya");}
    public WakeWordDetector(String wakeWord){this.wakeWord= wakeWord==null||wakeWord.trim().isEmpty()?"omepikya":wakeWord.trim().toLowerCase(Locale.US);}
    public boolean isWakeWord(String text){return text!=null&&text.trim().toLowerCase(Locale.US).equals(wakeWord);}
    public boolean containsWakeWord(String text){return text!=null&&text.toLowerCase(Locale.US).contains(wakeWord);}
    public String removeWakeWord(String text){if(text==null)return "";return text.replaceFirst("(?i)\\b"+java.util.regex.Pattern.quote(wakeWord)+"\\b"," ").trim();}
    public String getWakeWord(){return wakeWord;}
}

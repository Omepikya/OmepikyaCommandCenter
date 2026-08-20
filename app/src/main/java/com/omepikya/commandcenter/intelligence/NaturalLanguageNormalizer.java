package com.omepikya.commandcenter.intelligence;

import java.util.Locale;

public class NaturalLanguageNormalizer {
    public String normalize(String input) {
        if (input == null) return "";
        String s=input.trim().replaceAll("\\s+"," ");
        if (s.isEmpty()) return s;
        String lower=s.toLowerCase(Locale.US);
        String[] prefixes={"could you ","can you ","would you ","please ","kindly ","will you "};
        for(String p:prefixes) if(lower.startsWith(p)) { s=s.substring(p.length()).trim(); break; }
        s=s.replaceAll("[.?!]+$","").trim();
        lower=s.toLowerCase(Locale.US);
        if(lower.startsWith("phone ")) s="call "+s.substring(6).trim();
        else if(lower.startsWith("ring ")) s="call "+s.substring(5).trim();
        else if(lower.startsWith("launch ")) s="open "+s.substring(7).trim();
        else if(lower.startsWith("start ") && !lower.startsWith("start a ")) s="open "+s.substring(6).trim();
        return s;
    }
}

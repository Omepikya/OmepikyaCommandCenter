package com.omepikya.commandcenter.planning;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ActionPlanner {
    public CommandPlan plan(String command){
        CommandPlan p=new CommandPlan();
        if(command==null||command.trim().isEmpty()) return p;
        String remaining=command.trim();
        while(!remaining.isEmpty()){
            int idx=findSeparator(remaining);
            if(idx<0){p.add(remaining);break;}
            String left=remaining.substring(0,idx).trim();
            String right=remaining.substring(endOfSeparator(remaining,idx)).trim();
            if(left.isEmpty()||right.isEmpty()){p.add(remaining);break;}
            p.add(left); remaining=right;
        }
        return p;
    }
    private int findSeparator(String s){
        String l=s.toLowerCase(Locale.US);
        String[] seps={";"," and then "," after that "," then "," also "};
        int best=-1;
        for(String sep:seps){int i=l.indexOf(sep);if(i>=0&&(best<0||i<best))best=i;}
        return best;
    }
    private int endOfSeparator(String s,int idx){String l=s.toLowerCase(Locale.US);for(String x:new String[]{";"," and then "," after that "," then "," also "})if(l.startsWith(x,idx))return idx+x.length();return idx;}
}

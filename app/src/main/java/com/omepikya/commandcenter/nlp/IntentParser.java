package com.omepikya.commandcenter.nlp;
import com.omepikya.commandcenter.core.CommandType;import java.util.Locale;import java.util.Map;
public class IntentParser {
 private final CommandMatcher commandMatcher=new CommandMatcher();private final EntityExtractor entityExtractor=new EntityExtractor();
 public Intent parse(String command){if(command==null||command.trim().isEmpty())return new Intent(CommandType.UNKNOWN,command,null,0);String n=command.trim().replaceAll("\\s+"," ");CommandType type=commandMatcher.match(n);Map<String,String> entities=entityExtractor.extract(n);double c=confidence(type,entities,n);return new Intent(type,n,entities,c);}
 private double confidence(CommandType t,Map<String,String>e,String s){if(t==null||t==CommandType.UNKNOWN)return 0;double c=.82;if(t==CommandType.SYSTEM_SETTING)c=.96;if(e!=null&&!e.isEmpty())c+=.06;if(t==CommandType.COMMUNICATION&&e.get("person")!=null)c+=.06;if(t==CommandType.OPEN_APP&&e.get("app")!=null)c+=.05;if(s.split(" ").length<=1)c-=.15;return Math.max(0,Math.min(1,c));}
 public CommandMatcher getCommandMatcher(){return commandMatcher;}public EntityExtractor getEntityExtractor(){return entityExtractor;}
}

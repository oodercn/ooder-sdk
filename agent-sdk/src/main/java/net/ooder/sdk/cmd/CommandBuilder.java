package net.ooder.sdk.cmd;

import java.util.Map;

public interface CommandBuilder {
    
    CommandBuilder direction(CommandDirection direction);
    
    CommandBuilder protocol(String protocolType);
    
    CommandBuilder type(String commandType);
    
    CommandBuilder source(String sourceId);
    
    CommandBuilder target(String targetId);
    
    CommandBuilder payload(Map<String, Object> payload);
    
    CommandBuilder priority(int priority);
    
    CommandBuilder timeout(long timeout);
    
    CommandBuilder header(String key, Object value);
    
    Command build();
}

package net.ooder.sdk.reach;

import java.util.Map;

public interface ReachExecutor {
    
    boolean supports(String deviceType);
    
    ReachResult execute(ReachProtocol protocol);
}

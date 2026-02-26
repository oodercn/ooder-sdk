package net.ooder.sdk.reach;

public interface ReachManager {
    
    ReachResult execute(ReachProtocol protocol);
    
    void registerExecutor(String deviceType, ReachExecutor executor);
    
    void unregisterExecutor(String deviceType);
    
    void setAuthContext(ReachAuthContext authContext);
}

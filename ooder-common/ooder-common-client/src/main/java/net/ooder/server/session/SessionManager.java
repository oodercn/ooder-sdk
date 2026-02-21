package net.ooder.server.session;

import net.ooder.common.JDSException;
import net.ooder.engine.ConnectInfo;
import net.ooder.engine.JDSSessionHandle;

public interface SessionManager {
    
    JDSSessionHandle createSession(ConnectInfo info) throws JDSException;
    
    JDSSessionHandle getSession(String sessionId);
    
    void invalidateSession(String sessionId);
    
    void keepAlive(String sessionId);
    
    boolean isSessionValid(String sessionId);
    
    SessionStats getStats();
    
    void registerLifecycleListener(SessionLifecycle listener);
    
    void unregisterLifecycleListener(SessionLifecycle listener);
}

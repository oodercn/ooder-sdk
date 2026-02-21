package net.ooder.server.session;

import net.ooder.engine.ConnectInfo;
import net.ooder.engine.JDSSessionHandle;

public interface SessionLifecycle {
    
    void onCreated(JDSSessionHandle handle, ConnectInfo info);
    
    void onActivated(JDSSessionHandle handle);
    
    void onExpired(JDSSessionHandle handle);
    
    void onDestroyed(JDSSessionHandle handle);
}

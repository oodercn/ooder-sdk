package net.ooder.server.session;

import java.io.Serializable;

public class CacheStats implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private final int sessionCount;
    private final int connectInfoCount;
    private final int systemCodeCount;
    private final int connectTimeCount;
    
    public CacheStats(int sessionCount, int connectInfoCount, 
                      int systemCodeCount, int connectTimeCount) {
        this.sessionCount = sessionCount;
        this.connectInfoCount = connectInfoCount;
        this.systemCodeCount = systemCodeCount;
        this.connectTimeCount = connectTimeCount;
    }
    
    public int getSessionCount() {
        return sessionCount;
    }
    
    public int getConnectInfoCount() {
        return connectInfoCount;
    }
    
    public int getSystemCodeCount() {
        return systemCodeCount;
    }
    
    public int getConnectTimeCount() {
        return connectTimeCount;
    }
    
    public boolean isConsistent() {
        return sessionCount == connectInfoCount 
            && sessionCount == systemCodeCount 
            && sessionCount == connectTimeCount;
    }
    
    @Override
    public String toString() {
        return "CacheStats{" +
                "sessionCount=" + sessionCount +
                ", connectInfoCount=" + connectInfoCount +
                ", systemCodeCount=" + systemCodeCount +
                ", connectTimeCount=" + connectTimeCount +
                ", consistent=" + isConsistent() +
                '}';
    }
}

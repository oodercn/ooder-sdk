package net.ooder.server.session;

import java.io.Serializable;

public class SessionStats implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private final int activeSessions;
    private final int totalLogins;
    private final int totalLogouts;
    private final int expiredSessions;
    private final long avgLoginTime;
    private final long timestamp;
    
    public SessionStats(int activeSessions, int totalLogins, 
                        int totalLogouts, int expiredSessions, long avgLoginTime) {
        this.activeSessions = activeSessions;
        this.totalLogins = totalLogins;
        this.totalLogouts = totalLogouts;
        this.expiredSessions = expiredSessions;
        this.avgLoginTime = avgLoginTime;
        this.timestamp = System.currentTimeMillis();
    }
    
    public int getActiveSessions() {
        return activeSessions;
    }
    
    public int getTotalLogins() {
        return totalLogins;
    }
    
    public int getTotalLogouts() {
        return totalLogouts;
    }
    
    public int getExpiredSessions() {
        return expiredSessions;
    }
    
    public long getAvgLoginTime() {
        return avgLoginTime;
    }
    
    public long getTimestamp() {
        return timestamp;
    }
    
    public double getLogoutRate() {
        if (totalLogins == 0) {
            return 0.0;
        }
        return (double) totalLogouts / totalLogins * 100;
    }
    
    public double getExpirationRate() {
        if (totalLogins == 0) {
            return 0.0;
        }
        return (double) expiredSessions / totalLogins * 100;
    }
    
    @Override
    public String toString() {
        return "SessionStats{" +
                "activeSessions=" + activeSessions +
                ", totalLogins=" + totalLogins +
                ", totalLogouts=" + totalLogouts +
                ", expiredSessions=" + expiredSessions +
                ", avgLoginTime=" + avgLoginTime + "ms" +
                '}';
    }
}

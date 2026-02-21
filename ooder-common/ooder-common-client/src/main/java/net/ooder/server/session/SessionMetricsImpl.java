package net.ooder.server.session;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class SessionMetricsImpl implements SessionMetrics {
    
    private final AtomicInteger activeSessions = new AtomicInteger(0);
    private final AtomicInteger totalLogins = new AtomicInteger(0);
    private final AtomicInteger totalLogouts = new AtomicInteger(0);
    private final AtomicInteger expiredSessions = new AtomicInteger(0);
    private final AtomicLong totalLoginTime = new AtomicLong(0);
    
    @Override
    public void recordLogin(String userId, long duration) {
        activeSessions.incrementAndGet();
        totalLogins.incrementAndGet();
        totalLoginTime.addAndGet(duration);
    }
    
    @Override
    public void recordLogout(String sessionId) {
        int current = activeSessions.get();
        if (current > 0) {
            activeSessions.decrementAndGet();
        }
        totalLogouts.incrementAndGet();
    }
    
    @Override
    public void recordSessionExpired(String sessionId) {
        int current = activeSessions.get();
        if (current > 0) {
            activeSessions.decrementAndGet();
        }
        expiredSessions.incrementAndGet();
    }
    
    @Override
    public void recordActiveSessions(int count) {
        activeSessions.set(count);
    }
    
    @Override
    public SessionStats getStats() {
        int logins = totalLogins.get();
        long avgLoginTime = logins > 0 ? totalLoginTime.get() / logins : 0;
        
        return new SessionStats(
            activeSessions.get(),
            logins,
            totalLogouts.get(),
            expiredSessions.get(),
            avgLoginTime
        );
    }
    
    @Override
    public void reset() {
        activeSessions.set(0);
        totalLogins.set(0);
        totalLogouts.set(0);
        expiredSessions.set(0);
        totalLoginTime.set(0);
    }
    
    public int getActiveSessions() {
        return activeSessions.get();
    }
    
    public int getTotalLogins() {
        return totalLogins.get();
    }
    
    public int getTotalLogouts() {
        return totalLogouts.get();
    }
    
    public int getExpiredSessions() {
        return expiredSessions.get();
    }
}

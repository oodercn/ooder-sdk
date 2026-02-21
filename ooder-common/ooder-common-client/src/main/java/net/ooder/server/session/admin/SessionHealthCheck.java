package net.ooder.server.session.admin;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;
import java.util.HashMap;

public class SessionHealthCheck implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private String status;
    private long timestamp;
    private int activeSessionCount;
    private int totalSessionCount;
    private int expiredSessionCount;
    private long uptime;
    private long memoryUsed;
    private long memoryMax;
    private double memoryUsagePercent;
    private boolean cacheHealthy;
    private boolean sessionManagerHealthy;
    private Map<String, Object> details;
    
    public SessionHealthCheck() {
        this.timestamp = System.currentTimeMillis();
        this.details = new HashMap<String, Object>();
    }
    
    public static SessionHealthCheck healthy(int activeSessionCount, int totalSessionCount) {
        SessionHealthCheck health = new SessionHealthCheck();
        health.setStatus("UP");
        health.setActiveSessionCount(activeSessionCount);
        health.setTotalSessionCount(totalSessionCount);
        health.setCacheHealthy(true);
        health.setSessionManagerHealthy(true);
        return health;
    }
    
    public static SessionHealthCheck unhealthy(String reason) {
        SessionHealthCheck health = new SessionHealthCheck();
        health.setStatus("DOWN");
        health.setCacheHealthy(false);
        health.setSessionManagerHealthy(false);
        health.addDetail("reason", reason);
        return health;
    }
    
    public static SessionHealthCheck degraded(int activeSessionCount, String warning) {
        SessionHealthCheck health = new SessionHealthCheck();
        health.setStatus("DEGRADED");
        health.setActiveSessionCount(activeSessionCount);
        health.setCacheHealthy(true);
        health.setSessionManagerHealthy(true);
        health.addDetail("warning", warning);
        return health;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public long getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
    
    public int getActiveSessionCount() {
        return activeSessionCount;
    }
    
    public void setActiveSessionCount(int activeSessionCount) {
        this.activeSessionCount = activeSessionCount;
    }
    
    public int getTotalSessionCount() {
        return totalSessionCount;
    }
    
    public void setTotalSessionCount(int totalSessionCount) {
        this.totalSessionCount = totalSessionCount;
    }
    
    public int getExpiredSessionCount() {
        return expiredSessionCount;
    }
    
    public void setExpiredSessionCount(int expiredSessionCount) {
        this.expiredSessionCount = expiredSessionCount;
    }
    
    public long getUptime() {
        return uptime;
    }
    
    public void setUptime(long uptime) {
        this.uptime = uptime;
    }
    
    public long getMemoryUsed() {
        return memoryUsed;
    }
    
    public void setMemoryUsed(long memoryUsed) {
        this.memoryUsed = memoryUsed;
    }
    
    public long getMemoryMax() {
        return memoryMax;
    }
    
    public void setMemoryMax(long memoryMax) {
        this.memoryMax = memoryMax;
    }
    
    public double getMemoryUsagePercent() {
        return memoryUsagePercent;
    }
    
    public void setMemoryUsagePercent(double memoryUsagePercent) {
        this.memoryUsagePercent = memoryUsagePercent;
    }
    
    public boolean isCacheHealthy() {
        return cacheHealthy;
    }
    
    public void setCacheHealthy(boolean cacheHealthy) {
        this.cacheHealthy = cacheHealthy;
    }
    
    public boolean isSessionManagerHealthy() {
        return sessionManagerHealthy;
    }
    
    public void setSessionManagerHealthy(boolean sessionManagerHealthy) {
        this.sessionManagerHealthy = sessionManagerHealthy;
    }
    
    public Map<String, Object> getDetails() {
        return details;
    }
    
    public void setDetails(Map<String, Object> details) {
        this.details = details;
    }
    
    public void addDetail(String key, Object value) {
        this.details.put(key, value);
    }
    
    public boolean isHealthy() {
        return "UP".equals(status);
    }
    
    public Date getTimestampAsDate() {
        return new Date(timestamp);
    }
    
    public String getUptimeFormatted() {
        long seconds = uptime / 1000;
        long days = seconds / 86400;
        long hours = (seconds % 86400) / 3600;
        long minutes = (seconds % 3600) / 60;
        long secs = seconds % 60;
        return String.format("%dd %02dh %02dm %02ds", days, hours, minutes, secs);
    }
    
    public String getMemoryUsedFormatted() {
        return formatBytes(memoryUsed);
    }
    
    public String getMemoryMaxFormatted() {
        return formatBytes(memoryMax);
    }
    
    private String formatBytes(long bytes) {
        if (bytes < 1024) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(1024));
        char unit = "KMGTPE".charAt(exp - 1);
        return String.format("%.1f %sB", bytes / Math.pow(1024, exp), unit);
    }
    
    @Override
    public String toString() {
        return "SessionHealthCheck{" +
                "status='" + status + '\'' +
                ", activeSessionCount=" + activeSessionCount +
                ", cacheHealthy=" + cacheHealthy +
                ", sessionManagerHealthy=" + sessionManagerHealthy +
                '}';
    }
}

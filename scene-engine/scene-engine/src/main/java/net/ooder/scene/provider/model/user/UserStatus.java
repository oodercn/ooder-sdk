package net.ooder.scene.provider.model.user;

public class UserStatus {
    
    private int totalUsers;
    private int activeUsers;
    private int disabledUsers;
    private long lastUpdated;
    
    public int getTotalUsers() {
        return totalUsers;
    }
    
    public void setTotalUsers(int totalUsers) {
        this.totalUsers = totalUsers;
    }
    
    public int getActiveUsers() {
        return activeUsers;
    }
    
    public void setActiveUsers(int activeUsers) {
        this.activeUsers = activeUsers;
    }
    
    public int getDisabledUsers() {
        return disabledUsers;
    }
    
    public void setDisabledUsers(int disabledUsers) {
        this.disabledUsers = disabledUsers;
    }
    
    public long getLastUpdated() {
        return lastUpdated;
    }
    
    public void setLastUpdated(long lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
}

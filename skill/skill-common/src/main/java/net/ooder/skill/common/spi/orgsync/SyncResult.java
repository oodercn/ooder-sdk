package net.ooder.skill.common.spi.orgsync;

public class SyncResult {
    
    private boolean success;
    private int userCount;
    private int departmentCount;
    private String message;
    private long syncTime;
    
    public SyncResult() {}
    
    public static SyncResult success(int userCount, int departmentCount) {
        SyncResult r = new SyncResult();
        r.success = true;
        r.userCount = userCount;
        r.departmentCount = departmentCount;
        r.syncTime = System.currentTimeMillis();
        return r;
    }
    
    public static SyncResult failure(String message) {
        SyncResult r = new SyncResult();
        r.success = false;
        r.message = message;
        return r;
    }
    
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    public int getUserCount() { return userCount; }
    public void setUserCount(int userCount) { this.userCount = userCount; }
    public int getDepartmentCount() { return departmentCount; }
    public void setDepartmentCount(int departmentCount) { this.departmentCount = departmentCount; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public long getSyncTime() { return syncTime; }
    public void setSyncTime(long syncTime) { this.syncTime = syncTime; }
}

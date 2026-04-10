package net.ooder.skill.common.spi.bind;

public class BindInfo {
    
    private String platform;
    private String platformUserId;
    private String platformUserName;
    private String userId;
    private long bindTime;
    private BindStatus status;
    
    public BindInfo() {}
    
    public BindInfo(String platform, String userId) {
        this.platform = platform;
        this.userId = userId;
        this.status = BindStatus.PENDING;
    }
    
    public String getPlatform() { return platform; }
    public void setPlatform(String platform) { this.platform = platform; }
    public String getPlatformUserId() { return platformUserId; }
    public void setPlatformUserId(String platformUserId) { this.platformUserId = platformUserId; }
    public String getPlatformUserName() { return platformUserName; }
    public void setPlatformUserName(String platformUserName) { this.platformUserName = platformUserName; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public long getBindTime() { return bindTime; }
    public void setBindTime(long bindTime) { this.bindTime = bindTime; }
    public BindStatus getStatus() { return status; }
    public void setStatus(BindStatus status) { this.status = status; }
}

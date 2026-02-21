package net.ooder.server.session.admin;

import java.io.Serializable;
import java.util.Date;

public class SessionInfo implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private String sessionId;
    private String userId;
    private String account;
    private String userName;
    private String mobile;
    private String email;
    private String systemCode;
    private Date connectTime;
    private Date lastActiveTime;
    private long duration;
    private String status;
    private String clientIp;
    private String clientInfo;
    
    public SessionInfo() {
    }
    
    public String getSessionId() {
        return sessionId;
    }
    
    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
    
    public String getUserId() {
        return userId;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    public String getAccount() {
        return account;
    }
    
    public void setAccount(String account) {
        this.account = account;
    }
    
    public String getUserName() {
        return userName;
    }
    
    public void setUserName(String userName) {
        this.userName = userName;
    }
    
    public String getMobile() {
        return mobile;
    }
    
    public void setMobile(String mobile) {
        this.mobile = mobile;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getSystemCode() {
        return systemCode;
    }
    
    public void setSystemCode(String systemCode) {
        this.systemCode = systemCode;
    }
    
    public Date getConnectTime() {
        return connectTime;
    }
    
    public void setConnectTime(Date connectTime) {
        this.connectTime = connectTime;
    }
    
    public Date getLastActiveTime() {
        return lastActiveTime;
    }
    
    public void setLastActiveTime(Date lastActiveTime) {
        this.lastActiveTime = lastActiveTime;
    }
    
    public long getDuration() {
        return duration;
    }
    
    public void setDuration(long duration) {
        this.duration = duration;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getClientIp() {
        return clientIp;
    }
    
    public void setClientIp(String clientIp) {
        this.clientIp = clientIp;
    }
    
    public String getClientInfo() {
        return clientInfo;
    }
    
    public void setClientInfo(String clientInfo) {
        this.clientInfo = clientInfo;
    }
    
    public String getDurationFormatted() {
        long seconds = duration / 1000;
        long hours = seconds / 3600;
        long minutes = (seconds % 3600) / 60;
        long secs = seconds % 60;
        return String.format("%02d:%02d:%02d", hours, minutes, secs);
    }
    
    public boolean isExpired(long expireTimeMs) {
        if (lastActiveTime == null) {
            return true;
        }
        return System.currentTimeMillis() - lastActiveTime.getTime() > expireTimeMs;
    }
    
    @Override
    public String toString() {
        return "SessionInfo{" +
                "sessionId='" + sessionId + '\'' +
                ", userId='" + userId + '\'' +
                ", account='" + account + '\'' +
                ", systemCode='" + systemCode + '\'' +
                ", connectTime=" + connectTime +
                ", duration=" + getDurationFormatted() +
                ", status='" + status + '\'' +
                '}';
    }
}

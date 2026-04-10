package net.ooder.skill.common.spi.bind;

public class QrCodeInfo {
    
    private String sessionId;
    private String qrCodeUrl;
    private String qrCodeData;
    private long expireTime;
    private String platform;
    
    public QrCodeInfo() {}
    
    public QrCodeInfo(String sessionId, String qrCodeUrl, String platform) {
        this.sessionId = sessionId;
        this.qrCodeUrl = qrCodeUrl;
        this.platform = platform;
    }
    
    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }
    public String getQrCodeUrl() { return qrCodeUrl; }
    public void setQrCodeUrl(String qrCodeUrl) { this.qrCodeUrl = qrCodeUrl; }
    public String getQrCodeData() { return qrCodeData; }
    public void setQrCodeData(String qrCodeData) { this.qrCodeData = qrCodeData; }
    public long getExpireTime() { return expireTime; }
    public void setExpireTime(long expireTime) { this.expireTime = expireTime; }
    public String getPlatform() { return platform; }
    public void setPlatform(String platform) { this.platform = platform; }
}

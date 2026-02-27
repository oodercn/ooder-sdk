package net.ooder.scene.provider;

/**
 * 安全状态
 */
public class SecurityStatus {
    private String status;
    private String securityLevel;
    private int activePolicies;
    private int totalPolicies;
    private int recentAlerts;
    private int blockedAttempts;
    private double threatScore;
    private boolean firewallEnabled;
    private boolean encryptionEnabled;
    private boolean auditEnabled;
    private long lastScanTime;

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getSecurityLevel() { return securityLevel; }
    public void setSecurityLevel(String securityLevel) { this.securityLevel = securityLevel; }
    public int getActivePolicies() { return activePolicies; }
    public void setActivePolicies(int activePolicies) { this.activePolicies = activePolicies; }
    public int getTotalPolicies() { return totalPolicies; }
    public void setTotalPolicies(int totalPolicies) { this.totalPolicies = totalPolicies; }
    public int getRecentAlerts() { return recentAlerts; }
    public void setRecentAlerts(int recentAlerts) { this.recentAlerts = recentAlerts; }
    public int getBlockedAttempts() { return blockedAttempts; }
    public void setBlockedAttempts(int blockedAttempts) { this.blockedAttempts = blockedAttempts; }
    public double getThreatScore() { return threatScore; }
    public void setThreatScore(double threatScore) { this.threatScore = threatScore; }
    public boolean isFirewallEnabled() { return firewallEnabled; }
    public void setFirewallEnabled(boolean firewallEnabled) { this.firewallEnabled = firewallEnabled; }
    public boolean isEncryptionEnabled() { return encryptionEnabled; }
    public void setEncryptionEnabled(boolean encryptionEnabled) { this.encryptionEnabled = encryptionEnabled; }
    public boolean isAuditEnabled() { return auditEnabled; }
    public void setAuditEnabled(boolean auditEnabled) { this.auditEnabled = auditEnabled; }
    public long getLastScanTime() { return lastScanTime; }
    public void setLastScanTime(long lastScanTime) { this.lastScanTime = lastScanTime; }
}

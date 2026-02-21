package net.ooder.scene.provider;

/**
 * 安全统计
 */
public class SecurityStats {
    private long totalPolicies;
    private long activePolicies;
    private long totalAcls;
    private long totalThreats;
    private long resolvedThreats;
    private long blockedAttempts;
    private long securityScans;

    public long getTotalPolicies() { return totalPolicies; }
    public void setTotalPolicies(long totalPolicies) { this.totalPolicies = totalPolicies; }
    public long getActivePolicies() { return activePolicies; }
    public void setActivePolicies(long activePolicies) { this.activePolicies = activePolicies; }
    public long getTotalAcls() { return totalAcls; }
    public void setTotalAcls(long totalAcls) { this.totalAcls = totalAcls; }
    public long getTotalThreats() { return totalThreats; }
    public void setTotalThreats(long totalThreats) { this.totalThreats = totalThreats; }
    public long getResolvedThreats() { return resolvedThreats; }
    public void setResolvedThreats(long resolvedThreats) { this.resolvedThreats = resolvedThreats; }
    public long getBlockedAttempts() { return blockedAttempts; }
    public void setBlockedAttempts(long blockedAttempts) { this.blockedAttempts = blockedAttempts; }
    public long getSecurityScans() { return securityScans; }
    public void setSecurityScans(long securityScans) { this.securityScans = securityScans; }
}

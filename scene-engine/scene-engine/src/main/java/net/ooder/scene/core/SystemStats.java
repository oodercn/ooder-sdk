package net.ooder.scene.core;

/**
 * 系统统计信息
 */
public class SystemStats {
    private long totalUsers;
    private long activeUsers;
    private long totalSkills;
    private long installedSkills;
    private long totalSceneGroups;
    private long activeSceneGroups;
    private long totalAuditLogs;
    private long uptime;

    public SystemStats() {}

    public long getTotalUsers() { return totalUsers; }
    public void setTotalUsers(long totalUsers) { this.totalUsers = totalUsers; }
    public long getActiveUsers() { return activeUsers; }
    public void setActiveUsers(long activeUsers) { this.activeUsers = activeUsers; }
    public long getTotalSkills() { return totalSkills; }
    public void setTotalSkills(long totalSkills) { this.totalSkills = totalSkills; }
    public long getInstalledSkills() { return installedSkills; }
    public void setInstalledSkills(long installedSkills) { this.installedSkills = installedSkills; }
    public long getTotalSceneGroups() { return totalSceneGroups; }
    public void setTotalSceneGroups(long totalSceneGroups) { this.totalSceneGroups = totalSceneGroups; }
    public long getActiveSceneGroups() { return activeSceneGroups; }
    public void setActiveSceneGroups(long activeSceneGroups) { this.activeSceneGroups = activeSceneGroups; }
    public long getTotalAuditLogs() { return totalAuditLogs; }
    public void setTotalAuditLogs(long totalAuditLogs) { this.totalAuditLogs = totalAuditLogs; }
    public long getUptime() { return uptime; }
    public void setUptime(long uptime) { this.uptime = uptime; }
}

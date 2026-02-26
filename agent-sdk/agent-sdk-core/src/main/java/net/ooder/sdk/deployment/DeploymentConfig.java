package net.ooder.sdk.deployment;

import java.nio.file.Path;
import java.time.Duration;

/**
 * 部署配置
 */
public class DeploymentConfig {
    private boolean zeroDowntime = true;
    private Duration healthCheckTimeout = Duration.ofSeconds(30);
    private Duration startupTimeout = Duration.ofMinutes(2);
    private int healthCheckRetries = 3;
    private boolean autoRollback = true;
    private Path backupDir;
    private double trafficShiftPercentage = 100.0;
    private boolean keepOldVersion = false;

    public boolean isZeroDowntime() {
        return zeroDowntime;
    }

    public void setZeroDowntime(boolean zeroDowntime) {
        this.zeroDowntime = zeroDowntime;
    }

    public Duration getHealthCheckTimeout() {
        return healthCheckTimeout;
    }

    public void setHealthCheckTimeout(Duration healthCheckTimeout) {
        this.healthCheckTimeout = healthCheckTimeout;
    }

    public Duration getStartupTimeout() {
        return startupTimeout;
    }

    public void setStartupTimeout(Duration startupTimeout) {
        this.startupTimeout = startupTimeout;
    }

    public int getHealthCheckRetries() {
        return healthCheckRetries;
    }

    public void setHealthCheckRetries(int healthCheckRetries) {
        this.healthCheckRetries = healthCheckRetries;
    }

    public boolean isAutoRollback() {
        return autoRollback;
    }

    public void setAutoRollback(boolean autoRollback) {
        this.autoRollback = autoRollback;
    }

    public Path getBackupDir() {
        return backupDir;
    }

    public void setBackupDir(Path backupDir) {
        this.backupDir = backupDir;
    }

    public double getTrafficShiftPercentage() {
        return trafficShiftPercentage;
    }

    public void setTrafficShiftPercentage(double trafficShiftPercentage) {
        this.trafficShiftPercentage = trafficShiftPercentage;
    }

    public boolean isKeepOldVersion() {
        return keepOldVersion;
    }

    public void setKeepOldVersion(boolean keepOldVersion) {
        this.keepOldVersion = keepOldVersion;
    }

    public static DeploymentConfig defaultConfig() {
        return new DeploymentConfig();
    }
}

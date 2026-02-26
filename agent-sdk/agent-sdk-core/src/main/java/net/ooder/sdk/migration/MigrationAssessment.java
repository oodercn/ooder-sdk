package net.ooder.sdk.migration;

/**
 * 迁移评估
 */
public class MigrationAssessment {
    private RiskLevel riskLevel;
    private long estimatedDataSize;
    private long estimatedDowntime;
    private boolean serviceDisruptionRequired;
    private double successProbability;
    private String[] prerequisites;

    public enum RiskLevel {
        LOW, MEDIUM, HIGH, CRITICAL
    }

    public RiskLevel getRiskLevel() {
        return riskLevel;
    }

    public void setRiskLevel(RiskLevel riskLevel) {
        this.riskLevel = riskLevel;
    }

    public long getEstimatedDataSize() {
        return estimatedDataSize;
    }

    public void setEstimatedDataSize(long estimatedDataSize) {
        this.estimatedDataSize = estimatedDataSize;
    }

    public long getEstimatedDowntime() {
        return estimatedDowntime;
    }

    public void setEstimatedDowntime(long estimatedDowntime) {
        this.estimatedDowntime = estimatedDowntime;
    }

    public boolean isServiceDisruptionRequired() {
        return serviceDisruptionRequired;
    }

    public void setServiceDisruptionRequired(boolean serviceDisruptionRequired) {
        this.serviceDisruptionRequired = serviceDisruptionRequired;
    }

    public double getSuccessProbability() {
        return successProbability;
    }

    public void setSuccessProbability(double successProbability) {
        this.successProbability = successProbability;
    }

    public String[] getPrerequisites() {
        return prerequisites;
    }

    public void setPrerequisites(String[] prerequisites) {
        this.prerequisites = prerequisites;
    }
}

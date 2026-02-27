package net.ooder.scene.provider;

/**
 * 威胁信息
 */
public class ThreatInfo {
    private String threatId;
    private String threatType;
    private String severity;
    private String source;
    private String description;
    private String status;
    private String recommendation;
    private long detectedAt;
    private long resolvedAt;

    public String getThreatId() { return threatId; }
    public void setThreatId(String threatId) { this.threatId = threatId; }
    public String getThreatType() { return threatType; }
    public void setThreatType(String threatType) { this.threatType = threatType; }
    public String getSeverity() { return severity; }
    public void setSeverity(String severity) { this.severity = severity; }
    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getRecommendation() { return recommendation; }
    public void setRecommendation(String recommendation) { this.recommendation = recommendation; }
    public long getDetectedAt() { return detectedAt; }
    public void setDetectedAt(long detectedAt) { this.detectedAt = detectedAt; }
    public long getResolvedAt() { return resolvedAt; }
    public void setResolvedAt(long resolvedAt) { this.resolvedAt = resolvedAt; }
}

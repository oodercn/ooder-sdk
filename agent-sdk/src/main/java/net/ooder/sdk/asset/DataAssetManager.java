package net.ooder.sdk.asset;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface DataAssetManager {
    
    DataAsset registerDataAsset(DataAsset asset);
    
    void updateDataAsset(String assetId, Map<String, Object> updates);
    
    void unregisterDataAsset(String assetId);
    
    Optional<DataAsset> getDataAsset(String assetId);
    
    List<DataAsset> getDataAssetsByCategory(DataCategory category);
    
    List<DataAsset> getDataAssetsByOwner(String ownerId);
    
    List<DataAsset> getDataAssetsBySensitivity(DataSensitivity sensitivity);
    
    CompletableFuture<DataQualityReport> assessQuality(String assetId);
    
    CompletableFuture<Void> backup(String assetId);
    
    CompletableFuture<Void> restore(String assetId, String backupId);
    
    CompletableFuture<Map<String, Object>> analyze(String assetId);
    
    void setRetentionPolicy(String assetId, RetentionPolicy policy);
    
    void applyDataClassification(String assetId, DataClassification classification);
    
    enum DataCategory {
        BUSINESS_DATA,
        OPERATIONAL_DATA,
        FINANCIAL_DATA,
        CUSTOMER_DATA,
        KNOWLEDGE_DATA
    }
    
    enum DataFormat {
        STRUCTURED,
        SEMI_STRUCTURED,
        UNSTRUCTURED
    }
    
    enum DataSensitivity {
        PUBLIC,
        INTERNAL,
        CONFIDENTIAL,
        RESTRICTED
    }
    
    class RetentionPolicy {
        private int retentionDays;
        private boolean autoDelete;
        private String archiveLocation;
        
        public int getRetentionDays() { return retentionDays; }
        public void setRetentionDays(int retentionDays) { this.retentionDays = retentionDays; }
        
        public boolean isAutoDelete() { return autoDelete; }
        public void setAutoDelete(boolean autoDelete) { this.autoDelete = autoDelete; }
        
        public String getArchiveLocation() { return archiveLocation; }
        public void setArchiveLocation(String archiveLocation) { this.archiveLocation = archiveLocation; }
    }
    
    class DataClassification {
        private String level;
        private String justification;
        private List<String> complianceRequirements;
        
        public String getLevel() { return level; }
        public void setLevel(String level) { this.level = level; }
        
        public String getJustification() { return justification; }
        public void setJustification(String justification) { this.justification = justification; }
        
        public List<String> getComplianceRequirements() { return complianceRequirements; }
        public void setComplianceRequirements(List<String> complianceRequirements) { this.complianceRequirements = complianceRequirements; }
    }
    
    class DataQualityReport {
        private String assetId;
        private boolean healthy;
        private double completenessScore;
        private double accuracyScore;
        private double consistencyScore;
        private double overallScore;
        private List<String> issues;
        private Map<String, Object> details;
        
        public String getAssetId() { return assetId; }
        public void setAssetId(String assetId) { this.assetId = assetId; }
        
        public boolean isHealthy() { return healthy; }
        public void setHealthy(boolean healthy) { this.healthy = healthy; }
        
        public double getCompletenessScore() { return completenessScore; }
        public void setCompletenessScore(double completenessScore) { this.completenessScore = completenessScore; }
        
        public double getAccuracyScore() { return accuracyScore; }
        public void setAccuracyScore(double accuracyScore) { this.accuracyScore = accuracyScore; }
        
        public double getConsistencyScore() { return consistencyScore; }
        public void setConsistencyScore(double consistencyScore) { this.consistencyScore = consistencyScore; }
        
        public double getOverallScore() { return overallScore; }
        public void setOverallScore(double overallScore) { this.overallScore = overallScore; }
        
        public List<String> getIssues() { return issues; }
        public void setIssues(List<String> issues) { this.issues = issues; }
        
        public Map<String, Object> getDetails() { return details; }
        public void setDetails(Map<String, Object> details) { this.details = details; }
    }
}

package net.ooder.sdk.validator;

import java.util.List;

public class ValidationResult {
    private String category;
    private String version;
    private long timestamp;
    private boolean valid;
    private int totalChecks;
    private int passed;
    private int warnings;
    private int errors;
    private int score;
    private List<CheckResult> details;
    private List<Recommendation> recommendations;
    
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    
    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }
    
    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    
    public boolean isValid() { return valid; }
    public void setValid(boolean valid) { this.valid = valid; }
    
    public int getTotalChecks() { return totalChecks; }
    public void setTotalChecks(int totalChecks) { this.totalChecks = totalChecks; }
    
    public int getPassed() { return passed; }
    public void setPassed(int passed) { this.passed = passed; }
    
    public int getWarnings() { return warnings; }
    public void setWarnings(int warnings) { this.warnings = warnings; }
    
    public int getErrors() { return errors; }
    public void setErrors(int errors) { this.errors = errors; }
    
    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }
    
    public List<CheckResult> getDetails() { return details; }
    public void setDetails(List<CheckResult> details) { this.details = details; }
    
    public List<Recommendation> getRecommendations() { return recommendations; }
    public void setRecommendations(List<Recommendation> recommendations) { this.recommendations = recommendations; }
}
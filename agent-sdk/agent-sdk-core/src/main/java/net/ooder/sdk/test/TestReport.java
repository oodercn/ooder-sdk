package net.ooder.sdk.test;

import java.util.List;

public class TestReport {
    private String category;
    private String version;
    private long timestamp;
    private long duration;
    private int total;
    private int passed;
    private int failed;
    private int skipped;
    private double coverage;
    private List<TestResult> results;
    private CoverageReport coverageReport;
    
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    
    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }
    
    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    
    public long getDuration() { return duration; }
    public void setDuration(long duration) { this.duration = duration; }
    
    public int getTotal() { return total; }
    public void setTotal(int total) { this.total = total; }
    
    public int getPassed() { return passed; }
    public void setPassed(int passed) { this.passed = passed; }
    
    public int getFailed() { return failed; }
    public void setFailed(int failed) { this.failed = failed; }
    
    public int getSkipped() { return skipped; }
    public void setSkipped(int skipped) { this.skipped = skipped; }
    
    public double getCoverage() { return coverage; }
    public void setCoverage(double coverage) { this.coverage = coverage; }
    
    public List<TestResult> getResults() { return results; }
    public void setResults(List<TestResult> results) { this.results = results; }
    
    public CoverageReport getCoverageReport() { return coverageReport; }
    public void setCoverageReport(CoverageReport coverageReport) { this.coverageReport = coverageReport; }
}
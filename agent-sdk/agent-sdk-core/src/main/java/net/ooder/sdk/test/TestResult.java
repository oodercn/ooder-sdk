package net.ooder.sdk.test;

import java.util.Map;

public class TestResult {
    private String name;
    private TestType type;
    private net.ooder.sdk.validator.Status status;
    private long duration;
    private String errorMessage;
    private String stackTrace;
    private Map<String, Object> actualOutput;
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public TestType getType() { return type; }
    public void setType(TestType type) { this.type = type; }
    
    public net.ooder.sdk.validator.Status getStatus() { return status; }
    public void setStatus(net.ooder.sdk.validator.Status status) { this.status = status; }
    
    public long getDuration() { return duration; }
    public void setDuration(long duration) { this.duration = duration; }
    
    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
    
    public String getStackTrace() { return stackTrace; }
    public void setStackTrace(String stackTrace) { this.stackTrace = stackTrace; }
    
    public Map<String, Object> getActualOutput() { return actualOutput; }
    public void setActualOutput(Map<String, Object> actualOutput) { this.actualOutput = actualOutput; }
}
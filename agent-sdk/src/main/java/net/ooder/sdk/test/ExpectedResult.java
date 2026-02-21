package net.ooder.sdk.test;

import java.util.Map;

public class ExpectedResult {
    private String status;
    private Map<String, Object> output;
    private ExpectedError error;
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public Map<String, Object> getOutput() { return output; }
    public void setOutput(Map<String, Object> output) { this.output = output; }
    
    public ExpectedError getError() { return error; }
    public void setError(ExpectedError error) { this.error = error; }
}
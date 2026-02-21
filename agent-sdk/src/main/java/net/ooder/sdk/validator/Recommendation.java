package net.ooder.sdk.validator;

public class Recommendation {
    private Severity severity;
    private String message;
    private String action;
    
    public Severity getSeverity() { return severity; }
    public void setSeverity(Severity severity) { this.severity = severity; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    
    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
}
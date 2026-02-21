package net.ooder.sdk.test;

import java.util.Map;

public class TestStep {
    private String action;
    private Map<String, Object> input;
    private ExpectedResult expected;
    private Map<String, String> save;
    
    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
    
    public Map<String, Object> getInput() { return input; }
    public void setInput(Map<String, Object> input) { this.input = input; }
    
    public ExpectedResult getExpected() { return expected; }
    public void setExpected(ExpectedResult expected) { this.expected = expected; }
    
    public Map<String, String> getSave() { return save; }
    public void setSave(Map<String, String> save) { this.save = save; }
}
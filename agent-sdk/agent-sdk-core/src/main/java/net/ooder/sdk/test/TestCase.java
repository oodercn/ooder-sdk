package net.ooder.sdk.test;

import java.util.List;
import java.util.Map;

public class TestCase {
    private String name;
    private String description;
    private String category;
    private String method;
    private TestType type;
    private Map<String, Object> input;
    private ExpectedResult expected;
    private List<TestStep> steps;
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    
    public String getMethod() { return method; }
    public void setMethod(String method) { this.method = method; }
    
    public TestType getType() { return type; }
    public void setType(TestType type) { this.type = type; }
    
    public Map<String, Object> getInput() { return input; }
    public void setInput(Map<String, Object> input) { this.input = input; }
    
    public ExpectedResult getExpected() { return expected; }
    public void setExpected(ExpectedResult expected) { this.expected = expected; }
    
    public List<TestStep> getSteps() { return steps; }
    public void setSteps(List<TestStep> steps) { this.steps = steps; }
}
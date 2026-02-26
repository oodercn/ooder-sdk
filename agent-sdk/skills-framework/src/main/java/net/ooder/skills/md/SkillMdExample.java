package net.ooder.skills.md;

import java.util.Map;

public class SkillMdExample {
    
    private String name;
    private String description;
    private Map<String, Object> input;
    private Map<String, Object> output;
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public Map<String, Object> getInput() { return input; }
    public void setInput(Map<String, Object> input) { this.input = input; }
    
    public Map<String, Object> getOutput() { return output; }
    public void setOutput(Map<String, Object> output) { this.output = output; }
}

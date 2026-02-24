package net.ooder.sdk.skill;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SkillMdDocument {
    
    private String skillId;
    private String name;
    private String description;
    private String version;
    private String author;
    private Map<String, Object> metadata = new HashMap<>();
    private List<SkillMdSection> sections = new ArrayList<>();
    private List<SkillMdParameter> inputs = new ArrayList<>();
    private List<SkillMdParameter> outputs = new ArrayList<>();
    private List<SkillMdExample> examples = new ArrayList<>();
    private String executorClass;
    private String domain;
    private List<String> tags = new ArrayList<>();
    
    public String getSkillId() { return skillId; }
    public void setSkillId(String skillId) { this.skillId = skillId; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }
    
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    
    public Map<String, Object> getMetadata() { return metadata; }
    public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }
    
    public List<SkillMdSection> getSections() { return sections; }
    public void setSections(List<SkillMdSection> sections) { this.sections = sections; }
    
    public List<SkillMdParameter> getInputs() { return inputs; }
    public void setInputs(List<SkillMdParameter> inputs) { this.inputs = inputs; }
    
    public List<SkillMdParameter> getOutputs() { return outputs; }
    public void setOutputs(List<SkillMdParameter> outputs) { this.outputs = outputs; }
    
    public List<SkillMdExample> getExamples() { return examples; }
    public void setExamples(List<SkillMdExample> examples) { this.examples = examples; }
    
    public String getExecutorClass() { return executorClass; }
    public void setExecutorClass(String executorClass) { this.executorClass = executorClass; }
    
    public String getDomain() { return domain; }
    public void setDomain(String domain) { this.domain = domain; }
    
    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }
}

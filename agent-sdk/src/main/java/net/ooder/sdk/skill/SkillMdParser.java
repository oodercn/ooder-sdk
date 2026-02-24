package net.ooder.sdk.skill;

import java.util.List;
import java.util.Map;

public interface SkillMdParser {
    
    SkillMdDocument parse(String markdownContent);
    
    SkillMdDocument parseFile(String filePath);
    
    boolean validate(String markdownContent);
    
    List<String> extractSections(String markdownContent);
    
    Map<String, String> extractMetadata(String markdownContent);
    
    List<SkillMdSection> parseSections(String markdownContent);
    
    class SkillMdDocument {
        private String skillId;
        private String name;
        private String description;
        private String version;
        private String author;
        private Map<String, Object> metadata;
        private List<SkillMdSection> sections;
        private List<SkillMdParameter> inputs;
        private List<SkillMdParameter> outputs;
        private List<SkillMdExample> examples;
        private String executorClass;
        private String domain;
        private List<String> tags;
        
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
    
    class SkillMdSection {
        private String title;
        private String content;
        private int level;
        private int startLine;
        private int endLine;
        
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
        
        public int getLevel() { return level; }
        public void setLevel(int level) { this.level = level; }
        
        public int getStartLine() { return startLine; }
        public void setStartLine(int startLine) { this.startLine = startLine; }
        
        public int getEndLine() { return endLine; }
        public void setEndLine(int endLine) { this.endLine = endLine; }
    }
    
    class SkillMdParameter {
        private String name;
        private String type;
        private String description;
        private boolean required;
        private Object defaultValue;
        private List<String> constraints;
        
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        
        public boolean isRequired() { return required; }
        public void setRequired(boolean required) { this.required = required; }
        
        public Object getDefaultValue() { return defaultValue; }
        public void setDefaultValue(Object defaultValue) { this.defaultValue = defaultValue; }
        
        public List<String> getConstraints() { return constraints; }
        public void setConstraints(List<String> constraints) { this.constraints = constraints; }
    }
    
    class SkillMdExample {
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
}

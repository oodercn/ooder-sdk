package net.ooder.sdk.nlp;

import java.util.List;
import java.util.Map;

public interface SlotFiller {
    
    Map<String, Object> fill(String text, String intentId);
    
    Map<String, Object> fill(String text, SlotSchema schema);
    
    void defineSlot(String slotName, SlotDefinition definition);
    
    void removeSlot(String slotName);
    
    List<String> getSlotNames();
    
    SlotDefinition getSlotDefinition(String slotName);
    
    class SlotSchema {
        private String intentId;
        private List<SlotDefinition> slots;
        
        public String getIntentId() { return intentId; }
        public void setIntentId(String intentId) { this.intentId = intentId; }
        
        public List<SlotDefinition> getSlots() { return slots; }
        public void setSlots(List<SlotDefinition> slots) { this.slots = slots; }
    }
    
    class SlotDefinition {
        private String name;
        private String type;
        private boolean required;
        private List<String> prompts;
        private Object defaultValue;
        private List<String> examples;
        private String description;
        
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        
        public boolean isRequired() { return required; }
        public void setRequired(boolean required) { this.required = required; }
        
        public List<String> getPrompts() { return prompts; }
        public void setPrompts(List<String> prompts) { this.prompts = prompts; }
        
        public Object getDefaultValue() { return defaultValue; }
        public void setDefaultValue(Object defaultValue) { this.defaultValue = defaultValue; }
        
        public List<String> getExamples() { return examples; }
        public void setExamples(List<String> examples) { this.examples = examples; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
    }
    
    class SlotValue {
        private String name;
        private Object value;
        private double confidence;
        private String rawText;
        private int startIndex;
        private int endIndex;
        
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public Object getValue() { return value; }
        public void setValue(Object value) { this.value = value; }
        
        public double getConfidence() { return confidence; }
        public void setConfidence(double confidence) { this.confidence = confidence; }
        
        public String getRawText() { return rawText; }
        public void setRawText(String rawText) { this.rawText = rawText; }
        
        public int getStartIndex() { return startIndex; }
        public void setStartIndex(int startIndex) { this.startIndex = startIndex; }
        
        public int getEndIndex() { return endIndex; }
        public void setEndIndex(int endIndex) { this.endIndex = endIndex; }
    }
}

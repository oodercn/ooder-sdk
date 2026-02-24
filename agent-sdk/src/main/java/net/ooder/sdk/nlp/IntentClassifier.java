package net.ooder.sdk.nlp;

import java.util.List;
import java.util.Map;

public interface IntentClassifier {
    
    IntentResult classify(String text);
    
    IntentResult classify(String text, String domain);
    
    List<IntentResult> classifyTopN(String text, int n);
    
    void train(List<TrainingExample> examples);
    
    void addIntent(String intentId, List<String> examples);
    
    List<String> getIntents();
    
    void setDomain(String domain);
    
    String getDomain();
    
    class IntentResult {
        private String intentId;
        private String intentName;
        private double confidence;
        private Map<String, Object> slots;
        private String domain;
        private List<AlternativeIntent> alternatives;
        
        public String getIntentId() { return intentId; }
        public void setIntentId(String intentId) { this.intentId = intentId; }
        
        public String getIntentName() { return intentName; }
        public void setIntentName(String intentName) { this.intentName = intentName; }
        
        public double getConfidence() { return confidence; }
        public void setConfidence(double confidence) { this.confidence = confidence; }
        
        public Map<String, Object> getSlots() { return slots; }
        public void setSlots(Map<String, Object> slots) { this.slots = slots; }
        
        public String getDomain() { return domain; }
        public void setDomain(String domain) { this.domain = domain; }
        
        public List<AlternativeIntent> getAlternatives() { return alternatives; }
        public void setAlternatives(List<AlternativeIntent> alternatives) { this.alternatives = alternatives; }
    }
    
    class AlternativeIntent {
        private String intentId;
        private double confidence;
        
        public String getIntentId() { return intentId; }
        public void setIntentId(String intentId) { this.intentId = intentId; }
        
        public double getConfidence() { return confidence; }
        public void setConfidence(double confidence) { this.confidence = confidence; }
    }
    
    class TrainingExample {
        private String text;
        private String intentId;
        private Map<String, Object> slots;
        
        public String getText() { return text; }
        public void setText(String text) { this.text = text; }
        
        public String getIntentId() { return intentId; }
        public void setIntentId(String intentId) { this.intentId = intentId; }
        
        public Map<String, Object> getSlots() { return slots; }
        public void setSlots(Map<String, Object> slots) { this.slots = slots; }
        
        public static TrainingExample of(String text, String intentId) {
            TrainingExample example = new TrainingExample();
            example.setText(text);
            example.setIntentId(intentId);
            return example;
        }
    }
}

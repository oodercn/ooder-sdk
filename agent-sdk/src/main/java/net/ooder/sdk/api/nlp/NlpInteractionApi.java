package net.ooder.sdk.api.memory;

import net.ooder.sdk.story.WillTransformer;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface NlpInteractionApi {
    
    WillTransformer.WillExpression parse(String naturalLanguage);
    
    CompletableFuture<WillTransformer.WillExpression> parseAsync(String naturalLanguage);
    
    NlpResult analyze(String text);
    
    String extractIntent(String text);
    
    List<String> extractEntities(String text);
    
    String extractDomain(String text);
    
    int estimatePriority(String text);
    
    Map<String, Object> extractMetadata(String text);
    
    double calculateConfidence(WillTransformer.WillExpression will);
    
    List<String> suggestCapabilities(String intent, String domain);
    
    String generateClarificationQuestion(WillTransformer.WillExpression will);
    
    WillTransformer.WillExpression mergeContext(WillTransformer.WillExpression will, Map<String, Object> context);
    
    class NlpResult {
        private String intent;
        private String domain;
        private List<String> entities;
        private Map<String, Object> metadata;
        private double confidence;
        private String sentiment;
        private String language;
        
        public String getIntent() { return intent; }
        public void setIntent(String intent) { this.intent = intent; }
        
        public String getDomain() { return domain; }
        public void setDomain(String domain) { this.domain = domain; }
        
        public List<String> getEntities() { return entities; }
        public void setEntities(List<String> entities) { this.entities = entities; }
        
        public Map<String, Object> getMetadata() { return metadata; }
        public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }
        
        public double getConfidence() { return confidence; }
        public void setConfidence(double confidence) { this.confidence = confidence; }
        
        public String getSentiment() { return sentiment; }
        public void setSentiment(String sentiment) { this.sentiment = sentiment; }
        
        public String getLanguage() { return language; }
        public void setLanguage(String language) { this.language = language; }
    }
}

package net.ooder.sdk.nlp;

import java.util.List;
import java.util.Map;

public interface NlpProcessor {
    
    NlpResult process(String text);
    
    String extractIntent(String text);
    
    List<Entity> extractEntities(String text);
    
    List<String> tokenize(String text);
    
    String normalize(String text);
    
    String detectLanguage(String text);
    
    SentimentResult analyzeSentiment(String text);
    
    List<String> extractKeywords(String text, int limit);
    
    String summarize(String text, int maxLength);
    
    class NlpResult {
        private String text;
        private String language;
        private String intent;
        private double intentConfidence;
        private List<Entity> entities;
        private SentimentResult sentiment;
        private List<String> tokens;
        private List<String> keywords;
        private Map<String, Object> metadata;
        
        public String getText() { return text; }
        public void setText(String text) { this.text = text; }
        
        public String getLanguage() { return language; }
        public void setLanguage(String language) { this.language = language; }
        
        public String getIntent() { return intent; }
        public void setIntent(String intent) { this.intent = intent; }
        
        public double getIntentConfidence() { return intentConfidence; }
        public void setIntentConfidence(double intentConfidence) { this.intentConfidence = intentConfidence; }
        
        public List<Entity> getEntities() { return entities; }
        public void setEntities(List<Entity> entities) { this.entities = entities; }
        
        public SentimentResult getSentiment() { return sentiment; }
        public void setSentiment(SentimentResult sentiment) { this.sentiment = sentiment; }
        
        public List<String> getTokens() { return tokens; }
        public void setTokens(List<String> tokens) { this.tokens = tokens; }
        
        public List<String> getKeywords() { return keywords; }
        public void setKeywords(List<String> keywords) { this.keywords = keywords; }
        
        public Map<String, Object> getMetadata() { return metadata; }
        public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }
    }
    
    class Entity {
        private String text;
        private String type;
        private int startIndex;
        private int endIndex;
        private double confidence;
        private Map<String, Object> metadata;
        
        public String getText() { return text; }
        public void setText(String text) { this.text = text; }
        
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        
        public int getStartIndex() { return startIndex; }
        public void setStartIndex(int startIndex) { this.startIndex = startIndex; }
        
        public int getEndIndex() { return endIndex; }
        public void setEndIndex(int endIndex) { this.endIndex = endIndex; }
        
        public double getConfidence() { return confidence; }
        public void setConfidence(double confidence) { this.confidence = confidence; }
        
        public Map<String, Object> getMetadata() { return metadata; }
        public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }
    }
    
    class SentimentResult {
        private String label;
        private double score;
        private Map<String, Double> scores;
        
        public String getLabel() { return label; }
        public void setLabel(String label) { this.label = label; }
        
        public double getScore() { return score; }
        public void setScore(double score) { this.score = score; }
        
        public Map<String, Double> getScores() { return scores; }
        public void setScores(Map<String, Double> scores) { this.scores = scores; }
    }
}

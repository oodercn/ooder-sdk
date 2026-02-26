package net.ooder.sdk.story;

import java.util.List;
import java.util.Map;

public interface WillTransformer {
    
    UserStory transform(WillExpression will);
    
    List<UserStory> transformToStories(WillExpression will);
    
    StoryContext buildContext(UserStory story);
    
    List<StoryStep> generateSteps(UserStory story);
    
    List<String> identifyRequiredCapabilities(UserStory story);
    
    Map<String, Object> extractVariables(WillExpression will);
    
    class WillExpression {
        private String willId;
        private String rawText;
        private String intent;
        private String domain;
        private List<String> entities;
        private Map<String, Object> metadata;
        private int priority;
        
        public String getWillId() { return willId; }
        public void setWillId(String willId) { this.willId = willId; }
        
        public String getRawText() { return rawText; }
        public void setRawText(String rawText) { this.rawText = rawText; }
        
        public String getIntent() { return intent; }
        public void setIntent(String intent) { this.intent = intent; }
        
        public String getDomain() { return domain; }
        public void setDomain(String domain) { this.domain = domain; }
        
        public List<String> getEntities() { return entities; }
        public void setEntities(List<String> entities) { this.entities = entities; }
        
        public Map<String, Object> getMetadata() { return metadata; }
        public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }
        
        public int getPriority() { return priority; }
        public void setPriority(int priority) { this.priority = priority; }
        
        public static WillExpression fromText(String text) {
            WillExpression will = new WillExpression();
            will.setWillId("will-" + System.currentTimeMillis());
            will.setRawText(text);
            will.setPriority(5);
            return will;
        }
        
        public static WillExpressionBuilder builder() {
            return new WillExpressionBuilder();
        }
    }
    
    class WillExpressionBuilder {
        private String willId;
        private String rawText;
        private String intent;
        private String domain;
        private List<String> entities;
        private Map<String, Object> metadata;
        private int priority = 5;
        
        public WillExpressionBuilder willId(String willId) { this.willId = willId; return this; }
        public WillExpressionBuilder rawText(String rawText) { this.rawText = rawText; return this; }
        public WillExpressionBuilder intent(String intent) { this.intent = intent; return this; }
        public WillExpressionBuilder domain(String domain) { this.domain = domain; return this; }
        public WillExpressionBuilder entities(List<String> entities) { this.entities = entities; return this; }
        public WillExpressionBuilder metadata(Map<String, Object> metadata) { this.metadata = metadata; return this; }
        public WillExpressionBuilder priority(int priority) { this.priority = priority; return this; }
        
        public WillExpression build() {
            WillExpression will = new WillExpression();
            if (willId != null) will.setWillId(willId);
            else will.setWillId("will-" + System.currentTimeMillis());
            will.setRawText(rawText);
            will.setIntent(intent);
            will.setDomain(domain);
            will.setEntities(entities);
            will.setMetadata(metadata);
            will.setPriority(priority);
            return will;
        }
    }
}

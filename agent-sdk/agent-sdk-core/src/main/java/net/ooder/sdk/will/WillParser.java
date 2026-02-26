package net.ooder.sdk.will;

import java.util.List;

public interface WillParser {
    
    WillParseResult parse(String naturalLanguage);
    
    String understandSemantics(String text);
    
    String inferIntent(String text);
    
    List<String> identifyConstraints(String text);
    
    int judgePriority(String text);
    
    WillExpression.WillType inferWillType(String text);
    
    class WillParseResult {
        private String willId;
        private String semantics;
        private String intent;
        private List<String> constraints;
        private int priority;
        private WillExpression.WillType type;
        private String goal;
        private String action;
        private String object;
        private String timeline;
        private String deadline;
        private String responsible;
        private String originalText;
        private boolean success;
        private String errorMessage;
        
        public String getWillId() { return willId; }
        public void setWillId(String willId) { this.willId = willId; }
        
        public String getSemantics() { return semantics; }
        public void setSemantics(String semantics) { this.semantics = semantics; }
        
        public String getIntent() { return intent; }
        public void setIntent(String intent) { this.intent = intent; }
        
        public List<String> getConstraints() { return constraints; }
        public void setConstraints(List<String> constraints) { this.constraints = constraints; }
        
        public int getPriority() { return priority; }
        public void setPriority(int priority) { this.priority = priority; }
        
        public WillExpression.WillType getType() { return type; }
        public void setType(WillExpression.WillType type) { this.type = type; }
        
        public String getGoal() { return goal; }
        public void setGoal(String goal) { this.goal = goal; }
        
        public String getAction() { return action; }
        public void setAction(String action) { this.action = action; }
        
        public String getObject() { return object; }
        public void setObject(String object) { this.object = object; }
        
        public String getTimeline() { return timeline; }
        public void setTimeline(String timeline) { this.timeline = timeline; }
        
        public String getDeadline() { return deadline; }
        public void setDeadline(String deadline) { this.deadline = deadline; }
        
        public String getResponsible() { return responsible; }
        public void setResponsible(String responsible) { this.responsible = responsible; }
        
        public String getOriginalText() { return originalText; }
        public void setOriginalText(String originalText) { this.originalText = originalText; }
        
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        
        public String getErrorMessage() { return errorMessage; }
        public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
        
        public WillExpression toWillExpression() {
            return WillExpression.builder()
                .willId(willId)
                .type(type)
                .goal(goal)
                .action(action)
                .object(object)
                .timeline(timeline)
                .deadline(deadline)
                .responsible(responsible)
                .priority(priority)
                .constraints(constraints)
                .originalText(originalText)
                .build();
        }
        
        public static WillParseResult success(String willId) {
            WillParseResult result = new WillParseResult();
            result.setWillId(willId);
            result.setSuccess(true);
            return result;
        }
        
        public static WillParseResult failure(String errorMessage) {
            WillParseResult result = new WillParseResult();
            result.setSuccess(false);
            result.setErrorMessage(errorMessage);
            return result;
        }
    }
}

package net.ooder.sdk.will;

import java.time.Instant;
import java.util.List;
import java.util.Map;

public interface WillExpression {
    
    String getWillId();
    
    WillType getType();
    
    WillStatus getStatus();
    
    void setStatus(WillStatus status);
    
    String getGoal();
    
    String getTimeline();
    
    int getPriority();
    
    List<String> getConstraints();
    
    String getAction();
    
    String getObject();
    
    String getResourceRequirements();
    
    String getDeadline();
    
    String getResponsible();
    
    String getOriginalText();
    
    Instant getCreatedAt();
    
    String getCreatedBy();
    
    Map<String, Object> getMetadata();
    
    enum WillType {
        STRATEGIC,
        TACTICAL,
        EXECUTION
    }
    
    enum WillStatus {
        EXPRESSED,
        UNDERSTOOD,
        TRANSFORMED,
        EXECUTING,
        COMPLETED,
        FAILED,
        CANCELLED
    }
    
    static WillExpressionBuilder builder() {
        return new WillExpressionBuilder();
    }
}

package net.ooder.sdk.will;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WillExpressionBuilder {
    
    String willId;
    WillExpression.WillType type;
    WillExpression.WillStatus status = WillExpression.WillStatus.EXPRESSED;
    String goal;
    String timeline;
    int priority = 5;
    List<String> constraints = new ArrayList<>();
    String action;
    String object;
    String resourceRequirements;
    String deadline;
    String responsible;
    String originalText;
    Instant createdAt;
    String createdBy;
    Map<String, Object> metadata = new HashMap<>();
    
    public WillExpressionBuilder willId(String willId) {
        this.willId = willId;
        return this;
    }
    
    public WillExpressionBuilder type(WillExpression.WillType type) {
        this.type = type;
        return this;
    }
    
    public WillExpressionBuilder status(WillExpression.WillStatus status) {
        this.status = status;
        return this;
    }
    
    public WillExpressionBuilder goal(String goal) {
        this.goal = goal;
        return this;
    }
    
    public WillExpressionBuilder timeline(String timeline) {
        this.timeline = timeline;
        return this;
    }
    
    public WillExpressionBuilder priority(int priority) {
        this.priority = priority;
        return this;
    }
    
    public WillExpressionBuilder constraints(List<String> constraints) {
        this.constraints = constraints != null ? constraints : new ArrayList<>();
        return this;
    }
    
    public WillExpressionBuilder addConstraint(String constraint) {
        this.constraints.add(constraint);
        return this;
    }
    
    public WillExpressionBuilder action(String action) {
        this.action = action;
        return this;
    }
    
    public WillExpressionBuilder object(String object) {
        this.object = object;
        return this;
    }
    
    public WillExpressionBuilder resourceRequirements(String resourceRequirements) {
        this.resourceRequirements = resourceRequirements;
        return this;
    }
    
    public WillExpressionBuilder deadline(String deadline) {
        this.deadline = deadline;
        return this;
    }
    
    public WillExpressionBuilder responsible(String responsible) {
        this.responsible = responsible;
        return this;
    }
    
    public WillExpressionBuilder originalText(String originalText) {
        this.originalText = originalText;
        return this;
    }
    
    public WillExpressionBuilder createdAt(Instant createdAt) {
        this.createdAt = createdAt;
        return this;
    }
    
    public WillExpressionBuilder createdBy(String createdBy) {
        this.createdBy = createdBy;
        return this;
    }
    
    public WillExpressionBuilder metadata(Map<String, Object> metadata) {
        this.metadata = metadata != null ? metadata : new HashMap<>();
        return this;
    }
    
    public WillExpressionBuilder addMetadata(String key, Object value) {
        this.metadata.put(key, value);
        return this;
    }
    
    public WillExpression build() {
        if (willId == null) {
            willId = "will-" + System.currentTimeMillis();
        }
        if (createdAt == null) {
            createdAt = Instant.now();
        }
        return new WillExpressionImpl(this);
    }
}

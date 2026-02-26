package net.ooder.sdk.will;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Map;

class WillExpressionImpl implements WillExpression {
    
    private final String willId;
    private final WillType type;
    private WillStatus status;
    private final String goal;
    private final String timeline;
    private final int priority;
    private final List<String> constraints;
    private final String action;
    private final String object;
    private final String resourceRequirements;
    private final String deadline;
    private final String responsible;
    private final String originalText;
    private final Instant createdAt;
    private final String createdBy;
    private final Map<String, Object> metadata;
    
    WillExpressionImpl(WillExpressionBuilder builder) {
        this.willId = builder.willId;
        this.type = builder.type;
        this.status = builder.status;
        this.goal = builder.goal;
        this.timeline = builder.timeline;
        this.priority = builder.priority;
        this.constraints = Collections.unmodifiableList(builder.constraints);
        this.action = builder.action;
        this.object = builder.object;
        this.resourceRequirements = builder.resourceRequirements;
        this.deadline = builder.deadline;
        this.responsible = builder.responsible;
        this.originalText = builder.originalText;
        this.createdAt = builder.createdAt;
        this.createdBy = builder.createdBy;
        this.metadata = Collections.unmodifiableMap(builder.metadata);
    }
    
    @Override
    public String getWillId() { return willId; }
    
    @Override
    public WillType getType() { return type; }
    
    @Override
    public WillStatus getStatus() { return status; }
    
    @Override
    public void setStatus(WillStatus status) { this.status = status; }
    
    @Override
    public String getGoal() { return goal; }
    
    @Override
    public String getTimeline() { return timeline; }
    
    @Override
    public int getPriority() { return priority; }
    
    @Override
    public List<String> getConstraints() { return constraints; }
    
    @Override
    public String getAction() { return action; }
    
    @Override
    public String getObject() { return object; }
    
    @Override
    public String getResourceRequirements() { return resourceRequirements; }
    
    @Override
    public String getDeadline() { return deadline; }
    
    @Override
    public String getResponsible() { return responsible; }
    
    @Override
    public String getOriginalText() { return originalText; }
    
    @Override
    public Instant getCreatedAt() { return createdAt; }
    
    @Override
    public String getCreatedBy() { return createdBy; }
    
    @Override
    public Map<String, Object> getMetadata() { return metadata; }
    
    @Override
    public String toString() {
        return "WillExpression{" +
            "willId='" + willId + '\'' +
            ", type=" + type +
            ", status=" + status +
            ", goal='" + goal + '\'' +
            ", priority=" + priority +
            '}';
    }
}

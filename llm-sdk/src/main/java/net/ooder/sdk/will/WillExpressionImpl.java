package net.ooder.sdk.will;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 意志表达式实现类
 */
class WillExpressionImpl implements WillExpression {
    
    private String willId;
    private WillType type;
    private WillStatus status;
    private String goal;
    private String timeline;
    private int priority;
    private Priority priorityLevel;
    private List<String> constraints;
    private String action;
    private String target;
    private String object;
    private String description;
    private String resourceRequirements;
    private List<String> resources;
    private String deadline;
    private String responsible;
    private String originalText;
    private String naturalLanguage;
    private Instant createdAt;
    private String createdBy;
    private Map<String, Object> metadata;
    private Timeline timelineObj;
    private List<String> successCriteria;
    
    /**
     * 默认构造函数
     */
    WillExpressionImpl() {
        this.status = WillStatus.PENDING;
        this.priority = 2;
        this.priorityLevel = Priority.MEDIUM;
    }
    
    /**
     * 通过 Builder 构造
     */
    WillExpressionImpl(WillExpressionBuilder builder) {
        this.willId = builder.willId;
        this.type = builder.type;
        this.status = builder.status;
        this.goal = builder.goal;
        this.timeline = builder.timeline;
        this.priority = builder.priority;
        this.constraints = builder.constraints != null ? 
            Collections.unmodifiableList(builder.constraints) : Collections.emptyList();
        this.action = builder.action;
        this.object = builder.object;
        this.resourceRequirements = builder.resourceRequirements;
        this.deadline = builder.deadline;
        this.responsible = builder.responsible;
        this.originalText = builder.originalText;
        this.createdAt = builder.createdAt;
        this.createdBy = builder.createdBy;
        this.metadata = builder.metadata != null ? 
            Collections.unmodifiableMap(builder.metadata) : Collections.emptyMap();
    }
    
    @Override
    public String getWillId() { return willId; }
    
    @Override
    public void setWillId(String willId) { this.willId = willId; }
    
    @Override
    public WillType getType() { return type; }
    
    @Override
    public void setType(WillType type) { this.type = type; }
    
    @Override
    public WillStatus getStatus() { return status; }
    
    @Override
    public void setStatus(WillStatus status) { this.status = status; }
    
    @Override
    public String getGoal() { return goal; }
    
    @Override
    public void setGoal(String goal) { this.goal = goal; }
    
    @Override
    public String getTimeline() { return timeline; }
    
    @Override
    public void setTimeline(String timeline) { this.timeline = timeline; }
    
    @Override
    public int getPriority() { return priority; }
    
    @Override
    public void setPriority(int priority) { this.priority = priority; }
    
    @Override
    public Priority getPriorityLevel() { return priorityLevel; }
    
    @Override
    public void setPriorityLevel(Priority priority) { this.priorityLevel = priority; }
    
    @Override
    public List<String> getConstraints() { return constraints; }
    
    @Override
    public void setConstraints(List<String> constraints) { this.constraints = constraints; }
    
    @Override
    public String getAction() { return action; }
    
    @Override
    public void setAction(String action) { this.action = action; }
    
    @Override
    public String getTarget() { return target; }
    
    @Override
    public void setTarget(String target) { this.target = target; }
    
    @Override
    public String getObject() { return object; }
    
    @Override
    public void setObject(String object) { this.object = object; }
    
    @Override
    public String getDescription() { return description; }
    
    @Override
    public void setDescription(String description) { this.description = description; }
    
    @Override
    public String getResourceRequirements() { return resourceRequirements; }
    
    @Override
    public void setResourceRequirements(String resourceRequirements) { 
        this.resourceRequirements = resourceRequirements; 
    }
    
    @Override
    public List<String> getResources() { return resources; }
    
    @Override
    public void setResources(List<String> resources) { this.resources = resources; }
    
    @Override
    public String getDeadline() { return deadline; }
    
    @Override
    public void setDeadline(String deadline) { this.deadline = deadline; }
    
    @Override
    public String getResponsible() { return responsible; }
    
    @Override
    public void setResponsible(String responsible) { this.responsible = responsible; }
    
    @Override
    public String getOriginalText() { return originalText; }
    
    @Override
    public void setOriginalText(String originalText) { this.originalText = originalText; }
    
    @Override
    public String getNaturalLanguage() { return naturalLanguage; }
    
    @Override
    public void setNaturalLanguage(String naturalLanguage) { this.naturalLanguage = naturalLanguage; }
    
    @Override
    public Instant getCreatedAt() { return createdAt; }
    
    @Override
    public void setCreatedAt(long createdAt) { this.createdAt = Instant.ofEpochMilli(createdAt); }
    
    @Override
    public String getCreatedBy() { return createdBy; }
    
    @Override
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
    
    @Override
    public Map<String, Object> getMetadata() { return metadata; }
    
    @Override
    public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }
    
    @Override
    public Timeline getTimelineObj() { return timelineObj; }
    
    @Override
    public void setTimelineObj(Timeline timeline) { this.timelineObj = timeline; }
    
    @Override
    public List<String> getSuccessCriteria() { return successCriteria; }
    
    @Override
    public void setSuccessCriteria(List<String> successCriteria) { 
        this.successCriteria = successCriteria; 
    }
    
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

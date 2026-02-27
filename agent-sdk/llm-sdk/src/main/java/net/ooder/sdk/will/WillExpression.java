package net.ooder.sdk.will;

import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * 意志表达式接口
 * 表示用户的意图、目标或期望
 */
public interface WillExpression {
    
    String getWillId();
    void setWillId(String willId);
    
    WillType getType();
    void setType(WillType type);
    
    WillStatus getStatus();
    void setStatus(WillStatus status);
    
    String getGoal();
    void setGoal(String goal);
    
    String getTimeline();
    void setTimeline(String timeline);
    
    int getPriority();
    void setPriority(int priority);
    
    Priority getPriorityLevel();
    void setPriorityLevel(Priority priority);
    
    List<String> getConstraints();
    void setConstraints(List<String> constraints);
    
    String getAction();
    void setAction(String action);
    
    String getTarget();
    void setTarget(String target);
    
    String getObject();
    void setObject(String object);
    
    String getDescription();
    void setDescription(String description);
    
    String getResourceRequirements();
    void setResourceRequirements(String resourceRequirements);
    
    List<String> getResources();
    void setResources(List<String> resources);
    
    String getDeadline();
    void setDeadline(String deadline);
    
    String getResponsible();
    void setResponsible(String responsible);
    
    String getOriginalText();
    void setOriginalText(String originalText);
    
    String getNaturalLanguage();
    void setNaturalLanguage(String naturalLanguage);
    
    Instant getCreatedAt();
    void setCreatedAt(long createdAt);
    
    String getCreatedBy();
    void setCreatedBy(String createdBy);
    
    Map<String, Object> getMetadata();
    void setMetadata(Map<String, Object> metadata);
    
    Timeline getTimelineObj();
    void setTimelineObj(Timeline timeline);
    
    List<String> getSuccessCriteria();
    void setSuccessCriteria(List<String> successCriteria);
    
    /**
     * 意志类型
     */
    enum WillType {
        STRATEGIC,   // 战略目标
        TACTICAL,    // 战术目标
        OPERATIONAL, // 操作目标
        TECHNICAL,   // 技术目标
        EXECUTION    // 执行目标
    }
    
    /**
     * 意志状态
     */
    enum WillStatus {
        PENDING,      // 待处理
        EXPRESSED,    // 已表达
        UNDERSTOOD,   // 已理解
        TRANSFORMED,  // 已转换
        EXECUTING,    // 执行中
        COMPLETED,    // 已完成
        FAILED,       // 失败
        CANCELLED     // 已取消
    }
    
    /**
     * 优先级枚举
     */
    enum Priority {
        LOW(1),
        MEDIUM(2),
        HIGH(3),
        CRITICAL(4);
        
        private final int value;
        
        Priority(int value) {
            this.value = value;
        }
        
        public int getValue() {
            return value;
        }
    }
    
    /**
     * 时间线
     */
    class Timeline {
        private long startTime;
        private long endTime;
        private List<String> milestones;
        
        public long getStartTime() { return startTime; }
        public void setStartTime(long startTime) { this.startTime = startTime; }
        
        public long getEndTime() { return endTime; }
        public void setEndTime(long endTime) { this.endTime = endTime; }
        
        public List<String> getMilestones() { return milestones; }
        public void setMilestones(List<String> milestones) { this.milestones = milestones; }
    }
    
    static WillExpressionBuilder builder() {
        return new WillExpressionBuilder();
    }
}

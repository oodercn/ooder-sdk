package net.ooder.scene.monitor;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * 场景流程管理接口
 * 提供场景启动流程的跟踪和控制功能
 *
 * @author ooder
 * @since 2.3
 */
public interface SceneFlowManager {
    
    /**
     * 启动场景并跟踪流程
     * @param sceneId 场景ID
     * @return 流程状态
     */
    CompletableFuture<FlowStatus> startSceneWithFlow(String sceneId);
    
    /**
     * 获取流程状态
     * @param flowId 流程ID
     * @return 流程状态
     */
    CompletableFuture<FlowStatus> getFlowStatus(String flowId);
    
    /**
     * 获取流程步骤
     * @param flowId 流程ID
     * @return 流程步骤列表
     */
    CompletableFuture<List<FlowStep>> getFlowSteps(String flowId);
    
    /**
     * 暂停流程
     * @param flowId 流程ID
     * @return 是否成功
     */
    CompletableFuture<Boolean> pauseFlow(String flowId);
    
    /**
     * 恢复流程
     * @param flowId 流程ID
     * @return 是否成功
     */
    CompletableFuture<Boolean> resumeFlow(String flowId);
    
    /**
     * 回滚流程
     * @param flowId 流程ID
     * @return 是否成功
     */
    CompletableFuture<Boolean> rollbackFlow(String flowId);
    
    /**
     * 获取流程日志
     * @param flowId 流程ID
     * @param limit 限制条数
     * @return 流程日志列表
     */
    CompletableFuture<List<FlowLog>> getFlowLogs(String flowId, int limit);
}

/**
 * 流程状态
 */
class FlowStatus {
    private String flowId;
    private String sceneId;
    private String status;        // running, completed, failed, paused
    private int progress;         // 0-100
    private long startTime;
    private long estimatedEndTime;
    private List<FlowStep> steps;
    
    // Getters and Setters
    public String getFlowId() { return flowId; }
    public void setFlowId(String flowId) { this.flowId = flowId; }
    public String getSceneId() { return sceneId; }
    public void setSceneId(String sceneId) { this.sceneId = sceneId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public int getProgress() { return progress; }
    public void setProgress(int progress) { this.progress = progress; }
    public long getStartTime() { return startTime; }
    public void setStartTime(long startTime) { this.startTime = startTime; }
    public long getEstimatedEndTime() { return estimatedEndTime; }
    public void setEstimatedEndTime(long estimatedEndTime) { this.estimatedEndTime = estimatedEndTime; }
    public List<FlowStep> getSteps() { return steps; }
    public void setSteps(List<FlowStep> steps) { this.steps = steps; }
}

/**
 * 流程步骤
 */
class FlowStep {
    private int id;
    private String title;
    private String description;
    private String status;        // pending, running, completed, failed
    private long startTime;
    private long endTime;
    private List<FlowSubstep> substeps;
    
    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public long getStartTime() { return startTime; }
    public void setStartTime(long startTime) { this.startTime = startTime; }
    public long getEndTime() { return endTime; }
    public void setEndTime(long endTime) { this.endTime = endTime; }
    public List<FlowSubstep> getSubsteps() { return substeps; }
    public void setSubsteps(List<FlowSubstep> substeps) { this.substeps = substeps; }
}

/**
 * 流程子步骤
 */
class FlowSubstep {
    private String name;
    private String status;
    private String message;
    
    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}

/**
 * 流程日志
 */
class FlowLog {
    private long timestamp;
    private String level;     // INFO, DEBUG, WARN, ERROR
    private String message;
    private String step;      // 所属步骤
    
    // Getters and Setters
    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    public String getLevel() { return level; }
    public void setLevel(String level) { this.level = level; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public String getStep() { return step; }
    public void setStep(String step) { this.step = step; }
}

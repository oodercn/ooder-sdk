package net.ooder.scene.core.activation.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 激活步骤
 *
 * <p>表示激活流程中的单个步骤，包含步骤配置和执行状态</p>
 *
 * @author Ooder Team
 * @version 3.0.1
 * @since 3.0.1
 */
public class ActivationStep implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private String stepId;
    private String name;
    private String description;
    private StepStatus status;
    private boolean required;
    private boolean skippable;
    private boolean autoExecute;
    private Map<String, Object> data;
    private String error;
    private long startTime;
    private long endTime;
    private int order;
    
    public ActivationStep() {
        this.status = StepStatus.PENDING;
        this.required = true;
        this.skippable = false;
        this.autoExecute = false;
        this.data = new HashMap<>();
    }
    
    public ActivationStep(String stepId, String name) {
        this();
        this.stepId = stepId;
        this.name = name;
    }
    
    public void start() {
        this.status = StepStatus.IN_PROGRESS;
        this.startTime = System.currentTimeMillis();
    }
    
    public void complete(Map<String, Object> data) {
        this.status = StepStatus.COMPLETED;
        this.endTime = System.currentTimeMillis();
        if (data != null) {
            this.data.putAll(data);
        }
    }
    
    public void complete() {
        complete(null);
    }
    
    public void skip() {
        this.status = StepStatus.SKIPPED;
        this.endTime = System.currentTimeMillis();
    }
    
    public void fail(String error) {
        this.status = StepStatus.FAILED;
        this.error = error;
        this.endTime = System.currentTimeMillis();
    }
    
    public boolean isCompleted() {
        return status == StepStatus.COMPLETED || status == StepStatus.SKIPPED;
    }
    
    public boolean isInProgress() {
        return status == StepStatus.IN_PROGRESS;
    }
    
    public boolean isPending() {
        return status == StepStatus.PENDING;
    }
    
    public boolean isFailed() {
        return status == StepStatus.FAILED;
    }
    
    public long getDuration() {
        if (startTime > 0 && endTime > 0) {
            return endTime - startTime;
        }
        return 0;
    }
    
    public String getStepId() {
        return stepId;
    }
    
    public void setStepId(String stepId) {
        this.stepId = stepId;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public StepStatus getStatus() {
        return status;
    }
    
    public void setStatus(StepStatus status) {
        this.status = status;
    }
    
    public boolean isRequired() {
        return required;
    }
    
    public void setRequired(boolean required) {
        this.required = required;
    }
    
    public boolean isSkippable() {
        return skippable;
    }
    
    public void setSkippable(boolean skippable) {
        this.skippable = skippable;
    }
    
    public boolean isAutoExecute() {
        return autoExecute;
    }
    
    public void setAutoExecute(boolean autoExecute) {
        this.autoExecute = autoExecute;
    }
    
    public Map<String, Object> getData() {
        return data;
    }
    
    public void setData(Map<String, Object> data) {
        this.data = data != null ? data : new HashMap<>();
    }
    
    public String getError() {
        return error;
    }
    
    public void setError(String error) {
        this.error = error;
    }
    
    public long getStartTime() {
        return startTime;
    }
    
    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }
    
    public long getEndTime() {
        return endTime;
    }
    
    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }
    
    public int getOrder() {
        return order;
    }
    
    public void setOrder(int order) {
        this.order = order;
    }
    
    @Override
    public String toString() {
        return "ActivationStep{" +
                "stepId='" + stepId + '\'' +
                ", name='" + name + '\'' +
                ", status=" + status +
                ", required=" + required +
                ", autoExecute=" + autoExecute +
                '}';
    }
    
    /**
     * 步骤状态
     */
    public enum StepStatus {
        PENDING("待执行"),
        IN_PROGRESS("执行中"),
        COMPLETED("已完成"),
        FAILED("失败"),
        SKIPPED("已跳过");
        
        private final String description;
        
        StepStatus(String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return description;
        }
    }
}

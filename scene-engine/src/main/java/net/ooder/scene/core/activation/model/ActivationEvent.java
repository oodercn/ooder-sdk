package net.ooder.scene.core.activation.model;

import java.io.Serializable;

/**
 * 激活事件
 *
 * <p>表示激活流程中发生的各种事件，用于事件监听和通知</p>
 *
 * @author Ooder Team
 * @version 3.0.1
 * @since 3.0.1
 */
public class ActivationEvent implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private String processId;
    private String sceneGroupId;
    private ActivationEventType eventType;
    private String stepId;
    private String message;
    private long timestamp;
    private Object data;
    
    public ActivationEvent() {
        this.timestamp = System.currentTimeMillis();
    }
    
    public ActivationEvent(String processId, ActivationEventType eventType) {
        this();
        this.processId = processId;
        this.eventType = eventType;
    }
    
    public static ActivationEvent processStarted(String processId, String sceneGroupId) {
        ActivationEvent event = new ActivationEvent(processId, ActivationEventType.PROCESS_STARTED);
        event.setSceneGroupId(sceneGroupId);
        event.setMessage("激活流程已启动");
        return event;
    }
    
    public static ActivationEvent stepStarted(String processId, String stepId, String stepName) {
        ActivationEvent event = new ActivationEvent(processId, ActivationEventType.STEP_STARTED);
        event.setStepId(stepId);
        event.setMessage("步骤开始执行: " + stepName);
        return event;
    }
    
    public static ActivationEvent stepCompleted(String processId, String stepId, String stepName) {
        ActivationEvent event = new ActivationEvent(processId, ActivationEventType.STEP_COMPLETED);
        event.setStepId(stepId);
        event.setMessage("步骤执行完成: " + stepName);
        return event;
    }
    
    public static ActivationEvent stepFailed(String processId, String stepId, String error) {
        ActivationEvent event = new ActivationEvent(processId, ActivationEventType.STEP_FAILED);
        event.setStepId(stepId);
        event.setMessage("步骤执行失败: " + error);
        return event;
    }
    
    public static ActivationEvent stepSkipped(String processId, String stepId, String stepName) {
        ActivationEvent event = new ActivationEvent(processId, ActivationEventType.STEP_SKIPPED);
        event.setStepId(stepId);
        event.setMessage("步骤已跳过: " + stepName);
        return event;
    }
    
    public static ActivationEvent keyGenerated(String processId, String keyId) {
        ActivationEvent event = new ActivationEvent(processId, ActivationEventType.KEY_GENERATED);
        event.setMessage("密钥已生成: " + keyId);
        return event;
    }
    
    public static ActivationEvent networkActionExecuted(String processId, String actionId, boolean success) {
        ActivationEvent event = new ActivationEvent(processId, ActivationEventType.NETWORK_ACTION_EXECUTED);
        event.setMessage("网络动作执行" + (success ? "成功" : "失败") + ": " + actionId);
        return event;
    }
    
    public static ActivationEvent processCompleted(String processId, String sceneGroupId) {
        ActivationEvent event = new ActivationEvent(processId, ActivationEventType.PROCESS_COMPLETED);
        event.setSceneGroupId(sceneGroupId);
        event.setMessage("激活流程已完成");
        return event;
    }
    
    public static ActivationEvent processFailed(String processId, String error) {
        ActivationEvent event = new ActivationEvent(processId, ActivationEventType.PROCESS_FAILED);
        event.setMessage("激活流程失败: " + error);
        return event;
    }
    
    public static ActivationEvent processCancelled(String processId) {
        ActivationEvent event = new ActivationEvent(processId, ActivationEventType.PROCESS_CANCELLED);
        event.setMessage("激活流程已取消");
        return event;
    }
    
    public String getProcessId() {
        return processId;
    }
    
    public void setProcessId(String processId) {
        this.processId = processId;
    }
    
    public String getSceneGroupId() {
        return sceneGroupId;
    }
    
    public void setSceneGroupId(String sceneGroupId) {
        this.sceneGroupId = sceneGroupId;
    }
    
    public ActivationEventType getEventType() {
        return eventType;
    }
    
    public void setEventType(ActivationEventType eventType) {
        this.eventType = eventType;
    }
    
    public String getStepId() {
        return stepId;
    }
    
    public void setStepId(String stepId) {
        this.stepId = stepId;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public long getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
    
    public Object getData() {
        return data;
    }
    
    public void setData(Object data) {
        this.data = data;
    }
    
    @Override
    public String toString() {
        return "ActivationEvent{" +
                "processId='" + processId + '\'' +
                ", eventType=" + eventType +
                ", stepId='" + stepId + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
    
    /**
     * 激活事件类型
     */
    public enum ActivationEventType {
        PROCESS_STARTED("流程开始"),
        STEP_STARTED("步骤开始"),
        STEP_COMPLETED("步骤完成"),
        STEP_FAILED("步骤失败"),
        STEP_SKIPPED("步骤跳过"),
        KEY_GENERATED("密钥生成"),
        NETWORK_ACTION_EXECUTED("网络动作执行"),
        PROCESS_COMPLETED("流程完成"),
        PROCESS_FAILED("流程失败"),
        PROCESS_CANCELLED("流程取消");
        
        private final String description;
        
        ActivationEventType(String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return description;
        }
    }
}

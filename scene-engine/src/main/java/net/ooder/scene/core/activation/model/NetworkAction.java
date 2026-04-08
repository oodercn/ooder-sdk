package net.ooder.scene.core.activation.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 网络动作
 *
 * <p>表示激活完成后需要执行的网络相关动作，如通知、更新等</p>
 *
 * @author Ooder Team
 * @version 3.0.1
 * @since 3.0.1
 */
public class NetworkAction implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private String actionId;
    private String actionName;
    private String actionType;
    private ActionStatus status;
    private String message;
    private long timestamp;
    private Map<String, Object> result;
    private Map<String, Object> config;
    private int retryCount;
    private int maxRetries;
    private String errorMessage;
    
    public NetworkAction() {
        this.status = ActionStatus.PENDING;
        this.result = new HashMap<>();
        this.config = new HashMap<>();
        this.retryCount = 0;
        this.maxRetries = 3;
    }
    
    public NetworkAction(String actionId, String actionName, String actionType) {
        this();
        this.actionId = actionId;
        this.actionName = actionName;
        this.actionType = actionType;
    }
    
    public void start() {
        this.status = ActionStatus.RUNNING;
        this.timestamp = System.currentTimeMillis();
    }
    
    public void complete(Map<String, Object> result) {
        this.status = ActionStatus.COMPLETED;
        this.timestamp = System.currentTimeMillis();
        if (result != null) {
            this.result.putAll(result);
        }
    }
    
    public void complete() {
        complete(null);
    }
    
    public void skip(String reason) {
        this.status = ActionStatus.SKIPPED;
        this.message = reason;
        this.timestamp = System.currentTimeMillis();
    }
    
    public void fail(String errorMessage) {
        this.status = ActionStatus.FAILED;
        this.errorMessage = errorMessage;
        this.timestamp = System.currentTimeMillis();
    }
    
    public boolean retry() {
        if (retryCount < maxRetries) {
            retryCount++;
            this.status = ActionStatus.PENDING;
            return true;
        }
        return false;
    }
    
    public boolean isCompleted() {
        return status == ActionStatus.COMPLETED || status == ActionStatus.SKIPPED;
    }
    
    public boolean isPending() {
        return status == ActionStatus.PENDING;
    }
    
    public boolean isRunning() {
        return status == ActionStatus.RUNNING;
    }
    
    public boolean isFailed() {
        return status == ActionStatus.FAILED;
    }
    
    public boolean canRetry() {
        return retryCount < maxRetries;
    }
    
    public String getActionId() {
        return actionId;
    }
    
    public void setActionId(String actionId) {
        this.actionId = actionId;
    }
    
    public String getActionName() {
        return actionName;
    }
    
    public void setActionName(String actionName) {
        this.actionName = actionName;
    }
    
    public String getActionType() {
        return actionType;
    }
    
    public void setActionType(String actionType) {
        this.actionType = actionType;
    }
    
    public ActionStatus getStatus() {
        return status;
    }
    
    public void setStatus(ActionStatus status) {
        this.status = status;
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
    
    public Map<String, Object> getResult() {
        return result;
    }
    
    public void setResult(Map<String, Object> result) {
        this.result = result != null ? result : new HashMap<>();
    }
    
    public Map<String, Object> getConfig() {
        return config;
    }
    
    public void setConfig(Map<String, Object> config) {
        this.config = config != null ? config : new HashMap<>();
    }
    
    public int getRetryCount() {
        return retryCount;
    }
    
    public void setRetryCount(int retryCount) {
        this.retryCount = retryCount;
    }
    
    public int getMaxRetries() {
        return maxRetries;
    }
    
    public void setMaxRetries(int maxRetries) {
        this.maxRetries = maxRetries;
    }
    
    public String getErrorMessage() {
        return errorMessage;
    }
    
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
    
    @Override
    public String toString() {
        return "NetworkAction{" +
                "actionId='" + actionId + '\'' +
                ", actionName='" + actionName + '\'' +
                ", actionType='" + actionType + '\'' +
                ", status=" + status +
                '}';
    }
    
    /**
     * 动作状态
     */
    public enum ActionStatus {
        PENDING("待执行"),
        RUNNING("执行中"),
        COMPLETED("已完成"),
        FAILED("失败"),
        SKIPPED("已跳过");
        
        private final String description;
        
        ActionStatus(String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return description;
        }
    }
    
    /**
     * 动作类型常量
     */
    public static final String TYPE_NOTIFICATION = "NOTIFICATION";
    public static final String TYPE_UPDATE = "UPDATE";
    public static final String TYPE_SYNC = "SYNC";
    public static final String TYPE_CALLBACK = "CALLBACK";
}

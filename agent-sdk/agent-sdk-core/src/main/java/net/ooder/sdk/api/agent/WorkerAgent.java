package net.ooder.sdk.api.agent;

import net.ooder.sdk.api.skill.SkillService;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public interface WorkerAgent extends Agent {
    
    String getWorkerName();
    
    String getDescription();
    
    String getSceneId();
    
    String getSkillId();
    
    List<String> getCapabilities();
    
    WorkerAgentStatus getWorkerStatus();
    
    String getPreferredDevice();
    
    void setPreferredDevice(String deviceId);
    
    CompletableFuture<Object> execute(String capId, Map<String, Object> params);
    
    CompletableFuture<Object> executeAsync(String capId, Map<String, Object> params);
    
    void setIdle();
    
    void setBusy();
    
    void setError(String errorMessage);
    
    boolean isIdle();
    
    boolean isBusy();
    
    boolean hasError();
    
    String getCurrentTaskId();
    
    void setCurrentTaskId(String taskId);
    
    SkillService getSkill();
    
    void setSkill(SkillService skill);
    
    enum WorkerAgentStatus {
        IDLE("idle", "空闲"),
        BUSY("busy", "忙碌"),
        ERROR("error", "错误"),
        OFFLINE("offline", "离线");
        
        private final String code;
        private final String description;
        
        WorkerAgentStatus(String code, String description) {
            this.code = code;
            this.description = description;
        }
        
        public String getCode() { return code; }
        public String getDescription() { return description; }
        
        public static WorkerAgentStatus fromCode(String code) {
            for (WorkerAgentStatus status : values()) {
                if (status.code.equalsIgnoreCase(code)) {
                    return status;
                }
            }
            throw new IllegalArgumentException("Unknown worker agent status: " + code);
        }
    }
}

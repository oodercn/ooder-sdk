package net.ooder.sdk.api.agent;

import net.ooder.sdk.api.skill.Capability;
import net.ooder.sdk.api.skill.SkillService;
import net.ooder.sdk.binding.BindingManager;
import net.ooder.sdk.binding.DeviceBinding;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public interface SceneAgent extends Agent {
    
    String getSceneId();
    
    SceneAgentType getSceneAgentType();
    
    SceneAgentStatus getSceneStatus();
    
    List<Capability> getCapabilities();
    
    List<SkillService> getMountedSkills();
    
    void mountSkill(SkillService skill);
    
    void unmountSkill(String skillId);
    
    CompletableFuture<Object> invokeCapability(String capId, Map<String, Object> params);
    
    void joinSceneGroup(String groupId);
    
    void leaveSceneGroup();
    
    String getSceneGroupId();
    
    boolean isPrimary();
    
    boolean isBackup();
    
    void promoteToPrimary();
    
    void demoteToBackup();
    
    void suspend();
    
    void resume();
    
    Map<String, Object> getSharedState();
    
    void updateSharedState(Map<String, Object> state);
    
    void addWorkerAgent(WorkerAgent worker);
    
    void removeWorkerAgent(String workerId);
    
    WorkerAgent getWorkerAgent(String workerId);
    
    List<WorkerAgent> getWorkerAgents();
    
    CompletableFuture<Object> dispatchToWorker(String workerId, String capId, Map<String, Object> params);
    
    DeviceBinding bindDevices(String sourceDevice, String sourceCap, 
                              String targetDevice, String targetCap,
                              DeviceBinding.BindingType bindingType);
    
    void unbindDevices(String bindingId);
    
    List<DeviceBinding> getDeviceBindings();
    
    List<DeviceBinding> getDeviceBindingsByDevice(String deviceId);
    
    BindingManager getBindingManager();
    
    enum SceneAgentType {
        PRIMARY("primary", "主 Agent"),
        BACKUP("backup", "备份 Agent"),
        COLLABORATIVE("collaborative", "协作 Agent");
        
        private final String code;
        private final String description;
        
        SceneAgentType(String code, String description) {
            this.code = code;
            this.description = description;
        }
        
        public String getCode() { return code; }
        public String getDescription() { return description; }
        
        public static SceneAgentType fromCode(String code) {
            for (SceneAgentType type : values()) {
                if (type.code.equalsIgnoreCase(code)) {
                    return type;
                }
            }
            throw new IllegalArgumentException("Unknown scene agent type: " + code);
        }
    }
    
    enum SceneAgentStatus {
        INITIALIZING("initializing", "初始化中"),
        ACTIVE("active", "活动"),
        SUSPENDED("suspended", "暂停"),
        STOPPED("stopped", "停止"),
        ERROR("error", "错误"),
        FAILOVER("failover", "故障切换中");
        
        private final String code;
        private final String description;
        
        SceneAgentStatus(String code, String description) {
            this.code = code;
            this.description = description;
        }
        
        public String getCode() { return code; }
        public String getDescription() { return description; }
        
        public static SceneAgentStatus fromCode(String code) {
            for (SceneAgentStatus status : values()) {
                if (status.code.equalsIgnoreCase(code)) {
                    return status;
                }
            }
            throw new IllegalArgumentException("Unknown scene agent status: " + code);
        }
    }
}

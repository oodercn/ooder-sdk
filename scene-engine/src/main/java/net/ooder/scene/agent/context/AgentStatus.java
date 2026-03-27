package net.ooder.scene.agent.context;

/**
 * Agent 状态枚举
 *
 * @author Ooder Team
 * @version 3.0.1
 * @since 3.0.1
 */
public enum AgentStatus {
    
    ONLINE("online", "在线"),
    
    OFFLINE("offline", "离线"),
    
    IDLE("idle", "空闲"),
    
    BUSY("busy", "忙碌"),
    
    ERROR("error", "错误");
    
    private final String code;
    private final String description;
    
    AgentStatus(String code, String description) {
        this.code = code;
        this.description = description;
    }
    
    public String getCode() {
        return code;
    }
    
    public String getDescription() {
        return description;
    }
    
    public static AgentStatus fromCode(String code) {
        for (AgentStatus status : values()) {
            if (status.code.equalsIgnoreCase(code)) {
                return status;
            }
        }
        return OFFLINE;
    }
}

package net.ooder.scene.session.unified;

/**
 * 在线状态枚举
 *
 * @author Ooder Team
 * @version 3.0.1
 * @since 3.0.1
 */
public enum OnlineStatus {
    
    ONLINE("online", "在线"),
    
    OFFLINE("offline", "离线"),
    
    IDLE("idle", "空闲"),
    
    BUSY("busy", "忙碌"),
    
    AWAY("away", "离开");
    
    private final String code;
    private final String description;
    
    OnlineStatus(String code, String description) {
        this.code = code;
        this.description = description;
    }
    
    public String getCode() {
        return code;
    }
    
    public String getDescription() {
        return description;
    }
    
    public static OnlineStatus fromCode(String code) {
        for (OnlineStatus status : values()) {
            if (status.code.equalsIgnoreCase(code)) {
                return status;
            }
        }
        return OFFLINE;
    }
}

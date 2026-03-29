package net.ooder.scene.skill.notification;

/**
 * 通知类型枚举
 * 
 * @author SE Team
 * @version 2.3.1
 * @since 2.3.1
 */
public enum NotificationType {
    
    INFO("info", "信息通知"),
    WARNING("warning", "警告通知"),
    ERROR("error", "错误通知"),
    SUCCESS("success", "成功通知"),
    INVITATION("invitation", "邀请通知"),
    APPROVAL("approval", "审批通知"),
    REMINDER("reminder", "提醒通知"),
    SYSTEM("system", "系统通知");
    
    private final String code;
    private final String displayName;
    
    NotificationType(String code, String displayName) {
        this.code = code;
        this.displayName = displayName;
    }
    
    public String getCode() {
        return code;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public static NotificationType fromCode(String code) {
        if (code == null) {
            return null;
        }
        for (NotificationType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        return null;
    }
}

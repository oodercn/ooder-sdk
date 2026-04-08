package net.ooder.scene.todo;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * 待办类型枚举
 * 
 * <p>定义系统支持的待办类型。</p>
 * 
 * @author SE Team
 * @version 2.3.1
 * @since 2.3.1
 */
public enum TodoType {
    
    INVITATION("invitation", "协作邀请"),
    DELEGATION("delegation", "领导委派"),
    REMINDER("reminder", "待办提醒"),
    APPROVAL("approval", "审批请求"),
    ACTIVATION("activation", "待激活能力"),
    SCENE_NOTIFICATION("scene_notification", "场景通知");
    
    private final String code;
    private final String displayName;
    
    TodoType(String code, String displayName) {
        this.code = code;
        this.displayName = displayName;
    }
    
    @JsonValue
    public String getCode() {
        return code;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public static TodoType fromCode(String code) {
        if (code == null) {
            return null;
        }
        for (TodoType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        return REMINDER;
    }
    
    public static TodoType fromCode(String code, TodoType defaultValue) {
        if (code == null) {
            return defaultValue;
        }
        for (TodoType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        return defaultValue;
    }
}

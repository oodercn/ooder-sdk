package net.ooder.skills.api;

/**
 * 场景类型枚举
 * 定义场景的主要类型：主场景和协作场景
 *
 * @author ooder
 * @since 2.3
 */
public enum SceneType {
    /** 主场景 - 具有核心功能 */
    PRIMARY("primary", "Primary scene with core functionality"),
    /** 协作场景 - 作为依赖的场景 */
    COLLABORATIVE("collaborative", "Collaborative scene as dependency"),
    /** 自动场景 */
    AUTO("auto", "Auto scene"),
    /** 触发场景 */
    TRIGGER("trigger", "Trigger scene");
    
    private final String code;
    private final String description;
    
    SceneType(String code, String description) {
        this.code = code;
        this.description = description;
    }
    
    public String getCode() {
        return code;
    }
    
    public String getDescription() {
        return description;
    }
    
    public boolean isPrimary() {
        return this == PRIMARY;
    }
    
    public boolean isCollaborative() {
        return this == COLLABORATIVE;
    }
    
    public boolean isAuto() {
        return this == AUTO;
    }
    
    public boolean isTrigger() {
        return this == TRIGGER;
    }
    
    public boolean canSelfDrive() {
        return this == AUTO;
    }
    
    public static SceneType fromCode(String code) {
        for (SceneType type : values()) {
            if (type.code.equalsIgnoreCase(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown scene type: " + code);
    }
}

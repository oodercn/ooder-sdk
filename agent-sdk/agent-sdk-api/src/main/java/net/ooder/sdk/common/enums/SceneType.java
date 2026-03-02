package net.ooder.sdk.common.enums;

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
    COLLABORATIVE("collaborative", "Collaborative scene as dependency");
    
    /** 类型编码 */
    private final String code;
    /** 类型描述 */
    private final String description;
    
    /**
     * 构造函数
     * @param code 类型编码
     * @param description 类型描述
     */
    SceneType(String code, String description) {
        this.code = code;
        this.description = description;
    }
    
    /**
     * 获取类型编码
     * @return 类型编码
     */
    public String getCode() {
        return code;
    }
    
    /**
     * 获取类型描述
     * @return 类型描述
     */
    public String getDescription() {
        return description;
    }
    
    /**
     * 判断是否为主场景
     * @return true表示主场景
     */
    public boolean isPrimary() {
        return this == PRIMARY;
    }
    
    /**
     * 判断是否为协作场景
     * @return true表示协作场景
     */
    public boolean isCollaborative() {
        return this == COLLABORATIVE;
    }
    
    /**
     * 根据编码获取场景类型
     * @param code 类型编码
     * @return 场景类型
     * @throws IllegalArgumentException 如果编码未知
     */
    public static SceneType fromCode(String code) {
        for (SceneType type : values()) {
            if (type.code.equalsIgnoreCase(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown scene type: " + code);
    }
}

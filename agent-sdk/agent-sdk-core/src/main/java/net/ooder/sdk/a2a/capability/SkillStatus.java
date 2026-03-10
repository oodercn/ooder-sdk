package net.ooder.sdk.a2a.capability;

/**
 * 技能状态
 *
 * @author Agent-SDK Team
 * @version 3.0
 * @since 3.0
 */
public enum SkillStatus {
    ACTIVE("活跃"),
    INACTIVE("非活跃"),
    ERROR("错误"),
    MAINTENANCE("维护中");
    
    private final String name;
    
    SkillStatus(String name) {
        this.name = name;
    }
    
    public String getName() {
        return name;
    }
}

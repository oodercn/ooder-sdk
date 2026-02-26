package net.ooder.skill.org.api.entity;

/**
 * 角色类型枚举
 * 保持与原有 RoleType 兼容
 *
 * @author Ooder Team
 * @version 2.3
 * @since 2.3.0
 */
public enum RoleType {
    
    /**
     * 系统角色
     */
    SYSTEM,
    
    /**
     * 自定义角色
     */
    CUSTOM,
    
    /**
     * 默认角色
     */
    DEFAULT,
    
    /**
     * 临时角色
     */
    TEMPORARY
}

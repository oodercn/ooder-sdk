package net.ooder.sdk.common.enums;

/**
 * 成员角色枚举
 * 定义场景组成员的角色类型
 *
 * @author ooder
 * @since 2.3
 */
public enum MemberRole {
    /** 主成员 - 具有完全控制权 */
    PRIMARY("primary", "Primary agent with full control"),
    /** 备份成员 - 准备故障转移 */
    BACKUP("backup", "Backup agent ready for failover"),
    /** 观察成员 - 只读访问 */
    OBSERVER("observer", "Observer agent with read-only access"),
    /** 普通成员 - 有限访问权限 */
    MEMBER("member", "Regular member with limited access");

    /** 角色编码 */
    private final String code;
    /** 角色描述 */
    private final String description;
    
    /**
     * 构造函数
     * @param code 角色编码
     * @param description 角色描述
     */
    MemberRole(String code, String description) {
        this.code = code;
        this.description = description;
    }
    
    /**
     * 获取角色编码
     * @return 角色编码
     */
    public String getCode() {
        return code;
    }
    
    /**
     * 获取角色描述
     * @return 角色描述
     */
    public String getDescription() {
        return description;
    }
    
    /**
     * 判断是否为主成员
     * @return true表示主成员
     */
    public boolean isPrimary() {
        return this == PRIMARY;
    }
    
    /**
     * 判断是否为备份成员
     * @return true表示备份成员
     */
    public boolean isBackup() {
        return this == BACKUP;
    }
    
    /**
     * 根据编码获取成员角色
     * @param code 角色编码
     * @return 成员角色
     * @throws IllegalArgumentException 如果编码未知
     */
    public static MemberRole fromCode(String code) {
        for (MemberRole role : values()) {
            if (role.code.equalsIgnoreCase(code)) {
                return role;
            }
        }
        throw new IllegalArgumentException("Unknown member role: " + code);
    }
}

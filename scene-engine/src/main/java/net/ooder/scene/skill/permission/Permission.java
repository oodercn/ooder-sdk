package net.ooder.scene.skill.permission;

/**
 * 权限类型枚举
 *
 * @author ooder
 * @since 2.3
 */
public enum Permission {
    
    READ("read", "读权限", 1),
    WRITE("write", "写权限", 2),
    ADMIN("admin", "管理权限", 3),
    OWNER("owner", "所有者权限", 4);
    
    private final String code;
    private final String description;
    private final int level;
    
    Permission(String code, String description, int level) {
        this.code = code;
        this.description = description;
        this.level = level;
    }
    
    public String getCode() {
        return code;
    }
    
    public String getDescription() {
        return description;
    }
    
    public int getLevel() {
        return level;
    }
    
    public boolean includes(Permission other) {
        return this.level >= other.level;
    }
    
    public static Permission fromCode(String code) {
        for (Permission p : values()) {
            if (p.code.equalsIgnoreCase(code)) {
                return p;
            }
        }
        return null;
    }
}

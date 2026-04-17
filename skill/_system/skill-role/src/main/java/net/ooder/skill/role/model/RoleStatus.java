package net.ooder.skill.role.model;

public enum RoleStatus {
    ACTIVE("ACTIVE", "激活"),
    INACTIVE("INACTIVE", "未激活"),
    DELETED("DELETED", "已删除");

    private final String code;
    private final String name;

    RoleStatus(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() { return code; }
    public String getName() { return name; }

    public static RoleStatus fromCode(String code) {
        if (code == null) return ACTIVE;
        for (RoleStatus status : values()) {
            if (status.code.equals(code) || status.name().equals(code)) {
                return status;
            }
        }
        return ACTIVE;
    }
}

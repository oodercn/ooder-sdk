package net.ooder.skill.role.model;

public enum RoleType {
    SYSTEM("SYSTEM", "系统角色"),
    ORG("ORG", "组织角色"),
    CUSTOM("CUSTOM", "自定义角色");

    private final String code;
    private final String name;

    RoleType(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() { return code; }
    public String getName() { return name; }

    public static RoleType fromCode(String code) {
        if (code == null) return CUSTOM;
        for (RoleType type : values()) {
            if (type.code.equals(code) || type.name().equals(code)) {
                return type;
            }
        }
        return CUSTOM;
    }
}

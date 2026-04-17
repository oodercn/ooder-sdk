package net.ooder.skill.role.model;

public enum PermissionType {
    MENU("MENU", "菜单权限"),
    API("API", "接口权限"),
    DATA("DATA", "数据权限"),
    OPERATION("OPERATION", "操作权限");

    private final String code;
    private final String name;

    PermissionType(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() { return code; }
    public String getName() { return name; }

    public static PermissionType fromCode(String code) {
        if (code == null) return MENU;
        for (PermissionType type : values()) {
            if (type.code.equals(code) || type.name().equals(code)) {
                return type;
            }
        }
        return MENU;
    }
}

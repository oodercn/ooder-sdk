package net.ooder.skill.capability.model;

public enum CapabilityStatus {
    DRAFT("DRAFT", "草稿"),
    REGISTERED("REGISTERED", "已注册"),
    ENABLED("ENABLED", "已启用"),
    DISABLED("DISABLED", "已禁用"),
    DEPRECATED("DEPRECATED", "已废弃"),
    ERROR("ERROR", "错误");

    private final String code;
    private final String name;

    CapabilityStatus(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() { return code; }
    public String getName() { return name; }
    public String getDescription() { return name; }

    public static CapabilityStatus fromCode(String code) {
        if (code == null) return null;
        for (CapabilityStatus status : values()) {
            if (status.code.equals(code) || status.name().equals(code)) {
                return status;
            }
        }
        return REGISTERED;
    }
}

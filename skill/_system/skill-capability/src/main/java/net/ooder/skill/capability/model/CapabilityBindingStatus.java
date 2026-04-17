package net.ooder.skill.capability.model;

public enum CapabilityBindingStatus {
    PENDING("PENDING", "待激活"),
    ACTIVE("ACTIVE", "激活"),
    INACTIVE("INACTIVE", "未激活"),
    ERROR("ERROR", "错误"),
    DISABLED("DISABLED", "已禁用");

    private final String code;
    private final String name;

    CapabilityBindingStatus(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() { return code; }
    public String getName() { return name; }

    public static CapabilityBindingStatus fromCode(String code) {
        if (code == null) return null;
        for (CapabilityBindingStatus status : values()) {
            if (status.code.equals(code) || status.name().equals(code)) {
                return status;
            }
        }
        return PENDING;
    }
}

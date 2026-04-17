package net.ooder.skill.capability.model;

public enum CapabilityProviderType {
    SKILL("SKILL", "技能提供"),
    AGENT("AGENT", "代理提供"),
    EXTERNAL("EXTERNAL", "外部服务"),
    INTERNAL("INTERNAL", "内部服务");

    private final String code;
    private final String name;

    CapabilityProviderType(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() { return code; }
    public String getName() { return name; }

    public static CapabilityProviderType fromCode(String code) {
        if (code == null) return INTERNAL;
        for (CapabilityProviderType type : values()) {
            if (type.code.equals(code) || type.name().equals(code)) {
                return type;
            }
        }
        return INTERNAL;
    }
}

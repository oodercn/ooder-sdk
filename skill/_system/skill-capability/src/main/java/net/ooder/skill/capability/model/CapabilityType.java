package net.ooder.skill.capability.model;

public enum CapabilityType {
    SCENE("SCENE", "场景能力", "ri-apps-line"),
    DRIVER("DRIVER", "驱动能力", "ri-cpu-line"),
    PROVIDER("PROVIDER", "提供者能力", "ri-flashlight-line"),
    CONNECTOR("CONNECTOR", "连接器能力", "ri-plug-line"),
    INTEGRATION("INTEGRATION", "集成能力", "ri-link");

    private final String code;
    private final String name;
    private final String icon;

    CapabilityType(String code, String name, String icon) {
        this.code = code;
        this.name = name;
        this.icon = icon;
    }

    public String getCode() { return code; }
    public String getName() { return name; }
    public String getIcon() { return icon; }
    public String getDescription() { return name; }

    public static CapabilityType fromCode(String code) {
        if (code == null) return null;
        for (CapabilityType type : values()) {
            if (type.code.equals(code) || type.name().equals(code)) {
                return type;
            }
        }
        return PROVIDER;
    }
}

package net.ooder.skill.capability.model;

public enum DriverType {
    TRIGGER("TRIGGER", "触发器"),
    SCHEDULE("SCHEDULE", "定时器"),
    EVENT("EVENT", "事件驱动"),
    MANUAL("MANUAL", "手动");

    private final String code;
    private final String name;

    DriverType(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() { return code; }
    public String getName() { return name; }

    public boolean isTrigger() { return this == TRIGGER; }

    public static DriverType fromCode(String code) {
        if (code == null) return null;
        for (DriverType type : values()) {
            if (type.code.equals(code) || type.name().equals(code)) {
                return type;
            }
        }
        return MANUAL;
    }
}

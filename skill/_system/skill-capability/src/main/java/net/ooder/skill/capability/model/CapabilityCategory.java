package net.ooder.skill.capability.model;

public enum CapabilityCategory {
    LLM("LLM", "大语言模型", 0x10, 0x1F),
    KNOWLEDGE("KNOWLEDGE", "知识管理", 0x20, 0x2F),
    AGENT("AGENT", "代理服务", 0x30, 0x3F),
    INTEGRATION("INTEGRATION", "集成服务", 0x40, 0x4F),
    DATA("DATA", "数据处理", 0x50, 0x5F),
    SECURITY("SECURITY", "安全服务", 0x60, 0x6F),
    MONITOR("MONITOR", "监控服务", 0x70, 0x7F),
    SYS("SYS", "系统服务", 0x00, 0x0F);

    private final String code;
    private final String name;
    private final int addressStart;
    private final int addressEnd;

    CapabilityCategory(String code, String name, int addressStart, int addressEnd) {
        this.code = code;
        this.name = name;
        this.addressStart = addressStart;
        this.addressEnd = addressEnd;
    }

    CapabilityCategory(String code, String name) {
        this(code, name, 0, 0);
    }

    public String getCode() { return code; }
    public String getName() { return name; }
    public int getAddressStart() { return addressStart; }
    public int getAddressEnd() { return addressEnd; }

    public static CapabilityCategory fromCode(String code) {
        if (code == null) return null;
        for (CapabilityCategory cat : values()) {
            if (cat.code.equals(code) || cat.name().equals(code)) {
                return cat;
            }
        }
        return SYS;
    }

    public static CapabilityCategory fromAddress(int address) {
        for (CapabilityCategory cat : values()) {
            if (address >= cat.addressStart && address <= cat.addressEnd) {
                return cat;
            }
        }
        return SYS;
    }
}

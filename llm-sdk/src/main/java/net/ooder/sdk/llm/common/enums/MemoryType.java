package net.ooder.sdk.llm.common.enums;

public enum MemoryType {
    SENSORY("sensory", "感知记忆"),
    WORKING("working", "工作记忆"),
    EPISODIC("episodic", "情景记忆"),
    SEMANTIC("semantic", "语义记忆"),
    PROCEDURAL("procedural", "程序记忆");

    private final String code;
    private final String description;

    MemoryType(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static MemoryType fromCode(String code) {
        for (MemoryType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown memory type: " + code);
    }
}

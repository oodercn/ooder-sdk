package net.ooder.sdk.llm.common.enums;

public enum ResourceType {
    CPU("cpu", "CPU资源"),
    GPU("gpu", "GPU资源"),
    MEMORY("memory", "内存资源"),
    STORAGE("storage", "存储资源");

    private final String code;
    private final String description;

    ResourceType(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static ResourceType fromCode(String code) {
        for (ResourceType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown resource type: " + code);
    }
}

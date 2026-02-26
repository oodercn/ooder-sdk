package net.ooder.sdk.llm.common.enums;

public enum CapabilityType {
    INFERENCE("inference", "推理能力"),
    GENERATION("generation", "生成能力"),
    ANALYSIS("analysis", "分析能力"),
    TRANSLATION("translation", "翻译能力"),
    SUMMARIZATION("summarization", "摘要能力"),
    CLASSIFICATION("classification", "分类能力");

    private final String code;
    private final String description;

    CapabilityType(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static CapabilityType fromCode(String code) {
        for (CapabilityType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown capability type: " + code);
    }
}

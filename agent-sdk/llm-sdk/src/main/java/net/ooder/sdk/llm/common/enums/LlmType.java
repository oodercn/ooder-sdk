package net.ooder.sdk.llm.common.enums;

public enum LlmType {
    OPENAI("openai", "OpenAI"),
    AZURE_OPENAI("azure_openai", "Azure OpenAI"),
    WENXIN("wenxin", "文心一言"),
    TONGYI("tongyi", "通义千问"),
    LOCAL("local", "本地部署模型"),
    OPENSOURCE("opensource", "开源模型");

    private final String code;
    private final String description;

    LlmType(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static LlmType fromCode(String code) {
        for (LlmType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown LLM type: " + code);
    }
}

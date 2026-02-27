package net.ooder.sdk.llm.common.enums;

public enum InputType {
    TEXT("text", "文本输入"),
    VOICE("voice", "语音输入"),
    IMAGE("image", "图像输入"),
    VIDEO("video", "视频输入");

    private final String code;
    private final String description;

    InputType(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static InputType fromCode(String code) {
        for (InputType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown input type: " + code);
    }
}

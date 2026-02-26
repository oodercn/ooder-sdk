package net.ooder.sdk.llm.common.enums;

public enum AuthType {
    TOKEN("token", "令牌认证"),
    CERTIFICATE("certificate", "证书认证"),
    API_KEY("api_key", "API密钥认证"),
    OAUTH2("oauth2", "OAuth2认证");

    private final String code;
    private final String description;

    AuthType(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static AuthType fromCode(String code) {
        for (AuthType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown auth type: " + code);
    }
}

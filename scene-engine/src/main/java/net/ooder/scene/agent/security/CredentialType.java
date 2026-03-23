package net.ooder.scene.agent.security;

public enum CredentialType {

    API_KEY("api_key", "API密钥"),

    PASSWORD("password", "密码"),

    TOKEN("token", "令牌"),

    CERTIFICATE("certificate", "证书");

    private final String code;
    private final String description;

    CredentialType(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}

package net.ooder.skill.capability.model;

public enum ConnectorType {
    HTTP("HTTP", "HTTP连接器"),
    WEBSOCKET("WEBSOCKET", "WebSocket连接器"),
    INTERNAL("INTERNAL", "内部连接器"),
    GRPC("GRPC", "gRPC连接器");

    private final String code;
    private final String name;

    ConnectorType(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() { return code; }
    public String getName() { return name; }

    public static ConnectorType fromCode(String code) {
        if (code == null) return HTTP;
        for (ConnectorType type : values()) {
            if (type.code.equals(code) || type.name().equals(code)) {
                return type;
            }
        }
        return HTTP;
    }
}

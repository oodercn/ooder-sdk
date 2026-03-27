package net.ooder.scene.a2a.mcp;

/**
 * MCP 消息类型枚举
 *
 * @author Ooder Team
 * @version 3.0.1
 * @since 3.0.1
 */
public enum MCPMessageType {
    
    REQUEST("request", "请求"),
    
    RESPONSE("response", "响应"),
    
    NOTIFICATION("notification", "通知");
    
    private final String code;
    private final String description;
    
    MCPMessageType(String code, String description) {
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

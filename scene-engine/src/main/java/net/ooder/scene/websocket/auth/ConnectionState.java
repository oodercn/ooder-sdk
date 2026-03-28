package net.ooder.scene.websocket.auth;

/**
 * 连接状态枚举
 *
 * @author Ooder Team
 * @version 3.0.1
 * @since 3.0.1
 */
public enum ConnectionState {
    
    CONNECTING("connecting", "连接中"),
    CONNECTED("connected", "已连接"),
    AUTHENTICATING("authenticating", "认证中"),
    AUTHENTICATED("authenticated", "已认证"),
    DISCONNECTING("disconnecting", "断开中"),
    DISCONNECTED("disconnected", "已断开"),
    ERROR("error", "错误");
    
    private final String code;
    private final String description;
    
    ConnectionState(String code, String description) {
        this.code = code;
        this.description = description;
    }
    
    public String getCode() {
        return code;
    }
    
    public String getDescription() {
        return description;
    }
    
    public static ConnectionState fromCode(String code) {
        for (ConnectionState state : values()) {
            if (state.code.equalsIgnoreCase(code)) {
                return state;
            }
        }
        return DISCONNECTED;
    }
}

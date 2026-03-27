package net.ooder.scene.a2a.mcp;

import java.util.HashMap;
import java.util.Map;

/**
 * MCP 协议消息
 *
 * <p>Model Context Protocol 消息格式。</p>
 *
 * @author Ooder Team
 * @version 3.0.1
 * @since 3.0.1
 */
public class MCPMessage {
    
    private String id;
    private MCPMessageType type;
    private String method;
    private Map<String, Object> params = new HashMap<>();
    private Map<String, Object> result;
    private MCPError error;
    
    private String from;
    private String to;
    private long timestamp;
    
    public MCPMessage() {
        this.id = java.util.UUID.randomUUID().toString().replace("-", "");
        this.timestamp = System.currentTimeMillis();
    }
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public MCPMessageType getType() {
        return type;
    }
    
    public void setType(MCPMessageType type) {
        this.type = type;
    }
    
    public String getMethod() {
        return method;
    }
    
    public void setMethod(String method) {
        this.method = method;
    }
    
    public Map<String, Object> getParams() {
        return params;
    }
    
    public void setParams(Map<String, Object> params) {
        this.params = params;
    }
    
    public void setParam(String key, Object value) {
        this.params.put(key, value);
    }
    
    @SuppressWarnings("unchecked")
    public <T> T getParam(String key) {
        return (T) this.params.get(key);
    }
    
    public Map<String, Object> getResult() {
        return result;
    }
    
    public void setResult(Map<String, Object> result) {
        this.result = result;
    }
    
    public MCPError getError() {
        return error;
    }
    
    public void setError(MCPError error) {
        this.error = error;
    }
    
    public String getFrom() {
        return from;
    }
    
    public void setFrom(String from) {
        this.from = from;
    }
    
    public String getTo() {
        return to;
    }
    
    public void setTo(String to) {
        this.to = to;
    }
    
    public long getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
    
    public boolean isRequest() {
        return type == MCPMessageType.REQUEST;
    }
    
    public boolean isResponse() {
        return type == MCPMessageType.RESPONSE;
    }
    
    public boolean isError() {
        return error != null;
    }
    
    public static MCPMessage request(String method, Map<String, Object> params) {
        MCPMessage msg = new MCPMessage();
        msg.setType(MCPMessageType.REQUEST);
        msg.setMethod(method);
        if (params != null) {
            msg.setParams(params);
        }
        return msg;
    }
    
    public static MCPMessage response(String id, Map<String, Object> result) {
        MCPMessage msg = new MCPMessage();
        msg.setId(id);
        msg.setType(MCPMessageType.RESPONSE);
        msg.setResult(result);
        return msg;
    }
    
    public static MCPMessage error(String id, int code, String message) {
        MCPMessage msg = new MCPMessage();
        msg.setId(id);
        msg.setType(MCPMessageType.RESPONSE);
        msg.setError(new MCPError(code, message));
        return msg;
    }
    
    @Override
    public String toString() {
        return "MCPMessage{" +
                "id='" + id + '\'' +
                ", type=" + type +
                ", method='" + method + '\'' +
                ", from='" + from + '\'' +
                ", to='" + to + '\'' +
                '}';
    }
}

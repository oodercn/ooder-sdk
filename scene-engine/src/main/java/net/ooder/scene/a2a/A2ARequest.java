package net.ooder.scene.a2a;

import java.util.HashMap;
import java.util.Map;

/**
 * A2A 请求
 *
 * @author Ooder Team
 * @version 3.0.1
 * @since 3.0.1
 */
public class A2ARequest {
    
    private String requestId;
    private String fromAgentId;
    private String toAgentId;
    private String sceneGroupId;
    
    private String action;
    private Map<String, Object> parameters = new HashMap<>();
    
    private long timeout = 30000;
    private long createdAt;
    
    public A2ARequest() {
        this.requestId = java.util.UUID.randomUUID().toString().replace("-", "");
        this.createdAt = System.currentTimeMillis();
    }
    
    public A2ARequest(String fromAgentId, String toAgentId, String action) {
        this();
        this.fromAgentId = fromAgentId;
        this.toAgentId = toAgentId;
        this.action = action;
    }
    
    public String getRequestId() {
        return requestId;
    }
    
    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }
    
    public String getFromAgentId() {
        return fromAgentId;
    }
    
    public void setFromAgentId(String fromAgentId) {
        this.fromAgentId = fromAgentId;
    }
    
    public String getToAgentId() {
        return toAgentId;
    }
    
    public void setToAgentId(String toAgentId) {
        this.toAgentId = toAgentId;
    }
    
    public String getSceneGroupId() {
        return sceneGroupId;
    }
    
    public void setSceneGroupId(String sceneGroupId) {
        this.sceneGroupId = sceneGroupId;
    }
    
    public String getAction() {
        return action;
    }
    
    public void setAction(String action) {
        this.action = action;
    }
    
    public Map<String, Object> getParameters() {
        return parameters;
    }
    
    public void setParameters(Map<String, Object> parameters) {
        this.parameters = parameters;
    }
    
    public void setParameter(String key, Object value) {
        this.parameters.put(key, value);
    }
    
    @SuppressWarnings("unchecked")
    public <T> T getParameter(String key) {
        return (T) this.parameters.get(key);
    }
    
    public long getTimeout() {
        return timeout;
    }
    
    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }
    
    public long getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }
    
    public A2AMessage toA2AMessage() {
        return A2AMessage.builder()
                .messageId(requestId)
                .from(fromAgentId)
                .to(toAgentId)
                .sceneGroupId(sceneGroupId)
                .type(A2AMessageType.TASK_REQUEST)
                .payload(Map.of("action", action, "parameters", parameters))
                .build();
    }
    
    public static A2ARequest fromA2AMessage(A2AMessage message) {
        A2ARequest request = new A2ARequest();
        request.setRequestId(message.getMessageId());
        request.setFromAgentId(message.getFromAgentId());
        request.setToAgentId(message.getToAgentId());
        request.setSceneGroupId(message.getSceneGroupId());
        
        if (message.getPayload() instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<String, Object> payload = (Map<String, Object>) message.getPayload();
            request.setAction((String) payload.get("action"));
            @SuppressWarnings("unchecked")
            Map<String, Object> params = (Map<String, Object>) payload.get("parameters");
            if (params != null) {
                request.setParameters(params);
            }
        }
        
        return request;
    }
    
    @Override
    public String toString() {
        return "A2ARequest{" +
                "requestId='" + requestId + '\'' +
                ", fromAgentId='" + fromAgentId + '\'' +
                ", toAgentId='" + toAgentId + '\'' +
                ", action='" + action + '\'' +
                '}';
    }
}

package net.ooder.scene.skill.tool;

import java.util.HashMap;
import java.util.Map;

/**
 * 工具执行上下文
 *
 * @author Ooder Team
 * @version 2.3.1
 */
public class ToolContext {

    private String sessionId;
    private String userId;
    private Map<String, Object> attributes = new HashMap<>();

    public ToolContext() {}

    public ToolContext(String sessionId, String userId) {
        this.sessionId = sessionId;
        this.userId = userId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setAttribute(String key, Object value) {
        attributes.put(key, value);
    }

    @SuppressWarnings("unchecked")
    public <T> T getAttribute(String key) {
        return (T) attributes.get(key);
    }

    public Map<String, Object> getAttributes() {
        return new HashMap<>(attributes);
    }
}

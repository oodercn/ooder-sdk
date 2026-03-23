package net.ooder.scene.llm.context.store;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 可持久化的会话数据
 *
 * @author ooder Team
 * @since 2.3.1
 */
public class SessionData implements Serializable {

    private static final long serialVersionUID = 1L;

    private String sessionId;
    private String userId;
    private String skillId;
    private List<Map<String, Object>> history;
    private Map<String, Object> variables;
    private long createTime;
    private long updateTime;

    public SessionData() {
        this.history = new ArrayList<>();
        this.variables = new HashMap<>();
        this.createTime = System.currentTimeMillis();
        this.updateTime = this.createTime;
    }

    public static SessionData create(String sessionId, String userId) {
        SessionData data = new SessionData();
        data.setSessionId(sessionId);
        data.setUserId(userId);
        return data;
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

    public String getSkillId() {
        return skillId;
    }

    public void setSkillId(String skillId) {
        this.skillId = skillId;
    }

    public List<Map<String, Object>> getHistory() {
        return history;
    }

    public void setHistory(List<Map<String, Object>> history) {
        this.history = history != null ? history : new ArrayList<>();
    }

    public void addMessage(Map<String, Object> message) {
        if (this.history == null) {
            this.history = new ArrayList<>();
        }
        this.history.add(message);
        this.updateTime = System.currentTimeMillis();
    }

    public Map<String, Object> getVariables() {
        return variables;
    }

    public void setVariables(Map<String, Object> variables) {
        this.variables = variables != null ? variables : new HashMap<>();
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }
}

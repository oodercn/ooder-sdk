package net.ooder.scene.core.decision;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 决策上下文
 * 
 * <p>封装决策引擎执行所需的上下文信息</p>
 *
 * @author ooder Team
 * @since 2.3.1
 */
public class DecisionContext {

    private String query;
    private String userId;
    private String sceneId;
    private String groupId;
    private String agentId;
    private DecisionMode mode = DecisionMode.ONLINE_FIRST;
    private List<Map<String, Object>> conversationHistory;
    private Map<String, Object> metadata;
    private Map<String, Object> params;
    private long timestamp;

    public DecisionContext() {
        this.timestamp = System.currentTimeMillis();
        this.conversationHistory = new ArrayList<>();
        this.metadata = new HashMap<>();
        this.params = new HashMap<>();
    }

    public String getQuery() {
        return query;
    }

    public DecisionContext setQuery(String query) {
        this.query = query;
        return this;
    }

    public String getUserId() {
        return userId;
    }

    public DecisionContext setUserId(String userId) {
        this.userId = userId;
        return this;
    }

    public String getSceneId() {
        return sceneId;
    }

    public DecisionContext setSceneId(String sceneId) {
        this.sceneId = sceneId;
        return this;
    }

    public String getGroupId() {
        return groupId;
    }

    public DecisionContext setGroupId(String groupId) {
        this.groupId = groupId;
        return this;
    }

    public String getAgentId() {
        return agentId;
    }

    public DecisionContext setAgentId(String agentId) {
        this.agentId = agentId;
        return this;
    }

    public DecisionMode getMode() {
        return mode;
    }

    public DecisionContext setMode(DecisionMode mode) {
        this.mode = mode;
        return this;
    }

    public List<Map<String, Object>> getConversationHistory() {
        return conversationHistory;
    }

    public DecisionContext setConversationHistory(List<Map<String, Object>> conversationHistory) {
        this.conversationHistory = conversationHistory != null ? conversationHistory : new ArrayList<>();
        return this;
    }

    public DecisionContext addMessage(String role, String content) {
        Map<String, Object> message = new HashMap<>();
        message.put("role", role);
        message.put("content", content);
        this.conversationHistory.add(message);
        return this;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public DecisionContext setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata != null ? metadata : new HashMap<>();
        return this;
    }

    public DecisionContext addMetadata(String key, Object value) {
        this.metadata.put(key, value);
        return this;
    }

    public Map<String, Object> getParams() {
        return params;
    }

    public DecisionContext setParams(Map<String, Object> params) {
        this.params = params != null ? params : new HashMap<>();
        return this;
    }

    public DecisionContext addParam(String key, Object value) {
        this.params.put(key, value);
        return this;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public boolean hasQuery() {
        return query != null && !query.trim().isEmpty();
    }

    public boolean hasConversationHistory() {
        return conversationHistory != null && !conversationHistory.isEmpty();
    }

    public static DecisionContext create() {
        return new DecisionContext();
    }

    public static DecisionContext of(String query, String userId, String sceneId) {
        return new DecisionContext()
            .setQuery(query)
            .setUserId(userId)
            .setSceneId(sceneId);
    }
}

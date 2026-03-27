package net.ooder.scene.agent.context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Agent 上下文
 *
 * <p>管理 Agent 的多级上下文信息。</p>
 *
 * @author Ooder Team
 * @version 3.0.1
 * @since 3.0.1
 */
public class AgentContext {
    
    private String agentId;
    private String sceneGroupId;
    
    private Map<String, Object> systemContext = new HashMap<>();
    private Map<String, Object> sceneContext = new HashMap<>();
    private Map<String, Object> sessionContext = new HashMap<>();
    private Map<String, Object> conversationContext = new HashMap<>();
    
    private List<Map<String, Object>> conversationHistory = new ArrayList<>();
    private int maxHistoryLength = 20;
    
    private long createdAt;
    private long lastUpdatedAt;
    
    public AgentContext() {
        this.createdAt = System.currentTimeMillis();
        this.lastUpdatedAt = this.createdAt;
    }
    
    public AgentContext(String agentId) {
        this();
        this.agentId = agentId;
    }
    
    public AgentContext(String agentId, String sceneGroupId) {
        this(agentId);
        this.sceneGroupId = sceneGroupId;
    }
    
    public String getAgentId() {
        return agentId;
    }
    
    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }
    
    public String getSceneGroupId() {
        return sceneGroupId;
    }
    
    public void setSceneGroupId(String sceneGroupId) {
        this.sceneGroupId = sceneGroupId;
    }
    
    public Map<String, Object> getSystemContext() {
        return systemContext;
    }
    
    public void setSystemContext(Map<String, Object> systemContext) {
        this.systemContext = systemContext;
        touch();
    }
    
    public Map<String, Object> getSceneContext() {
        return sceneContext;
    }
    
    public void setSceneContext(Map<String, Object> sceneContext) {
        this.sceneContext = sceneContext;
        touch();
    }
    
    public Map<String, Object> getSessionContext() {
        return sessionContext;
    }
    
    public void setSessionContext(Map<String, Object> sessionContext) {
        this.sessionContext = sessionContext;
        touch();
    }
    
    public Map<String, Object> getConversationContext() {
        return conversationContext;
    }
    
    public void setConversationContext(Map<String, Object> conversationContext) {
        this.conversationContext = conversationContext;
        touch();
    }
    
    public List<Map<String, Object>> getConversationHistory() {
        return conversationHistory;
    }
    
    public void setConversationHistory(List<Map<String, Object>> conversationHistory) {
        this.conversationHistory = conversationHistory;
        touch();
    }
    
    public int getMaxHistoryLength() {
        return maxHistoryLength;
    }
    
    public void setMaxHistoryLength(int maxHistoryLength) {
        this.maxHistoryLength = maxHistoryLength;
    }
    
    public long getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }
    
    public long getLastUpdatedAt() {
        return lastUpdatedAt;
    }
    
    public void setLastUpdatedAt(long lastUpdatedAt) {
        this.lastUpdatedAt = lastUpdatedAt;
    }
    
    public void addMessage(Map<String, Object> message) {
        conversationHistory.add(message);
        
        while (conversationHistory.size() > maxHistoryLength) {
            conversationHistory.remove(0);
        }
        
        touch();
    }
    
    public void clearHistory() {
        conversationHistory.clear();
        touch();
    }
    
    public void updateContext(String level, Map<String, Object> updates) {
        switch (level.toLowerCase()) {
            case "system":
                systemContext.putAll(updates);
                break;
            case "scene":
                sceneContext.putAll(updates);
                break;
            case "session":
                sessionContext.putAll(updates);
                break;
            case "conversation":
                conversationContext.putAll(updates);
                break;
            default:
                conversationContext.putAll(updates);
        }
        touch();
    }
    
    public Map<String, Object> getAllContext() {
        Map<String, Object> all = new HashMap<>();
        all.put("system", systemContext);
        all.put("scene", sceneContext);
        all.put("session", sessionContext);
        all.put("conversation", conversationContext);
        return all;
    }
    
    public void touch() {
        this.lastUpdatedAt = System.currentTimeMillis();
    }
    
    public AgentContext copy() {
        AgentContext copy = new AgentContext(agentId, sceneGroupId);
        copy.setSystemContext(new HashMap<>(systemContext));
        copy.setSceneContext(new HashMap<>(sceneContext));
        copy.setSessionContext(new HashMap<>(sessionContext));
        copy.setConversationContext(new HashMap<>(conversationContext));
        copy.setConversationHistory(new ArrayList<>(conversationHistory));
        copy.setMaxHistoryLength(maxHistoryLength);
        return copy;
    }
    
    @Override
    public String toString() {
        return "AgentContext{" +
                "agentId='" + agentId + '\'' +
                ", sceneGroupId='" + sceneGroupId + '\'' +
                ", historySize=" + conversationHistory.size() +
                '}';
    }
}

package net.ooder.sdk.a2a;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A2A (Agent-to-Agent) 上下文
 *
 * <p>管理多 Agent 之间的通信上下文</p>
 *
 * @author Ooder Team
 * @version 2.3
 */
public class A2AContext {

    /**
     * 上下文ID
     */
    private final String contextId;

    /**
     * 父上下文ID (用于层级通信)
     */
    private String parentContextId;

    /**
     * 源Agent ID
     */
    private String sourceAgentId;

    /**
     * 目标Agent ID
     */
    private String targetAgentId;

    /**
     * 场景ID
     */
    private String sceneId;

    /**
     * 域ID
     */
    private String domainId;

    /**
     * 上下文级别
     */
    private ContextLevel level;

    /**
     * 上下文属性
     */
    private final Map<String, Object> attributes;

    /**
     * 创建时间戳
     */
    private final long createdAt;

    /**
     * 超时时间 (毫秒)
     */
    private long timeout;

    public A2AContext() {
        this.contextId = UUID.randomUUID().toString();
        this.attributes = new ConcurrentHashMap<>();
        this.createdAt = System.currentTimeMillis();
        this.level = ContextLevel.SESSION;
        this.timeout = 30000; // 默认30秒
    }

    public A2AContext(String sourceAgentId, String targetAgentId) {
        this();
        this.sourceAgentId = sourceAgentId;
        this.targetAgentId = targetAgentId;
    }

    // Getters and Setters

    public String getContextId() {
        return contextId;
    }

    public String getParentContextId() {
        return parentContextId;
    }

    public void setParentContextId(String parentContextId) {
        this.parentContextId = parentContextId;
    }

    public String getSourceAgentId() {
        return sourceAgentId;
    }

    public void setSourceAgentId(String sourceAgentId) {
        this.sourceAgentId = sourceAgentId;
    }

    public String getTargetAgentId() {
        return targetAgentId;
    }

    public void setTargetAgentId(String targetAgentId) {
        this.targetAgentId = targetAgentId;
    }

    public String getSceneId() {
        return sceneId;
    }

    public void setSceneId(String sceneId) {
        this.sceneId = sceneId;
    }

    public String getDomainId() {
        return domainId;
    }

    public void setDomainId(String domainId) {
        this.domainId = domainId;
    }

    public ContextLevel getLevel() {
        return level;
    }

    public void setLevel(ContextLevel level) {
        this.level = level;
    }

    public void setAttribute(String key, Object value) {
        attributes.put(key, value);
    }

    @SuppressWarnings("unchecked")
    public <T> T getAttribute(String key) {
        return (T) attributes.get(key);
    }

    public Map<String, Object> getAllAttributes() {
        return new ConcurrentHashMap<>(attributes);
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public long getTimeout() {
        return timeout;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    public boolean isExpired() {
        return System.currentTimeMillis() - createdAt > timeout;
    }

    /**
     * 创建子上下文
     */
    public A2AContext createChildContext() {
        A2AContext child = new A2AContext();
        child.setParentContextId(this.contextId);
        child.setSceneId(this.sceneId);
        child.setDomainId(this.domainId);
        child.setLevel(this.level);
        return child;
    }

    /**
     * 上下文级别
     */
    public enum ContextLevel {
        GLOBAL,     // 全局级别
        DOMAIN,     // 域级别
        SCENE,      // 场景级别
        SESSION,    // 会话级别
        EXECUTION   // 执行级别
    }

    @Override
    public String toString() {
        return "A2AContext{" +
                "contextId='" + contextId + '\'' +
                ", sourceAgentId='" + sourceAgentId + '\'' +
                ", targetAgentId='" + targetAgentId + '\'' +
                ", sceneId='" + sceneId + '\'' +
                ", level=" + level +
                '}';
    }
}

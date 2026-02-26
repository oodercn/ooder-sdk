package net.ooder.sdk.api.agent;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 场景上下文
 *
 * <p>管理场景执行过程中的状态和数据</p>
 *
 * @author Ooder Team
 * @version 2.3
 */
public class SceneContext {

    /**
     * 上下文ID
     */
    private final String contextId;

    /**
     * 场景ID
     */
    private final String sceneId;

    /**
     * 域ID
     */
    private final String domainId;

    /**
     * 上下文属性
     */
    private final Map<String, Object> attributes;

    /**
     * 父上下文 (用于A2A通信)
     */
    private SceneContext parentContext;

    /**
     * 创建时间戳
     */
    private final long createdAt;

    public SceneContext(String contextId, String sceneId, String domainId) {
        this.contextId = contextId;
        this.sceneId = sceneId;
        this.domainId = domainId;
        this.attributes = new ConcurrentHashMap<>();
        this.createdAt = System.currentTimeMillis();
    }

    /**
     * 获取上下文ID
     */
    public String getContextId() {
        return contextId;
    }

    /**
     * 获取场景ID
     */
    public String getSceneId() {
        return sceneId;
    }

    /**
     * 获取域ID
     */
    public String getDomainId() {
        return domainId;
    }

    /**
     * 设置属性
     */
    public void setAttribute(String key, Object value) {
        attributes.put(key, value);
    }

    /**
     * 获取属性
     */
    @SuppressWarnings("unchecked")
    public <T> T getAttribute(String key) {
        return (T) attributes.get(key);
    }

    /**
     * 获取属性 (带默认值)
     */
    @SuppressWarnings("unchecked")
    public <T> T getAttribute(String key, T defaultValue) {
        T value = (T) attributes.get(key);
        return value != null ? value : defaultValue;
    }

    /**
     * 移除属性
     */
    public void removeAttribute(String key) {
        attributes.remove(key);
    }

    /**
     * 检查是否包含属性
     */
    public boolean hasAttribute(String key) {
        return attributes.containsKey(key);
    }

    /**
     * 获取所有属性
     */
    public Map<String, Object> getAllAttributes() {
        return new ConcurrentHashMap<>(attributes);
    }

    /**
     * 设置父上下文
     */
    public void setParentContext(SceneContext parentContext) {
        this.parentContext = parentContext;
    }

    /**
     * 获取父上下文
     */
    public SceneContext getParentContext() {
        return parentContext;
    }

    /**
     * 获取创建时间
     */
    public long getCreatedAt() {
        return createdAt;
    }

    /**
     * 获取上下文年龄 (毫秒)
     */
    public long getAge() {
        return System.currentTimeMillis() - createdAt;
    }

    /**
     * 创建子上下文
     */
    public SceneContext createChildContext(String childContextId) {
        SceneContext child = new SceneContext(childContextId, this.sceneId, this.domainId);
        child.setParentContext(this);
        return child;
    }

    /**
     * 清空上下文
     */
    public void clear() {
        attributes.clear();
    }

    @Override
    public String toString() {
        return "SceneContext{" +
                "contextId='" + contextId + '\'' +
                ", sceneId='" + sceneId + '\'' +
                ", domainId='" + domainId + '\'' +
                ", attributes=" + attributes.size() +
                ", createdAt=" + createdAt +
                '}';
    }
}

package net.ooder.scene.core.init;

import net.ooder.scene.core.SceneEngine;

import java.util.HashMap;
import java.util.Map;

/**
 * 初始化上下文
 *
 * <p>在 SceneEngine 初始化过程中传递上下文信息</p>
 *
 * @author Ooder Team
 * @version 2.3
 * @since 2.3.0
 */
public class InitContext {

    private final SceneEngine sceneEngine;
    private final Map<String, Object> attributes = new HashMap<>();

    public InitContext(SceneEngine sceneEngine) {
        this.sceneEngine = sceneEngine;
    }

    /**
     * 获取 SceneEngine 实例
     */
    public SceneEngine getSceneEngine() {
        return sceneEngine;
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
     * 获取属性（带默认值）
     */
    @SuppressWarnings("unchecked")
    public <T> T getAttribute(String key, T defaultValue) {
        Object value = attributes.get(key);
        return value != null ? (T) value : defaultValue;
    }

    /**
     * 是否包含属性
     */
    public boolean hasAttribute(String key) {
        return attributes.containsKey(key);
    }

    /**
     * 移除属性
     */
    public Object removeAttribute(String key) {
        return attributes.remove(key);
    }

    /**
     * 获取所有属性
     */
    public Map<String, Object> getAttributes() {
        return new HashMap<>(attributes);
    }
}

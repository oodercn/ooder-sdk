package net.ooder.sdk.llm.scene;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.HashMap;
import java.util.Map;

/**
 * NLP组件上下文
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NlpComponentContext {

    /**
     * 组件ID
     */
    private String componentId;

    /**
     * 组件类型
     */
    private String componentType;

    /**
     * 组件名称
     */
    private String componentName;

    /**
     * 原始配置
     */
    private Object rawConfig;

    /**
     * 解析后的配置
     */
    private Map<String, Object> parsedConfig;

    /**
     * 组件状态
     */
    @Builder.Default
    private ComponentStatus status = ComponentStatus.INITIALIZED;

    /**
     * 组件属性
     */
    @Builder.Default
    private Map<String, Object> properties = new HashMap<>();

    /**
     * 父组件ID
     */
    private String parentComponentId;

    /**
     * 组件状态枚举
     */
    public enum ComponentStatus {
        INITIALIZED,
        ACTIVE,
        INACTIVE,
        DESTROYED
    }

    /**
     * 设置属性
     */
    public void setProperty(String key, Object value) {
        properties.put(key, value);
    }

    /**
     * 获取属性
     */
    public Object getProperty(String key) {
        return properties.get(key);
    }
}

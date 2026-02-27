package net.ooder.scene.event;

import org.springframework.context.ApplicationEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 场景事件基类
 *
 * <p>所有场景相关事件的基类，继承自 Spring 的 ApplicationEvent。</p>
 *
 * <h3>核心属性：</h3>
 * <ul>
 *   <li>eventType - 事件类型</li>
 *   <li>traceId - 追踪ID，用于链路追踪</li>
 *   <li>metadata - 元数据，存储额外信息</li>
 * </ul>
 *
 * <h3>使用示例：</h3>
 * <pre>
 * // 创建事件
 * SceneEvent event = new CapabilityEvent(source, CapabilityEventType.INVOKED);
 * event.addMetadata("capId", "40");
 * event.addMetadata("duration", 100);
 *
 * // 发布事件
 * publisher.publishEvent(event);
 * </pre>
 *
 * @author Ooder Team
 * @version 2.3
 * @since 0.7.0
 * @see ApplicationEvent
 * @see SceneEventType
 */
public abstract class SceneEvent extends ApplicationEvent {

    /** 事件类型 */
    private final SceneEventType eventType;

    /** 追踪ID */
    private final String traceId;

    /** 元数据 */
    private final Map<String, Object> metadata;

    /**
     * 构造器
     *
     * @param source 事件源
     * @param eventType 事件类型
     */
    public SceneEvent(Object source, SceneEventType eventType) {
        super(source);
        this.eventType = eventType;
        this.traceId = generateTraceId();
        this.metadata = new HashMap<>();
    }

    /**
     * 获取事件类型
     *
     * @return 事件类型
     */
    public SceneEventType getEventType() {
        return eventType;
    }

    /**
     * 获取追踪ID
     *
     * @return 追踪ID
     */
    public String getTraceId() {
        return traceId;
    }

    /**
     * 获取元数据
     *
     * @return 元数据映射表
     */
    public Map<String, Object> getMetadata() {
        return metadata;
    }

    /**
     * 添加元数据
     *
     * @param key 键
     * @param value 值
     * @return 当前事件对象（链式调用）
     */
    public SceneEvent addMetadata(String key, Object value) {
        metadata.put(key, value);
        return this;
    }

    /**
     * 生成追踪ID
     *
     * @return 16位追踪ID
     */
    private String generateTraceId() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 16);
    }

    @Override
    public String toString() {
        return "SceneEvent{" +
                "eventType=" + eventType +
                ", timestamp=" + getTimestamp() +
                ", traceId='" + traceId + '\'' +
                ", source=" + source.getClass().getSimpleName() +
                '}';
    }
}

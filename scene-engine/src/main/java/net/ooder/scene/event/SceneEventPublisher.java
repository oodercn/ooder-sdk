package net.ooder.scene.event;

import net.ooder.scene.event.capability.CapabilityEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

/**
 * 场景事件发布器
 *
 * <p>用于发布场景事件的包装类，简化事件发布操作。</p>
 *
 * <h3>核心功能：</h3>
 * <ul>
 *   <li>封装 Spring 的 ApplicationEventPublisher</li>
 *   <li>提供类型安全的事件发布方法</li>
 *   <li>支持异步事件发布</li>
 * </ul>
 *
 * <h3>使用示例：</h3>
 * <pre>
 * @Autowired
 * private SceneEventPublisher publisher;
 *
 * // 发布事件
 * CapabilityEvent event = CapabilityEvent.invoked(this, "40", "Messaging", "req-001");
 * publisher.publish(event);
 * </pre>
 *
 * @author Ooder Team
 * @version 2.3
 * @since 0.7.0
 * @see ApplicationEventPublisher
 * @see SceneEvent
 */
@Component
public class SceneEventPublisher {

    /** Spring 事件发布器 */
    private final ApplicationEventPublisher publisher;

    /**
     * 构造器
     *
     * @param publisher Spring 事件发布器
     */
    public SceneEventPublisher(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

    /**
     * 发布事件
     *
     * @param event 场景事件
     */
    public void publish(SceneEvent event) {
        publisher.publishEvent(event);
    }

    /**
     * 异步发布事件
     *
     * @param event 场景事件
     */
    public void publishAsync(SceneEvent event) {
        new Thread(() -> publisher.publishEvent(event)).start();
    }
    
    /**
     * 发布能力事件
     *
     * @param event 能力事件
     */
    public void publishCapabilityEvent(CapabilityEvent event) {
        publisher.publishEvent(event);
    }
}

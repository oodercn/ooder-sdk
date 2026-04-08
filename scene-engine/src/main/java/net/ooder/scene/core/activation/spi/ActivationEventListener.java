package net.ooder.scene.core.activation.spi;

import net.ooder.scene.core.activation.model.ActivationEvent;

/**
 * 激活事件监听器接口
 *
 * <p>用于监听激活流程中的各种事件</p>
 *
 * @author Ooder Team
 * @version 3.0.1
 * @since 3.0.1
 */
public interface ActivationEventListener {
    
    /**
     * 处理激活事件
     *
     * @param event 激活事件
     */
    void onActivationEvent(ActivationEvent event);
}

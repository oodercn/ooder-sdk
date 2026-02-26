package net.ooder.infra.core.event;

import java.util.EventListener;

/**
 * 核心层事件监听器
 * 只允许观察，不允许修改事件状态
 */
@FunctionalInterface
public interface CoreEventListener extends EventListener {
    
    /**
     * 处理事件
     * @param event 核心事件
     */
    void onEvent(CoreEvent event);
}

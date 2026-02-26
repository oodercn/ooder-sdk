package net.ooder.infra.core.event;

import java.io.Serializable;
import java.util.EventObject;

/**
 * 核心层事件基类
 * 无状态，只允许观察，不允许中断
 */
public abstract class CoreEvent extends EventObject implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private final long timestamp;
    private final String eventType;
    
    public CoreEvent(Object source, String eventType) {
        super(source);
        this.eventType = eventType;
        this.timestamp = System.currentTimeMillis();
    }
    
    public long getTimestamp() {
        return timestamp;
    }
    
    public String getEventType() {
        return eventType;
    }
    
    /**
     * 获取事件描述
     * @return 事件描述
     */
    public abstract String getDescription();
}

package net.ooder.scene.message.queue;

/**
 * 消息优先级枚举
 *
 * @author Ooder Team
 * @version 3.0.1
 * @since 3.0.1
 */
public enum MessagePriority {
    
    LOW(1, "低优先级"),
    
    NORMAL(5, "普通优先级"),
    
    HIGH(8, "高优先级"),
    
    URGENT(10, "紧急优先级");
    
    private final int level;
    private final String description;
    
    MessagePriority(int level, String description) {
        this.level = level;
        this.description = description;
    }
    
    public int getLevel() {
        return level;
    }
    
    public String getDescription() {
        return description;
    }
    
    public static MessagePriority fromLevel(int level) {
        if (level >= URGENT.level) return URGENT;
        if (level >= HIGH.level) return HIGH;
        if (level >= NORMAL.level) return NORMAL;
        return LOW;
    }
}

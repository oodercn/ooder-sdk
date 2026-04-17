package net.ooder.spi.messaging.model;

public enum MessagePriority {
    LOW(1),
    NORMAL(5),
    HIGH(10),
    URGENT(20);

    private final int level;

    MessagePriority(int level) {
        this.level = level;
    }

    public int getLevel() {
        return level;
    }

    public static MessagePriority fromLevel(int level) {
        for (MessagePriority priority : values()) {
            if (priority.level == level) {
                return priority;
            }
        }
        return NORMAL;
    }
}

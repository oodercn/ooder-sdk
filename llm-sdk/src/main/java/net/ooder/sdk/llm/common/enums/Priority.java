package net.ooder.sdk.llm.common.enums;

public enum Priority {
    LOW(1, "低优先级"),
    NORMAL(5, "普通优先级"),
    HIGH(8, "高优先级"),
    CRITICAL(10, "关键优先级");

    private final int level;
    private final String description;

    Priority(int level, String description) {
        this.level = level;
        this.description = description;
    }

    public int getLevel() {
        return level;
    }

    public String getDescription() {
        return description;
    }

    public static Priority fromLevel(int level) {
        for (Priority priority : values()) {
            if (priority.level == level) {
                return priority;
            }
        }
        return NORMAL;
    }
}

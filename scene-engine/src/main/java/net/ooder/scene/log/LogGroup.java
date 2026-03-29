package net.ooder.scene.log;

public enum LogGroup {
    INFRASTRUCTURE("基础设施日志", 1),
    BUSINESS("业务日志", 2),
    OPERATION("运维日志", 3);

    private final String name;
    private final int priority;

    LogGroup(String name, int priority) {
        this.name = name;
        this.priority = priority;
    }

    public String getName() { return name; }
    public int getPriority() { return priority; }
}

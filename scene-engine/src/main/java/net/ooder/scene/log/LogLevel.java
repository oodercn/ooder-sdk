package net.ooder.scene.log;

public enum LogLevel {

    DEBUG(0, "调试"),

    INFO(1, "信息"),

    WARN(2, "警告"),

    ERROR(3, "错误"),

    FATAL(4, "致命");

    private final int severity;
    private final String name;

    LogLevel(int severity, String name) {
        this.severity = severity;
        this.name = name;
    }

    public int getSeverity() { return severity; }
    public String getName() { return name; }

    public boolean isAtLeast(LogLevel level) {
        return this.severity >= level.severity;
    }
}

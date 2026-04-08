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
    
    public static LogLevel fromCode(String code) {
        if (code == null) {
            return null;
        }
        for (LogLevel level : values()) {
            if (level.name().equalsIgnoreCase(code)) {
                return level;
            }
        }
        return INFO;
    }
    
    public static LogLevel fromCode(String code, LogLevel defaultValue) {
        if (code == null) {
            return defaultValue;
        }
        for (LogLevel level : values()) {
            if (level.name().equalsIgnoreCase(code)) {
                return level;
            }
        }
        return defaultValue;
    }
    
    public static LogLevel fromSeverity(int severity) {
        for (LogLevel level : values()) {
            if (level.severity == severity) {
                return level;
            }
        }
        return INFO;
    }
}

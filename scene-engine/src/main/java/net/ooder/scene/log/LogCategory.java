package net.ooder.scene.log;

public enum LogCategory {

    SYSTEM("SYSTEM", "系统日志", LogGroup.INFRASTRUCTURE, "file", 7),

    SECURITY("SECURITY", "安全日志", LogGroup.INFRASTRUCTURE, "database", 90),

    LOGIN("LOGIN", "登录日志", LogGroup.INFRASTRUCTURE, "database", 30),

    LLM_CALL("LLM_CALL", "LLM调用日志", LogGroup.BUSINESS, "elasticsearch", 30),

    AUDIT("AUDIT", "审计日志", LogGroup.BUSINESS, "database", 365),

    CAPABILITY("CAPABILITY", "能力调用日志", LogGroup.BUSINESS, "elasticsearch", 60),

    EXECUTION("EXECUTION", "执行日志", LogGroup.BUSINESS, "database", 90),

    INSTALL("INSTALL", "安装日志", LogGroup.BUSINESS, "database", 180),

    OPERATION("OPERATION", "运维日志", LogGroup.OPERATION, "database", 90),

    MONITOR("MONITOR", "监控日志", LogGroup.OPERATION, "elasticsearch", 30),

    PERFORMANCE("PERFORMANCE", "性能日志", LogGroup.OPERATION, "elasticsearch", 30);

    private final String code;
    private final String name;
    private final LogGroup group;
    private final String defaultStorage;
    private final int defaultRetentionDays;

    LogCategory(String code, String name, LogGroup group,
                String defaultStorage, int defaultRetentionDays) {
        this.code = code;
        this.name = name;
        this.group = group;
        this.defaultStorage = defaultStorage;
        this.defaultRetentionDays = defaultRetentionDays;
    }

    public String getCode() { return code; }
    public String getName() { return name; }
    public LogGroup getGroup() { return group; }
    public String getDefaultStorage() { return defaultStorage; }
    public int getDefaultRetentionDays() { return defaultRetentionDays; }

    public static LogCategory fromCode(String code) {
        for (LogCategory category : values()) {
            if (category.code.equals(code)) {
                return category;
            }
        }
        return null;
    }
}

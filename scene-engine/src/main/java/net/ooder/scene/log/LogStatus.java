package net.ooder.scene.log;

public enum LogStatus {

    PENDING("pending", "待处理"),

    PROCESSING("processing", "处理中"),

    SUCCESS("success", "成功"),

    FAILURE("failure", "失败"),

    TIMEOUT("timeout", "超时"),

    CANCELLED("cancelled", "已取消");

    private final String code;
    private final String name;

    LogStatus(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() { return code; }
    public String getName() { return name; }
    
    public static LogStatus fromCode(String code) {
        if (code == null) {
            return null;
        }
        for (LogStatus status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        return PENDING;
    }
    
    public static LogStatus fromCode(String code, LogStatus defaultValue) {
        if (code == null) {
            return defaultValue;
        }
        for (LogStatus status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        return defaultValue;
    }
}

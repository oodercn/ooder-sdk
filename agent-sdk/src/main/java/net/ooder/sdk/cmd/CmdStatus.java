package net.ooder.sdk.cmd;

public enum CmdStatus {
    
    PENDING("pending", "待发送"),
    SENT("sent", "已发送"),
    EXECUTING("executing", "执行中"),
    SUCCESS("success", "成功"),
    FAILED("failed", "失败"),
    TIMEOUT("timeout", "超时"),
    CANCELLED("cancelled", "已取消");
    
    private final String code;
    private final String description;
    
    CmdStatus(String code, String description) {
        this.code = code;
        this.description = description;
    }
    
    public String getCode() { return code; }
    public String getDescription() { return description; }
    
    public boolean isTerminal() {
        return this == SUCCESS || this == FAILED || this == TIMEOUT || this == CANCELLED;
    }
    
    public static CmdStatus fromCode(String code) {
        for (CmdStatus status : values()) {
            if (status.code.equalsIgnoreCase(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown command status: " + code);
    }
}

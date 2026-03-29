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
}

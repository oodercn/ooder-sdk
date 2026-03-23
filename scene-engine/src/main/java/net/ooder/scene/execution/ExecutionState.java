package net.ooder.scene.execution;

public enum ExecutionState {

    PENDING("pending", "待执行"),

    RUNNING("running", "执行中"),

    PROGRESS("progress", "进度更新"),

    COMPLETED("completed", "已完成"),

    FAILED("failed", "执行失败"),

    TIMEOUT("timeout", "执行超时"),

    CANCELLED("cancelled", "已取消");

    private final String code;
    private final String description;

    ExecutionState(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static ExecutionState fromCode(String code) {
        for (ExecutionState state : values()) {
            if (state.code.equalsIgnoreCase(code)) {
                return state;
            }
        }
        return PENDING;
    }

    public boolean isTerminal() {
        return this == COMPLETED || this == FAILED || this == TIMEOUT || this == CANCELLED;
    }
}

package net.ooder.scene.failover;

public enum FailoverEventType {

    AGENT_TIMEOUT("agent_timeout", "Agent 心跳超时"),

    AGENT_RECOVERED("agent_recovered", "Agent 恢复"),

    TASK_REASSIGNED("task_reassigned", "任务重新分配"),

    TASK_FAILED("task_failed", "任务失败"),

    FAILOVER_STARTED("failover_started", "故障转移开始"),

    FAILOVER_COMPLETED("failover_completed", "故障转移完成");

    private final String code;
    private final String description;

    FailoverEventType(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}

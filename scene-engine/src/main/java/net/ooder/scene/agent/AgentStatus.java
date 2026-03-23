package net.ooder.scene.agent;

public enum AgentStatus {

    ONLINE("online", "在线"),

    BUSY("busy", "忙碌"),

    IDLE("idle", "空闲"),

    OFFLINE("offline", "离线");

    private final String code;
    private final String description;

    AgentStatus(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static AgentStatus fromCode(String code) {
        for (AgentStatus status : values()) {
            if (status.code.equalsIgnoreCase(code)) {
                return status;
            }
        }
        return OFFLINE;
    }
}

package net.ooder.sdk.api.command;

public enum CommandStatus {
    PENDING(1, "Pending"),
    RUNNING(2, "Running"),
    SUCCESS(3, "Success"),
    FAILED(4, "Failed"),
    TIMEOUT(5, "Timeout"),
    CANCELLED(6, "Cancelled"),
    ROLLBACK(7, "Rollback"),
    RETRYING(8, "Retrying");
    
    private final int code;
    private final String description;
    
    CommandStatus(int code, String description) {
        this.code = code;
        this.description = description;
    }
    
    public int getCode() { return code; }
    
    public String getDescription() { return description; }
    
    public static CommandStatus fromCode(int code) {
        for (CommandStatus status : values()) {
            if (status.code == code) {
                return status;
            }
        }
        return PENDING;
    }
    
    public boolean isTerminal() {
        return this == SUCCESS || this == FAILED || this == TIMEOUT || this == CANCELLED || this == ROLLBACK;
    }
}

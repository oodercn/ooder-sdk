package net.ooder.sdk.api.command;

public enum CommandDirection {
    NORTHBOUND(1, "Northbound - from south component to north component"),
    SOUTHBOUND(2, "Southbound - from north component to south component"),
    BIDIRECTIONAL(3, "Bidirectional - can flow in both directions"),
    INTERNAL(4, "Internal - internal component communication");

    private final int code;
    private final String description;

    CommandDirection(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static CommandDirection fromCode(int code) {
        for (CommandDirection direction : values()) {
            if (direction.code == code) {
                return direction;
            }
        }
        throw new IllegalArgumentException("Unknown direction code: " + code);
    }
}

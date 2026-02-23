package net.ooder.sdk.cmd;

public enum CommandDirection {
    
    NORTH("north", "北向命令 (外部系统 -> ENexus)"),
    SOUTH("south", "南向命令 (ENexus -> 外部设备)");
    
    private final String code;
    private final String description;
    
    CommandDirection(String code, String description) {
        this.code = code;
        this.description = description;
    }
    
    public String getCode() { return code; }
    public String getDescription() { return description; }
    
    public static CommandDirection fromCode(String code) {
        for (CommandDirection dir : values()) {
            if (dir.code.equalsIgnoreCase(code)) {
                return dir;
            }
        }
        throw new IllegalArgumentException("Unknown command direction: " + code);
    }
}

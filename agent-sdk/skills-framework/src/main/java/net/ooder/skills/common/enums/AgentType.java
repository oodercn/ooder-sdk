
package net.ooder.skills.common.enums;

public enum AgentType {
    MCP("mcp", "MCP Agent"),
    ROUTE("route", "Route Agent"),
    END("end", "End Agent"),
    SCENE("scene", "Scene Agent"),
    WORKER("worker", "Worker Agent");
    
    private final String code;
    private final String description;
    
    AgentType(String code, String description) {
        this.code = code;
        this.description = description;
    }
    
    public String getCode() {
        return code;
    }
    
    public String getDescription() {
        return description;
    }
    
    public static AgentType fromCode(String code) {
        for (AgentType type : values()) {
            if (type.code.equalsIgnoreCase(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown agent type: " + code);
    }
    
    public boolean isSceneAgent() {
        return this == SCENE;
    }
    
    public boolean isWorkerAgent() {
        return this == WORKER;
    }
    
    public boolean isApplicationLayer() {
        return this == SCENE || this == WORKER;
    }
    
    public boolean isLinkLayer() {
        return this == MCP || this == ROUTE;
    }
    
    public boolean isPhysicalLayer() {
        return this == END;
    }
}

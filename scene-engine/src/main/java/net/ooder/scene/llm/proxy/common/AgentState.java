package net.ooder.scene.llm.proxy.common;

/**
 * Agent状态枚举
 */
public enum AgentState {
    ACTIVE("活跃", "Agent正常运行"),
    PAUSED("暂停", "Agent已暂停"),
    DESTROYED("已销毁", "Agent已销毁"),
    ERROR("错误", "Agent发生错误");
    
    private final String name;
    private final String description;
    
    AgentState(String name, String description) {
        this.name = name;
        this.description = description;
    }
    
    public String getName() {
        return name;
    }
    
    public String getDescription() {
        return description;
    }
}

package net.ooder.scene.llm.proxy.common;

/**
 * 连接池状态枚举
 */
public enum PoolState {
    ACTIVE("活跃", "连接池正常运行"),
    EXHAUSTED("耗尽", "连接数达到上限"),
    CLOSED("关闭", "连接池已关闭"),
    ERROR("错误", "连接池发生错误");
    
    private final String name;
    private final String description;
    
    PoolState(String name, String description) {
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

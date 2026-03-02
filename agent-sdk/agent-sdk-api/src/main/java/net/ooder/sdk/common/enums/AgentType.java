package net.ooder.sdk.common.enums;

/**
 * Agent类型枚举
 * 定义系统中支持的各种Agent类型
 *
 * @author ooder
 * @since 2.3
 */
public enum AgentType {
    /** MCP Agent - 模型上下文协议Agent */
    MCP("mcp", "MCP Agent"),
    /** 路由Agent - 负责消息路由 */
    ROUTE("route", "Route Agent"),
    /** 终端Agent - 末端执行Agent */
    END("end", "End Agent"),
    /** 场景Agent - 场景管理Agent */
    SCENE("scene", "Scene Agent"),
    /** 工作Agent - 工作执行Agent */
    WORKER("worker", "Worker Agent");
    
    /** 类型编码 */
    private final String code;
    /** 类型描述 */
    private final String description;
    
    /**
     * 构造函数
     * @param code 类型编码
     * @param description 类型描述
     */
    AgentType(String code, String description) {
        this.code = code;
        this.description = description;
    }
    
    /**
     * 获取类型编码
     * @return 类型编码
     */
    public String getCode() {
        return code;
    }
    
    /**
     * 获取类型描述
     * @return 类型描述
     */
    public String getDescription() {
        return description;
    }
    
    /**
     * 根据编码获取Agent类型
     * @param code 类型编码
     * @return Agent类型
     * @throws IllegalArgumentException 如果编码未知
     */
    public static AgentType fromCode(String code) {
        for (AgentType type : values()) {
            if (type.code.equalsIgnoreCase(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown agent type: " + code);
    }
    
    /**
     * 判断是否为场景Agent
     * @return true表示场景Agent
     */
    public boolean isSceneAgent() {
        return this == SCENE;
    }
    
    /**
     * 判断是否为工作Agent
     * @return true表示工作Agent
     */
    public boolean isWorkerAgent() {
        return this == WORKER;
    }
    
    /**
     * 判断是否属于应用层
     * @return true表示应用层Agent
     */
    public boolean isApplicationLayer() {
        return this == SCENE || this == WORKER;
    }
    
    /**
     * 判断是否属于链路层
     * @return true表示链路层Agent
     */
    public boolean isLinkLayer() {
        return this == MCP || this == ROUTE;
    }
    
    /**
     * 判断是否属于物理层
     * @return true表示物理层Agent
     */
    public boolean isPhysicalLayer() {
        return this == END;
    }
}

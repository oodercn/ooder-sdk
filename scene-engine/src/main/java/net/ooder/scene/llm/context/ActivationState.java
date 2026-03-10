package net.ooder.scene.llm.context;

/**
 * Skill 激活状态枚举
 * 
 * <p>定义 Skill 在生命周期中的各个状态</p>
 *
 * @author Ooder Team
 * @version 2.3.1
 * @since 2.3.1
 */
public enum ActivationState {
    
    /**
     * 已创建：上下文已创建但未激活
     */
    CREATED("已创建", "Context created but not activated"),
    
    /**
     * 已激活：Skill 已激活，可以正常使用
     */
    ACTIVATED("已激活", "Skill activated and ready to use"),
    
    /**
     * 已暂停：Skill 暂时挂起，不处理新请求
     */
    PAUSED("已暂停", "Skill paused, not processing new requests"),
    
    /**
     * 已恢复：从暂停状态恢复
     */
    RESUMED("已恢复", "Skill resumed from paused state"),
    
    /**
     * 已销毁：Skill 已销毁，资源已释放
     */
    DESTROYED("已销毁", "Skill destroyed, resources released"),
    
    /**
     * 错误状态：激活过程中发生错误
     */
    ERROR("错误", "Error occurred during activation");
    
    private final String displayName;
    private final String description;
    
    ActivationState(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public String getDescription() {
        return description;
    }
    
    /**
     * 检查状态是否活跃
     */
    public boolean isActive() {
        return this == ACTIVATED || this == RESUMED;
    }
    
    /**
     * 检查状态是否已终止
     */
    public boolean isTerminated() {
        return this == DESTROYED || this == ERROR;
    }
}

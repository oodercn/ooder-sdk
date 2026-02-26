package net.ooder.sdk.api.lifecycle;

/**
 * Skill 生命周期事件类型
 * 
 * 覆盖 Skill 从创建到销毁的完整生命周期
 */
public enum SkillLifecycleEventType {
    
    // 安装阶段
    INSTALLING("安装中"),
    INSTALLED("已安装"),
    INSTALL_FAILED("安装失败"),
    
    // 启动阶段
    STARTING("启动中"),
    STARTED("已启动"),
    START_FAILED("启动失败"),
    
    // 运行阶段
    HEALTH_CHANGED("健康状态变化"),
    CONFIG_CHANGED("配置变更"),
    
    // 停止阶段
    STOPPING("停止中"),
    STOPPED("已停止"),
    STOP_FAILED("停止失败"),
    
    // 卸载阶段
    UNINSTALLING("卸载中"),
    UNINSTALLED("已卸载"),
    UNINSTALL_FAILED("卸载失败"),
    
    // 错误
    ERROR_OCCURRED("发生错误");
    
    private final String description;
    
    SkillLifecycleEventType(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
}

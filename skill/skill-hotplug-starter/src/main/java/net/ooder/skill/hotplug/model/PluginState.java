package net.ooder.skill.hotplug.model;

/**
 * 插件状态枚举
 */
public enum PluginState {
    /**
     * 初始化中
     */
    INITIALIZING,

    /**
     * 已安装
     */
    INSTALLED,

    /**
     * 启动中
     */
    STARTING,

    /**
     * 运行中
     */
    ACTIVE,

    /**
     * 停止中
     */
    STOPPING,

    /**
     * 已停止
     */
    STOPPED,

    /**
     * 更新中
     */
    UPDATING,

    /**
     * 已更新
     */
    UPDATED,

    /**
     * 卸载中
     */
    UNINSTALLING,

    /**
     * 已卸载
     */
    UNINSTALLED,

    /**
     * 错误状态
     */
    ERROR
}

package net.ooder.skill.hotplug.model;

/**
 * 插件状态监听器接口
 */
public interface PluginStateListener {

    /**
     * 状态变更回调
     *
     * @param newState 新状态
     * @param context  插件上下文
     */
    void onStateChange(PluginState newState, PluginContext context);
}

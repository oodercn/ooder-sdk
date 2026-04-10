package net.ooder.skill.hotplug;

import net.ooder.skill.hotplug.model.PluginContext;

/**
 * Skill生命周期接口
 * Skill可以实现此接口来接收生命周期回调
 */
public interface SkillLifecycle {

    /**
     * Skill启动时调用
     *
     * @param context 插件上下文
     */
    void onStart(PluginContext context);

    /**
     * Skill停止时调用
     *
     * @param context 插件上下文
     */
    void onStop(PluginContext context);
}

package net.ooder.skills.container.api;

import java.util.List;

/**
 * Skill 句柄
 *
 * 封装已加载的 Skill 实例
 */
public interface SkillHandle {

    /**
     * 获取 Skill ID
     */
    String getSkillId();

    /**
     * 获取版本
     */
    String getVersion();

    /**
     * 获取当前状态
     */
    SkillState getState();

    /**
     * 获取提供的能力列表
     */
    List<CapabilityRegistry.Capability> getCapabilities();

    /**
     * 获取类加载器
     */
    ClassLoader getClassLoader();

    /**
     * 获取 Skill 实例
     */
    Object getInstance();

    /**
     * Skill 状态
     */
    enum SkillState {
        CREATED,
        LOADING,
        LOADED,
        STARTING,
        RUNNING,
        STOPPING,
        STOPPED,
        UNLOADING,
        UNLOADED,
        FAILED
    }
}

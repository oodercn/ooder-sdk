package net.ooder.skill.hotplug.model;

import net.ooder.skill.hotplug.SkillLifecycle;
import net.ooder.skill.hotplug.classloader.PluginClassLoader;
import net.ooder.skill.hotplug.config.SkillConfiguration;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 插件上下文
 * 保存Skill运行时的所有状态信息
 */
public class PluginContext {

    private final String skillId;
    private final PluginClassLoader classLoader;
    private final SkillConfiguration configuration;
    private final SkillPackage skillPackage;
    private final Date installTime;

    private PluginState state;
    private SkillLifecycle lifecycle;
    private final Map<String, Object> attributes;

    public PluginContext(String skillId, PluginClassLoader classLoader, SkillConfiguration configuration) {
        this(skillId, classLoader, configuration, null);
    }

    public PluginContext(String skillId, PluginClassLoader classLoader, SkillConfiguration configuration, SkillPackage skillPackage) {
        this.skillId = skillId;
        this.classLoader = classLoader;
        this.configuration = configuration;
        this.skillPackage = skillPackage;
        this.installTime = new Date();
        this.state = PluginState.INITIALIZING;
        this.attributes = new ConcurrentHashMap<>();
    }

    // ==================== 属性操作 ====================

    public void setAttribute(String key, Object value) {
        attributes.put(key, value);
    }

    @SuppressWarnings("unchecked")
    public <T> T getAttribute(String key) {
        return (T) attributes.get(key);
    }

    public void removeAttribute(String key) {
        attributes.remove(key);
    }

    public Map<String, Object> getAttributes() {
        return new ConcurrentHashMap<>(attributes);
    }

    // ==================== 类加载 ====================

    public Class<?> loadClass(String className) throws ClassNotFoundException {
        return classLoader.loadClass(className);
    }

    // ==================== Getters and Setters ====================

    public String getSkillId() {
        return skillId;
    }

    public PluginClassLoader getClassLoader() {
        return classLoader;
    }

    public SkillConfiguration getConfiguration() {
        return configuration;
    }

    public SkillPackage getSkillPackage() {
        return skillPackage;
    }

    public Date getInstallTime() {
        return installTime;
    }

    public PluginState getState() {
        return state;
    }

    public void setState(PluginState state) {
        this.state = state;
    }

    public SkillLifecycle getLifecycle() {
        return lifecycle;
    }

    public void setLifecycle(SkillLifecycle lifecycle) {
        this.lifecycle = lifecycle;
    }

    @Override
    public String toString() {
        return "PluginContext{" +
                "skillId='" + skillId + '\'' +
                ", state=" + state +
                ", installTime=" + installTime +
                '}';
    }
}

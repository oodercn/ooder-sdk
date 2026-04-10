package net.ooder.skill.hotplug.model;

import net.ooder.skill.hotplug.config.SkillConfiguration;

import java.util.Date;

/**
 * 插件信息
 * 用于展示和查询
 */
public class PluginInfo {

    private String skillId;
    private PluginState state;
    private SkillConfiguration configuration;
    private Date installTime;
    private int loadedClassCount;
    private int registeredRouteCount;
    private int registeredServiceCount;

    // Getters and Setters

    public String getSkillId() {
        return skillId;
    }

    public void setSkillId(String skillId) {
        this.skillId = skillId;
    }

    public PluginState getState() {
        return state;
    }

    public void setState(PluginState state) {
        this.state = state;
    }

    public SkillConfiguration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(SkillConfiguration configuration) {
        this.configuration = configuration;
    }

    public Date getInstallTime() {
        return installTime;
    }

    public void setInstallTime(Date installTime) {
        this.installTime = installTime;
    }

    public int getLoadedClassCount() {
        return loadedClassCount;
    }

    public void setLoadedClassCount(int loadedClassCount) {
        this.loadedClassCount = loadedClassCount;
    }

    public int getRegisteredRouteCount() {
        return registeredRouteCount;
    }

    public void setRegisteredRouteCount(int registeredRouteCount) {
        this.registeredRouteCount = registeredRouteCount;
    }

    public int getRegisteredServiceCount() {
        return registeredServiceCount;
    }

    public void setRegisteredServiceCount(int registeredServiceCount) {
        this.registeredServiceCount = registeredServiceCount;
    }

    @Override
    public String toString() {
        return "PluginInfo{" +
                "skillId='" + skillId + '\'' +
                ", state=" + state +
                ", installTime=" + installTime +
                '}';
    }
}

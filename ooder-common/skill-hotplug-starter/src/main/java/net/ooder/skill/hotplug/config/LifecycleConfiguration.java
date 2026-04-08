package net.ooder.skill.hotplug.config;

/**
 * 生命周期配置
 */
public class LifecycleConfiguration {

    /**
     * 启动类
     */
    private String startup;

    /**
     * 关闭类
     */
    private String shutdown;

    // Getters and Setters

    public String getStartup() {
        return startup;
    }

    public void setStartup(String startup) {
        this.startup = startup;
    }

    public String getShutdown() {
        return shutdown;
    }

    public void setShutdown(String shutdown) {
        this.shutdown = shutdown;
    }
}

package net.ooder.skill.hotplug;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * 热插拔配置属性
 * 支持多来源加载：system、plugins、external
 */
@ConfigurationProperties(prefix = "ooder.skill.hotplug")
public class HotPlugProperties {

    /**
     * 插件目录（向后兼容）
     */
    private String pluginDirectory = "./plugins";

    /**
     * 是否启用热插拔
     */
    private boolean enabled = true;

    /**
     * 是否自动加载插件目录中的插件
     */
    private boolean autoLoad = true;

    /**
     * 类加载器缓存大小
     */
    private int classLoaderCacheSize = 100;

    /**
     * 插件超时时间（秒）
     */
    private int pluginTimeout = 30;

    /**
     * 是否启用插件隔离
     */
    private boolean isolationEnabled = true;

    /**
     * 多来源加载配置
     */
    private List<SkillSource> sources = new ArrayList<>();

    /**
     * Skill 来源配置
     */
    public static class SkillSource {
        /**
         * 来源类型：system、plugins、external
         */
        private String type;

        /**
         * 来源路径
         */
        private String path;

        /**
         * 是否启用
         */
        private boolean enabled = true;

        /**
         * 加载优先级（数字越小优先级越高）
         */
        private int priority = 100;

        /**
         * 排除的 Skill ID 列表
         */
        private List<String> excludes = new ArrayList<>();

        // Getters and Setters
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public String getPath() { return path; }
        public void setPath(String path) { this.path = path; }
        public boolean isEnabled() { return enabled; }
        public void setEnabled(boolean enabled) { this.enabled = enabled; }
        public int getPriority() { return priority; }
        public void setPriority(int priority) { this.priority = priority; }
        public List<String> getExcludes() { return excludes; }
        public void setExcludes(List<String> excludes) { this.excludes = excludes; }
    }

    // Getters and Setters

    public String getPluginDirectory() {
        return pluginDirectory;
    }

    public void setPluginDirectory(String pluginDirectory) {
        this.pluginDirectory = pluginDirectory;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isAutoLoad() {
        return autoLoad;
    }

    public void setAutoLoad(boolean autoLoad) {
        this.autoLoad = autoLoad;
    }

    public int getClassLoaderCacheSize() {
        return classLoaderCacheSize;
    }

    public void setClassLoaderCacheSize(int classLoaderCacheSize) {
        this.classLoaderCacheSize = classLoaderCacheSize;
    }

    public int getPluginTimeout() {
        return pluginTimeout;
    }

    public void setPluginTimeout(int pluginTimeout) {
        this.pluginTimeout = pluginTimeout;
    }

    public boolean isIsolationEnabled() {
        return isolationEnabled;
    }

    public void setIsolationEnabled(boolean isolationEnabled) {
        this.isolationEnabled = isolationEnabled;
    }

    public List<SkillSource> getSources() {
        return sources;
    }

    public void setSources(List<SkillSource> sources) {
        this.sources = sources;
    }
}

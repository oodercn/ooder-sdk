package net.ooder.sdk.api.config;

import java.util.Map;
import java.util.Set;

/**
 * 统一配置接口
 *
 * 提供跨模块的统一配置管理能力
 */
public interface UnifiedConfiguration {

    /**
     * 获取配置值
     */
    <T> T get(String key, Class<T> type);

    /**
     * 获取配置值（带默认值）
     */
    <T> T get(String key, Class<T> type, T defaultValue);

    /**
     * 设置配置值
     */
    <T> void set(String key, T value);

    /**
     * 批量设置配置
     */
    void setAll(Map<String, Object> configs);

    /**
     * 删除配置
     */
    void remove(String key);

    /**
     * 检查配置是否存在
     */
    boolean contains(String key);

    /**
     * 获取所有配置键
     */
    Set<String> keys();

    /**
     * 获取指定前缀的所有配置
     */
    Map<String, Object> getByPrefix(String prefix);

    /**
     * 添加配置变更监听器
     */
    void addListener(String key, ConfigChangeListener listener);

    /**
     * 移除配置变更监听器
     */
    void removeListener(String key, ConfigChangeListener listener);

    /**
     * 持久化配置
     */
    void persist();

    /**
     * 重新加载配置
     */
    void reload();

    /**
     * 配置变更监听器
     */
    @FunctionalInterface
    interface ConfigChangeListener {
        void onChange(String key, Object oldValue, Object newValue);
    }

    /**
     * 配置作用域
     */
    enum ConfigScope {
        SYSTEM,
        USER,
        SCENE,
        SKILL
    }

    /**
     * 配置元数据
     */
    class ConfigMetadata {
        private String key;
        private String description;
        private ConfigScope scope;
        private boolean encrypted;
        private boolean readonly;
        private Object defaultValue;

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public ConfigScope getScope() {
            return scope;
        }

        public void setScope(ConfigScope scope) {
            this.scope = scope;
        }

        public boolean isEncrypted() {
            return encrypted;
        }

        public void setEncrypted(boolean encrypted) {
            this.encrypted = encrypted;
        }

        public boolean isReadonly() {
            return readonly;
        }

        public void setReadonly(boolean readonly) {
            this.readonly = readonly;
        }

        public Object getDefaultValue() {
            return defaultValue;
        }

        public void setDefaultValue(Object defaultValue) {
            this.defaultValue = defaultValue;
        }
    }
}

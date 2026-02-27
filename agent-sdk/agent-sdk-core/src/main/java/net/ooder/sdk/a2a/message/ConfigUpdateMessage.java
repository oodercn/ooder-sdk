package net.ooder.sdk.a2a.message;

import java.util.HashMap;
import java.util.Map;

/**
 * 配置更新消息（泛型版本）
 *
 * @param <C> 配置项类型
 * @author Ooder Team
 * @version 2.3
 * @since 2.3
 */
public class ConfigUpdateMessage<C> extends A2AMessage<Map<String, C>> {

    /** 配置项映射 */
    private Map<String, C> config;

    public ConfigUpdateMessage() {
        super(A2AMessageType.CONFIG_UPDATE);
        this.config = new HashMap<>();
    }
    
    /**
     * 创建通用配置更新消息（向后兼容）
     */
    public static ConfigUpdateMessage<Object> createGeneric() {
        return new ConfigUpdateMessage<>();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder<C> {
        private ConfigUpdateMessage<C> message = new ConfigUpdateMessage<>();

        public Builder<C> skillId(String skillId) {
            message.setSkillId(skillId);
            return this;
        }

        public Builder<C> config(Map<String, C> config) {
            message.setConfig(config);
            return this;
        }

        public Builder<C> configItem(String key, C value) {
            message.addConfigItem(key, value);
            return this;
        }

        public ConfigUpdateMessage<C> build() {
            return message;
        }
    }

    /**
     * 获取配置项
     * @return 配置映射
     */
    public Map<String, C> getConfig() {
        return config;
    }

    /**
     * 设置配置项
     * @param config 配置映射
     */
    public void setConfig(Map<String, C> config) {
        this.config = config != null ? config : new HashMap<>();
        this.setData(this.config);
    }

    public void addConfigItem(String key, C value) {
        this.config.put(key, value);
        this.setData(this.config);
    }
}

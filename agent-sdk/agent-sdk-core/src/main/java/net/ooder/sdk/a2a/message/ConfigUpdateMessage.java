package net.ooder.sdk.a2a.message;

import java.util.HashMap;
import java.util.Map;

/**
 * 配置更新消息
 *
 * @author Ooder Team
 * @version 1.0
 * @since 2.3.0
 */
public class ConfigUpdateMessage extends A2AMessage {

    private Map<String, Object> config;

    public ConfigUpdateMessage() {
        super(A2AMessageType.CONFIG_UPDATE);
        this.config = new HashMap<>();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private ConfigUpdateMessage message = new ConfigUpdateMessage();

        public Builder skillId(String skillId) {
            message.setSkillId(skillId);
            return this;
        }

        public Builder config(Map<String, Object> config) {
            message.setConfig(config);
            return this;
        }

        public Builder configItem(String key, Object value) {
            message.addConfigItem(key, value);
            return this;
        }

        public ConfigUpdateMessage build() {
            return message;
        }
    }

    public Map<String, Object> getConfig() {
        return config;
    }

    public void setConfig(Map<String, Object> config) {
        this.config = config != null ? config : new HashMap<>();
    }

    public void addConfigItem(String key, Object value) {
        this.config.put(key, value);
    }
}

package net.ooder.skill.hotplug.a2a.message;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

/**
 * 配置更新消息
 * 对应Ooder-A2A规范v1.0 config_update类型
 */
public class ConfigUpdateMessage extends A2AMessage {

    /**
     * 配置项
     */
    @JsonProperty("config")
    private Map<String, Object> config;

    /**
     * 是否立即生效
     */
    @JsonProperty("immediate")
    private Boolean immediate;

    public ConfigUpdateMessage() {
        super(MessageType.CONFIG_UPDATE);
    }

    public Map<String, Object> getConfig() {
        return config;
    }

    public void setConfig(Map<String, Object> config) {
        this.config = config;
    }

    public Boolean getImmediate() {
        return immediate;
    }

    public void setImmediate(Boolean immediate) {
        this.immediate = immediate;
    }
}

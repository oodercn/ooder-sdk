package net.ooder.skills.container.api.webhook;

import java.util.List;

/**
 * Webhook 配置
 *
 * 支持 URL、事件类型、签名密钥配置
 */
public class WebhookConfig {

    private String webhookId;
    private String skillId;
    private String url;
    private List<String> events;
    private String secret;
    private boolean enabled;
    private long timeout;
    private int retryCount;

    public WebhookConfig() {
        this.enabled = true;
        this.timeout = 5000; // 默认5秒超时
        this.retryCount = 3; // 默认重试3次
    }

    public String getWebhookId() {
        return webhookId;
    }

    public void setWebhookId(String webhookId) {
        this.webhookId = webhookId;
    }

    public String getSkillId() {
        return skillId;
    }

    public void setSkillId(String skillId) {
        this.skillId = skillId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public List<String> getEvents() {
        return events;
    }

    public void setEvents(List<String> events) {
        this.events = events;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public long getTimeout() {
        return timeout;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    public int getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(int retryCount) {
        this.retryCount = retryCount;
    }

    /**
     * 检查是否订阅了指定事件
     */
    public boolean isSubscribedTo(String eventType) {
        if (events == null || events.isEmpty()) {
            return true; // 未指定则订阅所有
        }
        return events.contains(eventType) || events.contains("*");
    }
}

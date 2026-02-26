package net.ooder.skills.container.api.webhook;

import java.util.List;

/**
 * Webhook 管理器
 *
 * 管理 Skill 的 Webhook 配置和事件推送
 */
public interface WebhookManager {

    /**
     * 注册 Webhook
     *
     * @param skillId Skill ID
     * @param config Webhook 配置
     * @return Webhook ID
     */
    String registerWebhook(String skillId, WebhookConfig config);

    /**
     * 注销 Webhook
     *
     * @param webhookId Webhook ID
     */
    void unregisterWebhook(String webhookId);

    /**
     * 更新 Webhook 配置
     *
     * @param webhookId Webhook ID
     * @param config 新配置
     */
    void updateWebhook(String webhookId, WebhookConfig config);

    /**
     * 获取 Skill 的所有 Webhook
     *
     * @param skillId Skill ID
     * @return Webhook 列表
     */
    List<WebhookConfig> getWebhooks(String skillId);

    /**
     * 启用 Webhook
     *
     * @param webhookId Webhook ID
     */
    void enableWebhook(String webhookId);

    /**
     * 禁用 Webhook
     *
     * @param webhookId Webhook ID
     */
    void disableWebhook(String webhookId);

    /**
     * 发送事件到 Webhook
     *
     * @param skillId Skill ID
     * @param eventType 事件类型
     * @param payload 事件数据
     */
    void sendEvent(String skillId, String eventType, Object payload);

    /**
     * 测试 Webhook
     *
     * @param webhookId Webhook ID
     * @return 测试结果
     */
    WebhookTestResult testWebhook(String webhookId);

    /**
     * Webhook 测试结果
     */
    class WebhookTestResult {
        private boolean success;
        private int statusCode;
        private String message;
        private long responseTime;

        public boolean isSuccess() {
            return success;
        }

        public void setSuccess(boolean success) {
            this.success = success;
        }

        public int getStatusCode() {
            return statusCode;
        }

        public void setStatusCode(int statusCode) {
            this.statusCode = statusCode;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public long getResponseTime() {
            return responseTime;
        }

        public void setResponseTime(long responseTime) {
            this.responseTime = responseTime;
        }
    }
}

package net.ooder.sdk.a2a.message;

import java.util.HashMap;
import java.util.Map;

/**
 * 任务发送消息
 *
 * <p>对应Ooder-A2A规范v1.0 task_send类型</p>
 *
 * @author Ooder Team
 * @version 1.0
 * @since 2.3.0
 */
public class TaskSendMessage extends A2AMessage {

    /**
     * 用户输入
     */
    private String input;

    /**
     * 任务参数
     */
    private Map<String, Object> parameters;

    /**
     * 回调URL
     */
    private String callbackUrl;

    public TaskSendMessage() {
        super(A2AMessageType.TASK_SEND);
        this.parameters = new HashMap<>();
    }

    // ==================== Builder模式 ====================

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private TaskSendMessage message = new TaskSendMessage();

        public Builder skillId(String skillId) {
            message.setSkillId(skillId);
            return this;
        }

        public Builder sessionId(String sessionId) {
            message.setSessionId(sessionId);
            return this;
        }

        public Builder input(String input) {
            message.setInput(input);
            return this;
        }

        public Builder parameters(Map<String, Object> parameters) {
            message.setParameters(parameters);
            return this;
        }

        public Builder parameter(String key, Object value) {
            message.addParameter(key, value);
            return this;
        }

        public Builder callbackUrl(String callbackUrl) {
            message.setCallbackUrl(callbackUrl);
            return this;
        }

        public Builder metadata(String key, Object value) {
            message.addMetadata(key, value);
            return this;
        }

        public TaskSendMessage build() {
            return message;
        }
    }

    // ==================== Getters and Setters ====================

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }

    public Map<String, Object> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, Object> parameters) {
        this.parameters = parameters != null ? parameters : new HashMap<>();
    }

    public String getCallbackUrl() {
        return callbackUrl;
    }

    public void setCallbackUrl(String callbackUrl) {
        this.callbackUrl = callbackUrl;
    }

    /**
     * 添加参数
     */
    public void addParameter(String key, Object value) {
        this.parameters.put(key, value);
    }

    @Override
    public String toString() {
        return "TaskSendMessage{" +
                "type=" + getType() +
                ", skillId='" + getSkillId() + '\'' +
                ", sessionId='" + getSessionId() + '\'' +
                ", input='" + input + '\'' +
                ", parameters=" + parameters +
                '}';
    }
}

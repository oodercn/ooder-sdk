package net.ooder.skill.hotplug.a2a.message;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

/**
 * 任务发送消息
 * 对应Ooder-A2A规范v1.0 task_send类型
 */
public class TaskSendMessage extends A2AMessage {

    /**
     * 用户输入
     */
    @JsonProperty("input")
    private String input;

    /**
     * 任务参数
     */
    @JsonProperty("parameters")
    private Map<String, Object> parameters;

    /**
     * 回调URL
     */
    @JsonProperty("callbackUrl")
    private String callbackUrl;

    public TaskSendMessage() {
        super(MessageType.TASK_SEND);
    }

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
        this.parameters = parameters;
    }

    public String getCallbackUrl() {
        return callbackUrl;
    }

    public void setCallbackUrl(String callbackUrl) {
        this.callbackUrl = callbackUrl;
    }

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
}

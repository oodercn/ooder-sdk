package net.ooder.sdk.a2a.message;

import java.util.HashMap;
import java.util.Map;

/**
 * 任务发送消息（泛型版本）
 *
 * <p>对应Ooder-A2A规范v1.0 task_send类型</p>
 *
 * @param <P> 参数类型
 * @author Ooder Team
 * @version 2.3
 * @since 2.3
 */
public class TaskSendMessage<P> extends A2AMessage<Map<String, P>> {

    /**
     * 用户输入
     */
    private String input;

    /**
     * 任务参数
     */
    private Map<String, P> parameters;

    /**
     * 回调URL
     */
    private String callbackUrl;

    public TaskSendMessage() {
        super(A2AMessageType.TASK_SEND);
        this.parameters = new HashMap<>();
    }
    
    /**
     * 创建通用任务发送消息（向后兼容）
     */
    public static TaskSendMessage<Object> createGeneric() {
        return new TaskSendMessage<>();
    }

    // ==================== Builder模式 ====================

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder<P> {
        private TaskSendMessage<P> message = new TaskSendMessage<>();

        public Builder<P> skillId(String skillId) {
            message.setSkillId(skillId);
            return this;
        }

        public Builder<P> sessionId(String sessionId) {
            message.setSessionId(sessionId);
            return this;
        }

        public Builder<P> input(String input) {
            message.setInput(input);
            return this;
        }

        public Builder<P> parameters(Map<String, P> parameters) {
            message.setParameters(parameters);
            return this;
        }

        public Builder<P> parameter(String key, P value) {
            message.addParameter(key, value);
            return this;
        }

        public Builder<P> callbackUrl(String callbackUrl) {
            message.setCallbackUrl(callbackUrl);
            return this;
        }

        public Builder<P> metadata(String key, String value) {
            message.addMetadata(key, value);
            return this;
        }

        public TaskSendMessage<P> build() {
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

    /**
     * 获取任务参数
     * @return 参数映射
     */
    public Map<String, P> getParameters() {
        return parameters;
    }

    /**
     * 设置任务参数
     * @param parameters 参数映射
     */
    public void setParameters(Map<String, P> parameters) {
        this.parameters = parameters != null ? parameters : new HashMap<>();
        this.setData(this.parameters);
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
    public void addParameter(String key, P value) {
        this.parameters.put(key, value);
        this.setData(this.parameters);
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

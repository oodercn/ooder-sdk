package net.ooder.sdk.llm.context;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 上下文实例
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContextInstance {

    /**
     * 实例ID
     */
    private String instanceId;

    /**
     * 模板ID
     */
    private String templateId;

    /**
     * 会话ID
     */
    private String sessionId;

    /**
     * 消息列表
     */
    @Builder.Default
    private List<Message> messages = new ArrayList<>();

    /**
     * 变量
     */
    @Builder.Default
    private Map<String, Object> variables = new HashMap<>();

    /**
     * 创建时间
     */
    @Builder.Default
    private long createdAt = System.currentTimeMillis();

    /**
     * 最后更新时间
     */
    @Builder.Default
    private long lastUpdatedAt = System.currentTimeMillis();

    /**
     * 元数据
     */
    @Builder.Default
    private Map<String, Object> metadata = new HashMap<>();

    /**
     * 添加消息
     */
    public void addMessage(String role, String content) {
        messages.add(Message.builder()
                .role(role)
                .content(content)
                .timestamp(System.currentTimeMillis())
                .build());
        lastUpdatedAt = System.currentTimeMillis();
    }

    /**
     * 设置变量
     */
    public void setVariable(String key, Object value) {
        variables.put(key, value);
        lastUpdatedAt = System.currentTimeMillis();
    }

    /**
     * 获取变量
     */
    public Object getVariable(String key) {
        return variables.get(key);
    }

    /**
     * 消息
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Message {
        private String role;
        private String content;
        private long timestamp;
    }
}

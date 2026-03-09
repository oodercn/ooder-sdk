package net.ooder.sdk.llm.model;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * 驱动层聊天请求
 * 直接映射LLM提供商的API格式
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DriverChatRequest {
    
    /**
     * 模型名称
     */
    private String model;
    
    /**
     * 消息列表
     */
    private List<ChatMessage> messages;
    
    /**
     * 温度
     */
    private Double temperature;
    
    /**
     * Top P
     */
    private Double topP;
    
    /**
     * 最大Token数
     */
    private Integer maxTokens;
    
    /**
     * 工具列表
     */
    private List<ToolDefinition> tools;
    
    /**
     * 工具选择
     */
    private Object toolChoice;
    
    /**
     * 元数据
     */
    private Map<String, Object> metadata;
    
    public static DriverChatRequest create(String model, List<ChatMessage> messages) {
        return DriverChatRequest.builder()
                .model(model)
                .messages(messages)
                .temperature(0.7)
                .topP(1.0)
                .maxTokens(4096)
                .build();
    }
}

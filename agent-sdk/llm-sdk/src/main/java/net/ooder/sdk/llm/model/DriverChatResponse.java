package net.ooder.sdk.llm.model;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * 驱动层聊天响应
 * 直接映射LLM提供商的API响应格式
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DriverChatResponse {
    
    /**
     * 响应ID
     */
    private String id;
    
    /**
     * 模型名称
     */
    private String model;
    
    /**
     * 消息
     */
    private ChatMessage message;
    
    /**
     * 使用量
     */
    private TokenUsage usage;
    
    /**
     * 结束原因
     */
    private String finishReason;
    
    /**
     * 创建时间
     */
    private Long createdTime;
}

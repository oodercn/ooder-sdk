package net.ooder.sdk.llm.model;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

/**
 * 统一的消息模型
 * 用于LLM对话中的消息传递
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage {
    
    /**
     * 角色 (system/user/assistant/tool)
     */
    private String role;
    
    /**
     * 消息内容
     */
    private String content;
    
    /**
     * 名称（用于tool消息）
     */
    private String name;
    
    /**
     * 工具调用（assistant消息）
     */
    private List<ToolCall> toolCalls;
    
    /**
     * 单个工具调用
     */
    private ToolCall toolCall;
    
    public ChatMessage(String role, String content) {
        this.role = role;
        this.content = content;
    }
    
    public static ChatMessage system(String content) {
        return new ChatMessage("system", content);
    }
    
    public static ChatMessage user(String content) {
        return new ChatMessage("user", content);
    }
    
    public static ChatMessage assistant(String content) {
        return new ChatMessage("assistant", content);
    }
    
    public static ChatMessage tool(String content, String name) {
        ChatMessage message = new ChatMessage("tool", content);
        message.setName(name);
        return message;
    }
}

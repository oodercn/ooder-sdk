package net.ooder.scene.skill.conversation;

/**
 * 流消息处理器
 *
 * @author ooder
 * @since 2.3
 */
public interface StreamMessageHandler {
    
    /**
     * 处理流式内容
     *
     * @param content 内容片段
     */
    void onContent(String content);
    
    /**
     * 处理工具调用
     *
     * @param toolName 工具名称
     * @param arguments 参数
     */
    void onToolCall(String toolName, String arguments);
    
    /**
     * 处理完成
     *
     * @param response 完整响应
     */
    void onComplete(MessageResponse response);
    
    /**
     * 处理错误
     *
     * @param error 错误信息
     */
    void onError(String error);
}

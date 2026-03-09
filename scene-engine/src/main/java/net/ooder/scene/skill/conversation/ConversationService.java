package net.ooder.scene.skill.conversation;

import java.util.List;
import java.util.Map;

/**
 * 对话服务接口
 *
 * <p>提供多轮对话能力，支持：</p>
 * <ul>
 *   <li>对话历史管理</li>
 *   <li>上下文维护</li>
 *   <li>工具调用集成</li>
 *   <li>RAG 增强</li>
 * </ul>
 *
 * <p>架构层次：应用层 - 智能增强</p>
 *
 * @author ooder
 * @since 2.3
 */
public interface ConversationService {
    
    /**
     * 创建对话
     *
     * @param userId 用户ID
     * @param request 创建请求
     * @return 对话信息
     */
    Conversation createConversation(String userId, ConversationCreateRequest request);
    
    /**
     * 获取对话
     *
     * @param conversationId 对话ID
     * @return 对话信息
     */
    Conversation getConversation(String conversationId);
    
    /**
     * 删除对话
     *
     * @param conversationId 对话ID
     */
    void deleteConversation(String conversationId);
    
    /**
     * 列出用户的对话
     *
     * @param userId 用户ID
     * @param limit 限制数量
     * @return 对话列表
     */
    List<Conversation> listConversations(String userId, int limit);
    
    /**
     * 发送消息
     *
     * @param conversationId 对话ID
     * @param request 消息请求
     * @return 消息响应
     */
    MessageResponse sendMessage(String conversationId, MessageRequest request);
    
    /**
     * 流式发送消息
     *
     * @param conversationId 对话ID
     * @param request 消息请求
     * @param handler 流处理器
     */
    void sendMessageStream(String conversationId, MessageRequest request, StreamMessageHandler handler);
    
    /**
     * 获取对话历史
     *
     * @param conversationId 对话ID
     * @param limit 限制数量
     * @return 消息列表
     */
    List<Message> getHistory(String conversationId, int limit);
    
    /**
     * 清空对话历史
     *
     * @param conversationId 对话ID
     */
    void clearHistory(String conversationId);
    
    /**
     * 获取对话统计
     *
     * @param conversationId 对话ID
     * @return 统计信息
     */
    ConversationStats getStats(String conversationId);
}

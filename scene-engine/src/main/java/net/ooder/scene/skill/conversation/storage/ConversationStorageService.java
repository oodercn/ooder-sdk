package net.ooder.scene.skill.conversation.storage;

import net.ooder.scene.skill.conversation.Conversation;
import net.ooder.scene.skill.conversation.FunctionCallLog;
import net.ooder.scene.skill.conversation.Message;

import java.util.List;

/**
 * 对话存储服务接口
 *
 * <p>提供对话数据的持久化存储能力，支持：</p>
 * <ul>
 *   <li>对话会话存储</li>
 *   <li>消息历史存储</li>
 *   <li>工具调用日志存储</li>
 * </ul>
 *
 * <p>架构层次：存储层</p>
 *
 * @author ooder
 * @since 2.3
 */
public interface ConversationStorageService {

    /**
     * 保存对话会话
     *
     * @param conversation 对话会话
     */
    void saveConversation(Conversation conversation);

    /**
     * 获取对话会话
     *
     * @param conversationId 对话ID
     * @return 对话会话
     */
    Conversation getConversation(String conversationId);

    /**
     * 删除对话会话
     *
     * @param conversationId 对话ID
     */
    void deleteConversation(String conversationId);

    /**
     * 列出用户的所有对话
     *
     * @param userId 用户ID
     * @return 对话列表
     */
    List<Conversation> listConversations(String userId);

    /**
     * 保存消息
     *
     * @param conversationId 对话ID
     * @param message 消息
     */
    void saveMessage(String conversationId, Message message);

    /**
     * 获取对话的消息历史
     *
     * @param conversationId 对话ID
     * @return 消息列表
     */
    List<Message> getMessages(String conversationId);

    /**
     * 清空对话消息历史
     *
     * @param conversationId 对话ID
     */
    void clearMessages(String conversationId);

    /**
     * 记录工具调用
     *
     * @param conversationId 对话ID
     * @param log 工具调用日志
     */
    void saveToolCallLog(String conversationId, FunctionCallLog log);

    /**
     * 获取工具调用历史
     *
     * @param conversationId 对话ID
     * @return 工具调用日志列表
     */
    List<FunctionCallLog> getToolCallLogs(String conversationId);

    /**
     * 获取工具调用历史（限制数量）
     *
     * @param conversationId 对话ID
     * @param limit 限制数量
     * @return 工具调用日志列表
     */
    List<FunctionCallLog> getToolCallLogs(String conversationId, int limit);

    /**
     * 初始化存储
     */
    void initialize();

    /**
     * 关闭存储服务
     */
    void shutdown();

    /**
     * 保存上下文数据
     *
     * @param key 存储键
     * @param data 上下文数据
     */
    void saveContext(String key, java.util.Map<String, Object> data);

    /**
     * 加载上下文数据
     *
     * @param key 存储键
     * @return 上下文数据，如果不存在返回 null
     */
    java.util.Map<String, Object> loadContext(String key);

    /**
     * 删除上下文数据
     *
     * @param key 存储键
     */
    void deleteContext(String key);
}

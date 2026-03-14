package net.ooder.scene.skill.conversation;

import net.ooder.scene.skill.tool.ToolCallResult;

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
 *   <li>知识库自动更新</li>
 *   <li>审计日志记录</li>
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

    /**
     * 记录工具调用结果
     *
     * <p>自动触发：</p>
     * <ul>
     *   <li>审计日志记录</li>
     *   <li>知识库更新（可选）</li>
     * </ul>
     *
     * @param conversationId 对话ID
     * @param result 工具调用结果
     */
    void recordToolCall(String conversationId, ToolCallResult result);

    /**
     * 获取工具调用历史
     *
     * @param conversationId 对话ID
     * @return 工具调用日志列表
     */
    List<FunctionCallLog> getToolCallHistory(String conversationId);

    /**
     * 获取工具调用历史（限制数量）
     *
     * @param conversationId 对话ID
     * @param limit 限制数量
     * @return 工具调用日志列表
     */
    List<FunctionCallLog> getToolCallHistory(String conversationId, int limit);

    /**
     * 从对话中学习并更新知识库
     *
     * @param conversationId 对话ID
     * @return 学习结果，包含更新的知识条目数
     */
    LearnResult learnFromConversation(String conversationId);

    /**
     * 设置是否自动学习
     *
     * @param autoLearn 是否自动学习
     */
    void setAutoLearn(boolean autoLearn);

    /**
     * 检查是否开启自动学习
     *
     * @return 是否自动学习
     */
    boolean isAutoLearn();

    /**
     * 简洁对话方式 - 一行代码完成对话
     *
     * @param conversationId 对话ID
     * @param content 用户消息内容
     * @return 助手消息响应
     */
    Message chat(String conversationId, String content);

    /**
     * 简洁对话方式 - 带工具执行
     *
     * @param conversationId 对话ID
     * @param content 用户消息内容
     * @param toolNames 启用的工具名称列表
     * @return 助手消息响应
     */
    Message chatWithTools(String conversationId, String content, List<String> toolNames);

    /**
     * 流式对话
     *
     * @param conversationId 对话ID
     * @param content 用户消息内容
     * @param handler 流处理器
     */
    void chatStream(String conversationId, String content, StreamMessageHandler handler);

    /**
     * 对话分析
     *
     * @param conversationId 对话ID
     * @return 对话分析结果
     */
    ConversationAnalysis analyze(String conversationId);

    /**
     * 学习结果
     */
    class LearnResult {
        private int updatedEntries;
        private List<String> updatedIds;
        private String message;

        public LearnResult(int updatedEntries, List<String> updatedIds, String message) {
            this.updatedEntries = updatedEntries;
            this.updatedIds = updatedIds;
            this.message = message;
        }

        public int getUpdatedEntries() {
            return updatedEntries;
        }

        public List<String> getUpdatedIds() {
            return updatedIds;
        }

        public String getMessage() {
            return message;
        }
    }
}

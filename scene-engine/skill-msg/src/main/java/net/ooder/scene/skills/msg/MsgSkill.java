package net.ooder.scene.skills.msg;

import net.ooder.scene.core.PageResult;
import net.ooder.scene.core.UserInfo;

import java.util.List;
import java.util.Map;

/**
 * MsgSkill 消息技能接口
 * 
 * <p>提供消息发送、消息历史、已读标记、消息撤回等能力。</p>
 * 
 * @author Ooder Team
 * @version 0.7.3
 */
public interface MsgSkill {

    String getSkillId();

    String getSkillName();

    String getSkillVersion();

    List<String> getCapabilities();

    // ==================== 消息发送 ====================

    /**
     * 发送文本消息
     * 
     * @param conversationId 会话ID
     * @param senderId 发送者ID
     * @param senderName 发送者名称
     * @param content 消息内容
     * @return 消息信息
     */
    Map<String, Object> sendTextMessage(String conversationId, String senderId, String senderName, String content);

    /**
     * 发送图片消息
     * 
     * @param conversationId 会话ID
     * @param senderId 发送者ID
     * @param senderName 发送者名称
     * @param imageUrl 图片URL
     * @param thumbnailUrl 缩略图URL
     * @return 消息信息
     */
    Map<String, Object> sendImageMessage(String conversationId, String senderId, String senderName, 
                                          String imageUrl, String thumbnailUrl);

    /**
     * 发送文件消息
     * 
     * @param conversationId 会话ID
     * @param senderId 发送者ID
     * @param senderName 发送者名称
     * @param fileName 文件名
     * @param fileUrl 文件URL
     * @param fileSize 文件大小
     * @return 消息信息
     */
    Map<String, Object> sendFileMessage(String conversationId, String senderId, String senderName,
                                         String fileName, String fileUrl, long fileSize);

    // ==================== 消息历史 (SE-001 扩展) ====================

    /**
     * 获取消息历史
     * 
     * @param conversationId 会话ID
     * @param limit 限制数量
     * @param beforeTime 获取此时间之前的消息
     * @return 消息列表
     */
    List<Map<String, Object>> getMessageHistory(String conversationId, int limit, Long beforeTime);

    /**
     * 获取消息详情
     * 
     * @param messageId 消息ID
     * @return 消息信息
     */
    Map<String, Object> getMessage(String messageId);

    /**
     * 搜索消息
     * 
     * @param userId 用户ID
     * @param keyword 关键词
     * @param limit 限制数量
     * @return 消息列表
     */
    List<Map<String, Object>> searchMessages(String userId, String keyword, int limit);

    // ==================== 已读标记 (SE-001 扩展) ====================

    /**
     * 标记消息已读
     * 
     * @param conversationId 会话ID
     * @param userId 用户ID
     * @param lastReadTime 最后已读时间
     * @return 是否成功
     */
    boolean markAsRead(String conversationId, String userId, Long lastReadTime);

    /**
     * 获取未读消息数
     * 
     * @param conversationId 会话ID
     * @param userId 用户ID
     * @return 未读消息数
     */
    int getUnreadCount(String conversationId, String userId);

    /**
     * 获取未读消息汇总
     * 
     * @param userId 用户ID
     * @return 未读汇总信息
     */
    Map<String, Object> getUnreadSummary(String userId);

    // ==================== 消息撤回 (SE-001 扩展) ====================

    /**
     * 撤回消息
     * 
     * @param messageId 消息ID
     * @param userId 操作用户ID
     * @return 是否成功
     */
    boolean recallMessage(String messageId, String userId);

    /**
     * 删除消息（仅对自己可见）
     * 
     * @param messageId 消息ID
     * @param userId 用户ID
     * @return 是否成功
     */
    boolean deleteMessage(String messageId, String userId);

    // ==================== 消息转发 ====================

    /**
     * 转发消息
     * 
     * @param messageId 原消息ID
     * @param targetConversationIds 目标会话ID列表
     * @param senderId 发送者ID
     * @return 转发结果
     */
    Map<String, Object> forwardMessage(String messageId, List<String> targetConversationIds, String senderId);

    // ==================== 能力调用 ====================

    /**
     * 调用能力
     * 
     * @param capability 能力名称
     * @param params 参数
     * @return 调用结果
     */
    Object invoke(String capability, Map<String, Object> params);
}

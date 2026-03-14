package net.ooder.scene.skill.engine.context;

import java.util.List;
import java.util.Map;

/**
 * 上下文存储服务 - SE 核心服务
 * 提供统一的上下文持久化管理能力
 *
 * <p>变更说明: 新增接口，统一上下文持久化管理，替代各 Skill 自行实现的存储逻辑</p>
 *
 * @author ooder
 * @since 2.3.1
 */
public interface ContextStorageService {

    // ========== 用户上下文 ==========

    /**
     * 保存用户上下文
     * @param userId 用户ID
     * @param context 上下文数据
     */
    void saveUserContext(String userId, Map<String, Object> context);

    /**
     * 加载用户上下文
     * @param userId 用户ID
     * @return 上下文数据，不存在返回空Map
     */
    Map<String, Object> loadUserContext(String userId);

    /**
     * 删除用户上下文
     * @param userId 用户ID
     */
    void deleteUserContext(String userId);

    // ========== 会话上下文 ==========

    /**
     * 保存会话上下文
     * @param sessionId 会话ID
     * @param context 上下文数据
     */
    void saveSessionContext(String sessionId, Map<String, Object> context);

    /**
     * 加载会话上下文
     * @param sessionId 会话ID
     * @return 上下文数据，不存在返回空Map
     */
    Map<String, Object> loadSessionContext(String sessionId);

    /**
     * 检查会话是否存在
     * @param sessionId 会话ID
     * @return true 如果会话存在
     */
    boolean sessionExists(String sessionId);

    /**
     * 删除会话及其所有相关数据
     * @param sessionId 会话ID
     */
    void deleteSession(String sessionId);

    // ========== Skill 上下文 ==========

    /**
     * 保存 Skill 上下文
     * @param skillId Skill ID
     * @param sessionId 会话ID
     * @param context 上下文数据
     */
    void saveSkillContext(String skillId, String sessionId, Map<String, Object> context);

    /**
     * 加载 Skill 上下文
     * @param skillId Skill ID
     * @param sessionId 会话ID
     * @return 上下文数据，不存在返回空Map
     */
    Map<String, Object> loadSkillContext(String skillId, String sessionId);

    /**
     * 删除 Skill 上下文
     * @param skillId Skill ID
     * @param sessionId 会话ID
     */
    void deleteSkillContext(String skillId, String sessionId);

    // ========== 对话历史 ==========

    /**
     * 保存对话消息
     * @param sessionId 会话ID
     * @param message 消息对象，包含 role, content, timestamp 等字段
     */
    void saveChatMessage(String sessionId, Map<String, Object> message);

    /**
     * 批量保存对话消息
     * @param sessionId 会话ID
     * @param messages 消息列表
     */
    void saveChatMessages(String sessionId, List<Map<String, Object>> messages);

    /**
     * 加载对话历史
     * @param sessionId 会话ID
     * @param limit 限制数量，0 表示不限制
     * @return 消息列表，按时间戳升序排列
     */
    List<Map<String, Object>> loadChatHistory(String sessionId, int limit);

    /**
     * 加载对话历史（带偏移量）
     * @param sessionId 会话ID
     * @param offset 偏移量
     * @param limit 限制数量
     * @return 消息列表
     */
    List<Map<String, Object>> loadChatHistory(String sessionId, int offset, int limit);

    /**
     * 清空对话历史
     * @param sessionId 会话ID
     */
    void clearChatHistory(String sessionId);

    // ========== 页面状态 ==========

    /**
     * 保存页面状态
     * @param sessionId 会话ID
     * @param pageId 页面ID
     * @param state 状态数据
     */
    void savePageState(String sessionId, String pageId, Map<String, Object> state);

    /**
     * 加载页面状态
     * @param sessionId 会话ID
     * @param pageId 页面ID
     * @return 状态数据，不存在返回空Map
     */
    Map<String, Object> loadPageState(String sessionId, String pageId);

    /**
     * 删除页面状态
     * @param sessionId 会话ID
     * @param pageId 页面ID
     */
    void deletePageState(String sessionId, String pageId);

    /**
     * 获取会话的所有页面状态
     * @param sessionId 会话ID
     * @return 页面ID到状态的映射
     */
    Map<String, Map<String, Object>> loadAllPageStates(String sessionId);

    // ========== 工具方法 ==========

    /**
     * 获取存储根目录
     * @return 存储根目录路径
     */
    String getStorageRoot();

    /**
     * 清理过期数据
     * @param maxAgeDays 最大保留天数
     * @return 清理的记录数
     */
    int cleanupExpiredData(int maxAgeDays);
}

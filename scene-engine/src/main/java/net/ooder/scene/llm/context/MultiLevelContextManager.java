package net.ooder.scene.llm.context;

import java.util.List;
import java.util.Map;

/**
 * 多级上下文管理器接口
 *
 * <p>管理 LLM 的多级上下文：</p>
 * <ul>
 *   <li>Level 0: 全局上下文 - 系统基础信息、菜单功能映射、全局工具定义</li>
 *   <li>Level 1: 技能上下文 - 技能 SystemPrompt、工具定义、知识库绑定</li>
 *   <li>Level 2: 页面上下文 - 当前页面信息、可用 API、状态数据</li>
 *   <li>Level 3: 会话上下文 - 多轮对话历史、用户意图追踪</li>
 * </ul>
 *
 * <p>支持上下文缓存机制，页面跳转时自动重载上下文</p>
 *
 * @author ooder Team
 * @since 2.3.1
 */
public interface MultiLevelContextManager {

    /**
     * 初始化全局上下文
     *
     * @param config 全局上下文配置
     */
    void initializeGlobalContext(GlobalContextConfig config);

    /**
     * 获取当前上下文
     *
     * @return 当前 LLM 上下文
     */
    LlmContext getCurrentContext();

    /**
     * 页面跳转时重载上下文
     *
     * @param pageId 页面ID
     */
    void reloadContextForPage(String pageId);

    /**
     * 技能切换时重载上下文
     *
     * @param skillId 技能ID
     */
    void reloadContextForSkill(String skillId);

    /**
     * 获取上下文层级
     *
     * @return 当前上下文层级
     */
    ContextLevel getContextLevel();

    /**
     * 推送上下文更新
     *
     * @param update 上下文更新事件
     */
    void pushContextUpdate(ContextUpdate update);

    /**
     * 获取对话历史
     *
     * @param sessionId 会话ID
     * @return 消息列表
     */
    List<Map<String, Object>> getConversationHistory(String sessionId);

    /**
     * 添加消息
     *
     * @param sessionId 会话ID
     * @param message 消息
     */
    void addMessage(String sessionId, Map<String, Object> message);

    /**
     * 清除会话
     *
     * @param sessionId 会话ID
     */
    void clearSession(String sessionId);

    /**
     * 获取技能上下文缓存
     *
     * @param skillId 技能ID
     * @return 技能上下文
     */
    SkillActivationContext getSkillContext(String skillId);

    /**
     * 缓存技能上下文
     *
     * @param skillId 技能ID
     * @param context 技能上下文
     */
    void cacheSkillContext(String skillId, SkillActivationContext context);

    /**
     * 清除技能上下文缓存
     *
     * @param skillId 技能ID
     */
    void clearSkillContextCache(String skillId);

    /**
     * 获取页面-技能映射
     *
     * @param pageId 页面ID
     * @return 技能ID
     */
    String getSkillIdForPage(String pageId);

    /**
     * 设置页面-技能映射
     *
     * @param pageId 页面ID
     * @param skillId 技能ID
     */
    void setPageSkillMapping(String pageId, String skillId);

    /**
     * 获取当前页面ID
     *
     * @return 当前页面ID
     */
    String getCurrentPageId();

    /**
     * 设置当前页面ID
     *
     * @param pageId 页面ID
     */
    void setCurrentPageId(String pageId);

    /**
     * 获取当前技能ID
     *
     * @return 当前技能ID
     */
    String getCurrentSkillId();

    /**
     * 获取或创建会话
     *
     * @param userId 用户ID
     * @return 会话ID
     */
    String getOrCreateSession(String userId);

    /**
     * 获取会话的当前技能上下文
     *
     * @param sessionId 会话ID
     * @return 技能上下文
     */
    SkillActivationContext getSessionSkillContext(String sessionId);
}

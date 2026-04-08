package net.ooder.scene.group.service;

import net.ooder.scene.group.SceneGroup;
import net.ooder.scene.skill.knowledge.KnowledgeBinding;
import net.ooder.scene.llm.config.SceneLlmConfigInfo;

import java.util.List;
import java.util.Map;

/**
 * SceneGroup 统一服务接口
 *
 * <p>作为 OS 工程与 SE SDK 交互的统一入口，封装场景组的核心业务流程，
 * 避免 OS 工程过度参与底层核心流程。</p>
 *
 * <h3>核心职责：</h3>
 * <ul>
 *   <li>场景组生命周期管理 - 创建、激活、暂停、销毁</li>
 *   <li>知识库绑定管理 - 绑定、解绑、查询、配置</li>
 *   <li>LLM 配置管理 - 设置、查询、重置配置</li>
 *   <li>参与者管理 - 添加、移除、角色变更</li>
 *   <li>扩展配置管理 - 自定义配置存储</li>
 * </ul>
 *
 * <h3>使用示例：</h3>
 * <pre>
 * // OS 工程通过 Service 调用，不直接操作 Manager
 * @Autowired
 * private SceneGroupService sceneGroupService;
 *
 * // 绑定知识库
 * sceneGroupService.bindKnowledgeBase(groupId, kbId, kbName);
 *
 * // 设置 LLM 配置
 * sceneGroupService.setLlmConfig(groupId, config);
 * </pre>
 *
 * @author SE Team
 * @version 3.1.0
 * @since 3.1.0
 */
public interface SceneGroupService {

    // ========== 场景组生命周期管理 ==========

    /**
     * 创建场景组
     *
     * @param sceneGroupId 场景组ID
     * @param templateId 模板ID
     * @param creatorId 创建者ID
     * @param name 场景组名称
     * @param description 场景组描述
     * @return 创建的场景组
     */
    SceneGroup createSceneGroup(String sceneGroupId, String templateId, String creatorId,
                                 String name, String description);

    /**
     * 获取场景组
     *
     * @param sceneGroupId 场景组ID
     * @return 场景组，不存在返回 null
     */
    SceneGroup getSceneGroup(String sceneGroupId);

    /**
     * 获取所有场景组
     *
     * @return 场景组列表
     */
    List<SceneGroup> getAllSceneGroups();

    /**
     * 获取模板下的所有场景组
     *
     * @param templateId 模板ID
     * @return 场景组列表
     */
    List<SceneGroup> getSceneGroupsByTemplate(String templateId);

    /**
     * 激活场景组
     *
     * @param sceneGroupId 场景组ID
     * @return 是否成功
     */
    boolean activateSceneGroup(String sceneGroupId);

    /**
     * 暂停场景组
     *
     * @param sceneGroupId 场景组ID
     * @return 是否成功
     */
    boolean suspendSceneGroup(String sceneGroupId);

    /**
     * 归档场景组
     *
     * @param sceneGroupId 场景组ID
     * @return 是否成功
     */
    boolean archiveSceneGroup(String sceneGroupId);

    /**
     * 从归档恢复场景组
     *
     * @param sceneGroupId 场景组ID
     * @return 是否成功
     */
    boolean restoreSceneGroup(String sceneGroupId);

    /**
     * 销毁场景组
     *
     * @param sceneGroupId 场景组ID
     * @return 是否成功
     */
    boolean destroySceneGroup(String sceneGroupId);

    /**
     * 更新场景组信息
     *
     * @param sceneGroupId 场景组ID
     * @param name 名称
     * @param description 描述
     * @return 是否成功
     */
    boolean updateSceneGroupInfo(String sceneGroupId, String name, String description);

    // ========== 知识库绑定管理 ==========

    /**
     * 绑定知识库
     *
     * @param sceneGroupId 场景组ID
     * @param knowledgeBaseId 知识库ID
     * @param knowledgeBaseName 知识库名称
     * @return 绑定ID
     */
    String bindKnowledgeBase(String sceneGroupId, String knowledgeBaseId, String knowledgeBaseName);

    /**
     * 绑定知识库（带作用域）
     *
     * @param sceneGroupId 场景组ID
     * @param knowledgeBaseId 知识库ID
     * @param knowledgeBaseName 知识库名称
     * @param scope 作用域
     * @param priority 优先级
     * @return 绑定ID
     */
    String bindKnowledgeBase(String sceneGroupId, String knowledgeBaseId, String knowledgeBaseName,
                            String scope, int priority);

    /**
     * 解绑知识库
     *
     * @param sceneGroupId 场景组ID
     * @param knowledgeBaseId 知识库ID
     * @return 是否成功
     */
    boolean unbindKnowledgeBase(String sceneGroupId, String knowledgeBaseId);

    /**
     * 获取场景组绑定的所有知识库
     *
     * @param sceneGroupId 场景组ID
     * @return 知识库绑定列表
     */
    List<KnowledgeBinding> getKnowledgeBindings(String sceneGroupId);

    /**
     * 获取单个知识库绑定
     *
     * @param sceneGroupId 场景组ID
     * @param knowledgeBaseId 知识库ID
     * @return 知识库绑定信息
     */
    KnowledgeBinding getKnowledgeBinding(String sceneGroupId, String knowledgeBaseId);

    /**
     * 检查是否已绑定知识库
     *
     * @param sceneGroupId 场景组ID
     * @param knowledgeBaseId 知识库ID
     * @return 是否已绑定
     */
    boolean hasKnowledgeBinding(String sceneGroupId, String knowledgeBaseId);

    /**
     * 设置知识库绑定优先级
     *
     * @param sceneGroupId 场景组ID
     * @param knowledgeBaseId 知识库ID
     * @param priority 优先级
     * @return 是否成功
     */
    boolean setKnowledgeBindingPriority(String sceneGroupId, String knowledgeBaseId, int priority);

    /**
     * 清除场景组的所有知识库绑定
     *
     * @param sceneGroupId 场景组ID
     * @return 是否成功
     */
    boolean clearKnowledgeBindings(String sceneGroupId);

    // ========== LLM 配置管理 ==========

    /**
     * 获取 LLM 配置
     *
     * @param sceneGroupId 场景组ID
     * @return LLM 配置信息
     */
    SceneLlmConfigInfo getLlmConfig(String sceneGroupId);

    /**
     * 设置 LLM 配置
     *
     * @param sceneGroupId 场景组ID
     * @param provider 提供商
     * @param model 模型
     * @param temperature 温度
     * @param maxTokens 最大令牌数
     * @return 是否成功
     */
    boolean setLlmConfig(String sceneGroupId, String provider, String model,
                        double temperature, int maxTokens);

    /**
     * 设置完整 LLM 配置
     *
     * @param sceneGroupId 场景组ID
     * @param config LLM 配置
     * @return 是否成功
     */
    boolean setLlmConfig(String sceneGroupId, SceneLlmConfigInfo config);

    /**
     * 更新 LLM 配置（部分更新）
     *
     * @param sceneGroupId 场景组ID
     * @param config LLM 配置
     * @return 是否成功
     */
    boolean updateLlmConfig(String sceneGroupId, SceneLlmConfigInfo config);

    /**
     * 重置 LLM 配置为默认值
     *
     * @param sceneGroupId 场景组ID
     * @return 是否成功
     */
    boolean resetLlmConfig(String sceneGroupId);

    /**
     * 检查是否有自定义 LLM 配置
     *
     * @param sceneGroupId 场景组ID
     * @return 是否有自定义配置
     */
    boolean hasCustomLlmConfig(String sceneGroupId);

    /**
     * 获取默认 LLM 配置
     *
     * @return 默认配置
     */
    SceneLlmConfigInfo getDefaultLlmConfig();

    // ========== 扩展配置管理 ==========

    /**
     * 设置扩展配置
     *
     * @param sceneGroupId 场景组ID
     * @param key 配置键
     * @param value 配置值
     * @return 是否成功
     */
    boolean setExtendedConfig(String sceneGroupId, String key, Object value);

    /**
     * 获取扩展配置
     *
     * @param sceneGroupId 场景组ID
     * @param key 配置键
     * @return 配置值
     */
    Object getExtendedConfig(String sceneGroupId, String key);

    /**
     * 获取所有扩展配置
     *
     * @param sceneGroupId 场景组ID
     * @return 配置映射
     */
    Map<String, Object> getAllExtendedConfig(String sceneGroupId);

    /**
     * 移除扩展配置
     *
     * @param sceneGroupId 场景组ID
     * @param key 配置键
     * @return 是否成功
     */
    boolean removeExtendedConfig(String sceneGroupId, String key);

    // ========== 统计信息 ==========

    /**
     * 获取场景组数量
     *
     * @return 场景组数量
     */
    int getSceneGroupCount();

    /**
     * 获取场景组绑定的知识库数量
     *
     * @param sceneGroupId 场景组ID
     * @return 知识库数量
     */
    int getKnowledgeBindingCount(String sceneGroupId);

    /**
     * 获取总知识库绑定数量
     *
     * @return 总绑定数量
     */
    int getTotalKnowledgeBindingCount();

    /**
     * 获取 LLM 配置数量
     *
     * @return 配置数量
     */
    int getLlmConfigCount();
}

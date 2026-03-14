package net.ooder.scene.spi;

import net.ooder.scene.skill.conversation.ConversationService;
import net.ooder.scene.skill.conversation.storage.ConversationStorageService;
import net.ooder.scene.skill.knowledge.InteractionFeedbackService;
import net.ooder.scene.skill.knowledge.KnowledgeBaseService;
import net.ooder.scene.skill.knowledge.TerminologyService;
import net.ooder.scene.skill.tool.ToolOrchestrator;
import net.ooder.scene.skill.tool.ToolRegistry;

/**
 * Scene Engine 服务提供者接口 (SPI)
 * <p>为 Skill 插件提供访问 SE 核心服务的统一入口</p>
 *
 * <p>使用方式：</p>
 * <pre>
 * // 在 Skill 中获取服务
 * SceneEngineServiceProvider provider = ServiceLoader.load(SceneEngineServiceProvider.class)
 *     .findFirst()
 *     .orElseThrow(() -> new IllegalStateException("SceneEngineServiceProvider not found"));
 *
 * ConversationService conversationService = provider.getConversationService();
 * </pre>
 *
 * @author ooder Team
 * @since 2.3.1
 */
public interface SceneEngineServiceProvider {

    /**
     * 获取对话存储服务
     *
     * @return 对话存储服务
     */
    ConversationStorageService getStorageService();

    /**
     * 获取对话服务
     *
     * @return 对话服务
     */
    ConversationService getConversationService();

    /**
     * 获取知识库服务
     *
     * @return 知识库服务
     */
    KnowledgeBaseService getKnowledgeBaseService();

    /**
     * 获取术语服务
     *
     * @return 术语服务
     */
    TerminologyService getTerminologyService();

    /**
     * 获取交互反馈服务
     *
     * @return 交互反馈服务
     */
    InteractionFeedbackService getInteractionFeedbackService();

    /**
     * 获取工具注册表
     *
     * @return 工具注册表
     */
    ToolRegistry getToolRegistry();

    /**
     * 获取工具编排器
     *
     * @return 工具编排器
     */
    ToolOrchestrator getToolOrchestrator();

    /**
     * 获取服务提供者名称
     *
     * @return 提供者名称
     */
    String getProviderName();

    /**
     * 获取服务提供者版本
     *
     * @return 版本号
     */
    String getProviderVersion();

    /**
     * 检查服务是否可用
     *
     * @param serviceType 服务类型
     * @return 是否可用
     */
    boolean isServiceAvailable(Class<?> serviceType);
}

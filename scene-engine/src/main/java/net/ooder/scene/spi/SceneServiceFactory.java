package net.ooder.scene.spi;

import net.ooder.scene.skill.conversation.ConversationService;
import net.ooder.scene.skill.conversation.storage.ConversationStorageService;
import net.ooder.scene.skill.knowledge.InteractionFeedbackService;
import net.ooder.scene.skill.knowledge.KnowledgeBaseService;
import net.ooder.scene.skill.knowledge.TerminologyService;
import net.ooder.scene.skill.tool.ToolOrchestrator;
import net.ooder.scene.skill.tool.ToolRegistry;

/**
 * Scene Engine 服务工厂接口
 * <p>为 SceneServices 静态入口提供工厂方法</p>
 *
 * <p>使用场景：</p>
 * <ul>
 *   <li>SE 启动时设置工厂实现</li>
 *   <li>Skill 通过 SceneServices 静态方法访问服务</li>
 * </ul>
 *
 * <p>示例：</p>
 * <pre>
 * // SE 启动时初始化
 * SceneServices.setFactory(new DefaultSceneServiceFactory(...));
 *
 * // Skill 中使用
 * ConversationService conv = SceneServices.getConversationService();
 * </pre>
 *
 * @author ooder Team
 * @since 2.3.1
 * @see SceneServices
 */
public interface SceneServiceFactory {

    /**
     * 获取对话存储服务
     *
     * @return 对话存储服务，如果不可用返回 null
     */
    ConversationStorageService getStorageService();

    /**
     * 获取对话服务
     *
     * @return 对话服务，如果不可用返回 null
     */
    ConversationService getConversationService();

    /**
     * 获取知识库服务
     *
     * @return 知识库服务，如果不可用返回 null
     */
    KnowledgeBaseService getKnowledgeService();

    /**
     * 获取术语服务
     *
     * @return 术语服务，如果不可用返回 null
     */
    TerminologyService getTerminologyService();

    /**
     * 获取交互反馈服务
     *
     * @return 交互反馈服务，如果不可用返回 null
     */
    InteractionFeedbackService getInteractionFeedbackService();

    /**
     * 获取工具注册表
     *
     * @return 工具注册表，如果不可用返回 null
     */
    ToolRegistry getToolRegistry();

    /**
     * 获取工具编排器
     *
     * @return 工具编排器，如果不可用返回 null
     */
    ToolOrchestrator getToolOrchestrator();
}

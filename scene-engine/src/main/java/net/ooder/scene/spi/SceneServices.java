package net.ooder.scene.spi;

import net.ooder.scene.skill.conversation.ConversationService;
import net.ooder.scene.skill.conversation.storage.ConversationStorageService;
import net.ooder.scene.skill.knowledge.InteractionFeedbackService;
import net.ooder.scene.skill.knowledge.KnowledgeBaseService;
import net.ooder.scene.skill.knowledge.TerminologyService;
import net.ooder.scene.skill.tool.ToolOrchestrator;
import net.ooder.scene.skill.tool.ToolRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Scene Engine 服务静态入口
 * <p>为 Skill 插件提供静态方式访问 SE 核心服务</p>
 *
 * <p>使用场景：</p>
 * <ul>
 *   <li>Skill 控制器无法使用 Spring 依赖注入时</li>
 *   <li>需要跨 ClassLoader 访问 SE 服务时</li>
 *   <li>简化服务访问，无需管理 Bean 生命周期</li>
 * </ul>
 *
 * <p>使用示例：</p>
 * <pre>
 * // Skill 控制器中直接使用
 * public class ChatController {
 *     private final ConversationService conversationService;
 *
 *     public ChatController() {
 *         this.conversationService = SceneServices.getConversationService();
 *         if (this.conversationService == null) {
 *             throw new IllegalStateException("SE services not initialized");
 *         }
 *     }
 *
 *     public String chat(String message) {
 *         return conversationService.chat("session-001", message).getContent();
 *     }
 * }
 * </pre>
 *
 * <p>初始化方式（SE 启动时）：</p>
 * <pre>
 * &#64;Configuration
 * public class SceneEngineConfig {
 *     &#64;Autowired
 *     private ConversationService conversationService;
 *
 *     &#64;PostConstruct
 *     public void init() {
 *         SceneServiceFactory factory = new DefaultSceneServiceFactory(
 *             conversationService, ...
 *         );
 *         SceneServices.setFactory(factory);
 *     }
 * }
 * </pre>
 *
 * @author ooder Team
 * @since 2.3.1
 * @see SceneServiceFactory
 */
public final class SceneServices {

    private static final Logger log = LoggerFactory.getLogger(SceneServices.class);

    private static volatile SceneServiceFactory factory;
    private static volatile boolean initialized = false;

    private SceneServices() {
        // 禁止实例化
    }

    /**
     * 设置服务工厂
     * <p>应在 SE 启动时调用，且只调用一次</p>
     *
     * @param factory 服务工厂实现
     * @throws IllegalStateException 如果已经初始化
     */
    public static synchronized void setFactory(SceneServiceFactory factory) {
        if (initialized) {
            log.warn("SceneServices already initialized, ignoring new factory");
            return;
        }
        SceneServices.factory = factory;
        initialized = true;
        log.info("SceneServices initialized successfully");
    }

    /**
     * 检查服务是否已初始化
     *
     * @return true 如果服务已初始化
     */
    public static boolean isInitialized() {
        return initialized && factory != null;
    }

    /**
     * 获取对话存储服务
     *
     * @return 对话存储服务，如果未初始化返回 null
     */
    public static ConversationStorageService getStorageService() {
        if (!initialized || factory == null) {
            log.warn("SceneServices not initialized, cannot get StorageService");
            return null;
        }
        return factory.getStorageService();
    }

    /**
     * 获取对话服务
     *
     * @return 对话服务，如果未初始化返回 null
     */
    public static ConversationService getConversationService() {
        if (!initialized || factory == null) {
            log.warn("SceneServices not initialized, cannot get ConversationService");
            return null;
        }
        return factory.getConversationService();
    }

    /**
     * 获取知识库服务
     *
     * @return 知识库服务，如果未初始化返回 null
     */
    public static KnowledgeBaseService getKnowledgeService() {
        if (!initialized || factory == null) {
            log.warn("SceneServices not initialized, cannot get KnowledgeService");
            return null;
        }
        return factory.getKnowledgeService();
    }

    /**
     * 获取术语服务
     *
     * @return 术语服务，如果未初始化返回 null
     */
    public static TerminologyService getTerminologyService() {
        if (!initialized || factory == null) {
            log.warn("SceneServices not initialized, cannot get TerminologyService");
            return null;
        }
        return factory.getTerminologyService();
    }

    /**
     * 获取交互反馈服务
     *
     * @return 交互反馈服务，如果未初始化返回 null
     */
    public static InteractionFeedbackService getInteractionFeedbackService() {
        if (!initialized || factory == null) {
            log.warn("SceneServices not initialized, cannot get InteractionFeedbackService");
            return null;
        }
        return factory.getInteractionFeedbackService();
    }

    /**
     * 获取工具注册表
     *
     * @return 工具注册表，如果未初始化返回 null
     */
    public static ToolRegistry getToolRegistry() {
        if (!initialized || factory == null) {
            log.warn("SceneServices not initialized, cannot get ToolRegistry");
            return null;
        }
        return factory.getToolRegistry();
    }

    /**
     * 获取工具编排器
     *
     * @return 工具编排器，如果未初始化返回 null
     */
    public static ToolOrchestrator getToolOrchestrator() {
        if (!initialized || factory == null) {
            log.warn("SceneServices not initialized, cannot get ToolOrchestrator");
            return null;
        }
        return factory.getToolOrchestrator();
    }

    /**
     * 重置服务（主要用于测试）
     */
    public static synchronized void reset() {
        factory = null;
        initialized = false;
        log.info("SceneServices reset");
    }
}

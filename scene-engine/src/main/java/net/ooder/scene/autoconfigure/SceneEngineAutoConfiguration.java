package net.ooder.scene.autoconfigure;

import net.ooder.scene.llm.LlmService;
import net.ooder.scene.llm.impl.NoOpLlmService;
import net.ooder.scene.skill.conversation.ConversationService;
import net.ooder.scene.skill.conversation.impl.ConversationServiceImpl;
import net.ooder.scene.skill.conversation.storage.ConversationStorageService;
import net.ooder.scene.skill.conversation.storage.impl.FileConversationStorageService;
import net.ooder.scene.skill.knowledge.*;
import net.ooder.scene.skill.knowledge.impl.*;
import net.ooder.scene.skill.rag.RagApi;
import net.ooder.scene.skill.tool.ToolOrchestrator;
import net.ooder.scene.skill.tool.ToolRegistry;
import net.ooder.scene.skill.tool.impl.ToolOrchestratorImpl;
import net.ooder.scene.skill.tool.impl.ToolRegistryImpl;
import net.ooder.scene.skill.SkillControllerFactory;
import net.ooder.scene.spi.SceneServices;
import net.ooder.scene.spi.SceneServiceFactory;
import net.ooder.scene.spi.impl.DefaultSceneServiceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;

/**
 * Scene Engine 自动配置类
 * <p>将 SE 核心服务暴露为 Spring Bean，供 Skill 插件使用</p>
 * <p>SE 只提供接口和扩展点，具体实现由 Skill 插件提供</p>
 *
 * <p>默认禁用，需要通过配置显式启用：</p>
 * <pre>
 * scene.engine.enabled: true
 * </pre>
 *
 * @author ooder Team
 * @since 2.3.1
 */
@Configuration
@ConditionalOnProperty(name = "scene.engine.enabled", havingValue = "true", matchIfMissing = false)
@EnableConfigurationProperties(SceneEngineProperties.class)
public class SceneEngineAutoConfiguration {

    // 服务字段，用于 @PostConstruct 方法
    private ConversationStorageService storageService;
    private ConversationService conversationService;
    private KnowledgeBaseService knowledgeService;
    private TerminologyService terminologyService;
    private InteractionFeedbackService interactionFeedbackService;
    private ToolRegistry toolRegistry;
    private ToolOrchestrator toolOrchestrator;

    /**
     * 对话存储服务
     * <p>SE 提供默认的文件存储实现</p>
     */
    @Bean
    @ConditionalOnMissingBean(ConversationStorageService.class)
    public ConversationStorageService conversationStorageService(SceneEngineProperties properties) {
        this.storageService = new FileConversationStorageService(properties.getConversation().getStorage().getPath());
        return this.storageService;
    }

    /**
     * 工具注册表
     * <p>SE 提供默认实现</p>
     */
    @Bean
    @ConditionalOnMissingBean(ToolRegistry.class)
    public ToolRegistry toolRegistry() {
        this.toolRegistry = new ToolRegistryImpl();
        return this.toolRegistry;
    }

    /**
     * 默认 LLM 服务
     * <p>当没有实际的 LlmService 实现时，提供空实现</p>
     * <p>避免因缺少 LlmService Bean 导致启动失败</p>
     */
    @Bean
    @ConditionalOnMissingBean(LlmService.class)
    public LlmService defaultLlmService() {
        return new NoOpLlmService();
    }

    /**
     * 工具编排器
     * <p>SE 提供默认实现</p>
     */
    @Bean
    @ConditionalOnMissingBean(ToolOrchestrator.class)
    public ToolOrchestrator toolOrchestrator(ToolRegistry toolRegistry) {
        this.toolOrchestrator = new ToolOrchestratorImpl(toolRegistry);
        return this.toolOrchestrator;
    }

    /**
     * 术语服务
     * <p>SE 提供默认实现</p>
     */
    @Bean
    @ConditionalOnMissingBean(TerminologyService.class)
    public TerminologyService terminologyService() {
        this.terminologyService = new TerminologyServiceImpl();
        return this.terminologyService;
    }

    /**
     * 对话服务
     * <p>只有在 LlmService Bean 存在时才创建</p>
     * <p>LlmService 由 skill-llm 插件提供</p>
     */
    @Bean
    @ConditionalOnBean(LlmService.class)
    @ConditionalOnMissingBean(ConversationService.class)
    public ConversationService conversationService(
            LlmService llmService,
            ConversationStorageService storageService,
            ToolRegistry toolRegistry,
            ToolOrchestrator toolOrchestrator) {

        ConversationServiceImpl service = new ConversationServiceImpl(
                null,  // knowledgeBaseService - 可选
                null,  // ragPipeline - 可选
                toolRegistry,
                toolOrchestrator,
                llmService,
                null,  // auditService - 可选
                storageService,
                null   // feedbackService - 可选
        );
        this.conversationService = service;
        return service;
    }

    /**
     * 交互反馈服务
     * <p>只有在 ConversationService Bean 存在时才创建</p>
     */
    @Bean
    @ConditionalOnBean(ConversationService.class)
    @ConditionalOnMissingBean(InteractionFeedbackService.class)
    public InteractionFeedbackService interactionFeedbackService(
            TerminologyService terminologyService,
            ConversationService conversationService) {
        this.interactionFeedbackService = new InteractionFeedbackServiceImpl(
                null,  // knowledgeBaseService - 可选
                terminologyService,
                conversationService
        );
        return this.interactionFeedbackService;
    }

    /**
     * 初始化 SceneServices 静态入口
     * <p>在 SE 启动时初始化，供 Skill 插件使用</p>
     */
    @PostConstruct
    public void initSceneServices() {
        SceneServiceFactory factory = new DefaultSceneServiceFactory(
                storageService,
                conversationService,
                knowledgeService,
                terminologyService,
                interactionFeedbackService,
                toolRegistry,
                toolOrchestrator
        );

        SceneServices.setFactory(factory);

        // 初始化 SkillControllerFactory
        SkillControllerFactory.initialize();
    }

}

package net.ooder.scene.autoconfigure;

import net.ooder.scene.llm.LlmService;
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
import net.ooder.scene.spi.SceneServices;
import net.ooder.scene.spi.SceneServiceFactory;
import net.ooder.scene.spi.impl.DefaultSceneServiceFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * Scene Engine 自动配置类
 * <p>将 SE 核心服务暴露为 Spring Bean，供 Skill 插件使用</p>
 *
 * @author ooder Team
 * @since 2.3.1
 */
@Configuration
@EnableConfigurationProperties(SceneEngineProperties.class)
public class SceneEngineAutoConfiguration {

    /**
     * 对话存储服务
     */
    @Bean
    @ConditionalOnMissingBean(ConversationStorageService.class)
    public ConversationStorageService conversationStorageService(SceneEngineProperties properties) {
        return new FileConversationStorageService(properties.getConversation().getStorage().getPath());
    }

    /**
     * 工具注册表
     */
    @Bean
    @ConditionalOnMissingBean(ToolRegistry.class)
    public ToolRegistry toolRegistry() {
        return new ToolRegistryImpl();
    }

    /**
     * 工具编排器
     */
    @Bean
    @ConditionalOnMissingBean(ToolOrchestrator.class)
    public ToolOrchestrator toolOrchestrator(ToolRegistry toolRegistry) {
        return new ToolOrchestratorImpl(toolRegistry);
    }

    /**
     * 术语服务
     */
    @Bean
    @ConditionalOnMissingBean(TerminologyService.class)
    public TerminologyService terminologyService() {
        return new TerminologyServiceImpl();
    }

    /**
     * 交互反馈服务
     */
    @Bean
    @ConditionalOnMissingBean(InteractionFeedbackService.class)
    public InteractionFeedbackService interactionFeedbackService(
            KnowledgeBaseService knowledgeBaseService,
            TerminologyService terminologyService,
            ConversationService conversationService) {
        return new InteractionFeedbackServiceImpl(
                knowledgeBaseService,
                terminologyService,
                conversationService
        );
    }

    /**
     * 对话服务
     * <p>注意：此 Bean 依赖于其他服务，需要最后初始化</p>
     */
    @Bean
    @ConditionalOnMissingBean(ConversationService.class)
    public ConversationService conversationService(
            LlmService llmService,
            ConversationStorageService storageService,
            ToolRegistry toolRegistry,
            ToolOrchestrator toolOrchestrator,
            InteractionFeedbackService feedbackService) {

        ConversationServiceImpl service = new ConversationServiceImpl(
                null,  // knowledgeBaseService - 可选
                null,  // ragPipeline - 可选
                toolRegistry,
                toolOrchestrator,
                llmService,
                null,  // auditService - 可选
                storageService,
                feedbackService
        );

        return service;
    }

    /**
     * 初始化 SceneServices 静态入口
     * <p>在 SE 启动时初始化，供 Skill 插件使用</p>
     */
    @PostConstruct
    public void initSceneServices(
            ConversationStorageService storageService,
            ConversationService conversationService,
            KnowledgeBaseService knowledgeService,
            TerminologyService terminologyService,
            InteractionFeedbackService interactionFeedbackService,
            ToolRegistry toolRegistry,
            ToolOrchestrator toolOrchestrator) {

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
    }
}

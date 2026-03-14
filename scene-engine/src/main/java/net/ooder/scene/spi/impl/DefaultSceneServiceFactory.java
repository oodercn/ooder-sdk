package net.ooder.scene.spi.impl;

import net.ooder.scene.skill.conversation.ConversationService;
import net.ooder.scene.skill.conversation.storage.ConversationStorageService;
import net.ooder.scene.skill.knowledge.InteractionFeedbackService;
import net.ooder.scene.skill.knowledge.KnowledgeBaseService;
import net.ooder.scene.skill.knowledge.TerminologyService;
import net.ooder.scene.skill.tool.ToolOrchestrator;
import net.ooder.scene.skill.tool.ToolRegistry;
import net.ooder.scene.spi.SceneServiceFactory;

/**
 * Scene Service 工厂默认实现
 * <p>包装 SE 服务，提供给 SceneServices 静态入口使用</p>
 *
 * @author ooder Team
 * @since 2.3.1
 * @see net.ooder.scene.spi.SceneServices
 */
public class DefaultSceneServiceFactory implements SceneServiceFactory {

    private final ConversationStorageService storageService;
    private final ConversationService conversationService;
    private final KnowledgeBaseService knowledgeService;
    private final TerminologyService terminologyService;
    private final InteractionFeedbackService interactionFeedbackService;
    private final ToolRegistry toolRegistry;
    private final ToolOrchestrator toolOrchestrator;

    public DefaultSceneServiceFactory(
            ConversationStorageService storageService,
            ConversationService conversationService,
            KnowledgeBaseService knowledgeService,
            TerminologyService terminologyService,
            InteractionFeedbackService interactionFeedbackService,
            ToolRegistry toolRegistry,
            ToolOrchestrator toolOrchestrator) {
        this.storageService = storageService;
        this.conversationService = conversationService;
        this.knowledgeService = knowledgeService;
        this.terminologyService = terminologyService;
        this.interactionFeedbackService = interactionFeedbackService;
        this.toolRegistry = toolRegistry;
        this.toolOrchestrator = toolOrchestrator;
    }

    @Override
    public ConversationStorageService getStorageService() {
        return storageService;
    }

    @Override
    public ConversationService getConversationService() {
        return conversationService;
    }

    @Override
    public KnowledgeBaseService getKnowledgeService() {
        return knowledgeService;
    }

    @Override
    public TerminologyService getTerminologyService() {
        return terminologyService;
    }

    @Override
    public InteractionFeedbackService getInteractionFeedbackService() {
        return interactionFeedbackService;
    }

    @Override
    public ToolRegistry getToolRegistry() {
        return toolRegistry;
    }

    @Override
    public ToolOrchestrator getToolOrchestrator() {
        return toolOrchestrator;
    }
}

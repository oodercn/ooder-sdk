package net.ooder.scene.spi.impl;

import net.ooder.scene.skill.conversation.ConversationService;
import net.ooder.scene.skill.conversation.storage.ConversationStorageService;
import net.ooder.scene.skill.knowledge.InteractionFeedbackService;
import net.ooder.scene.skill.knowledge.KnowledgeBaseService;
import net.ooder.scene.skill.knowledge.TerminologyService;
import net.ooder.scene.skill.tool.ToolOrchestrator;
import net.ooder.scene.skill.tool.ToolRegistry;
import net.ooder.scene.spi.SceneEngineServiceProvider;

/**
 * 默认 Scene Engine 服务提供者实现
 * <p>通过构造函数注入所有服务，供 Skill 插件使用</p>
 *
 * @author ooder Team
 * @since 2.3.1
 */
public class DefaultSceneEngineServiceProvider implements SceneEngineServiceProvider {

    private final ConversationStorageService storageService;
    private final ConversationService conversationService;
    private final KnowledgeBaseService knowledgeBaseService;
    private final TerminologyService terminologyService;
    private final InteractionFeedbackService interactionFeedbackService;
    private final ToolRegistry toolRegistry;
    private final ToolOrchestrator toolOrchestrator;

    public DefaultSceneEngineServiceProvider(ConversationStorageService storageService,
                                               ConversationService conversationService,
                                               KnowledgeBaseService knowledgeBaseService,
                                               TerminologyService terminologyService,
                                               InteractionFeedbackService interactionFeedbackService,
                                               ToolRegistry toolRegistry,
                                               ToolOrchestrator toolOrchestrator) {
        this.storageService = storageService;
        this.conversationService = conversationService;
        this.knowledgeBaseService = knowledgeBaseService;
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
    public KnowledgeBaseService getKnowledgeBaseService() {
        return knowledgeBaseService;
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

    @Override
    public String getProviderName() {
        return "SceneEngine-Default";
    }

    @Override
    public String getProviderVersion() {
        return "2.3.1";
    }

    @Override
    public boolean isServiceAvailable(Class<?> serviceType) {
        if (serviceType == null) {
            return false;
        }
        
        if (serviceType.isInstance(conversationService)) {
            return conversationService != null;
        }
        if (serviceType.isInstance(knowledgeBaseService)) {
            return knowledgeBaseService != null;
        }
        if (serviceType.isInstance(terminologyService)) {
            return terminologyService != null;
        }
        if (serviceType.isInstance(interactionFeedbackService)) {
            return interactionFeedbackService != null;
        }
        if (serviceType.isInstance(toolRegistry)) {
            return toolRegistry != null;
        }
        if (serviceType.isInstance(toolOrchestrator)) {
            return toolOrchestrator != null;
        }
        if (serviceType.isInstance(storageService)) {
            return storageService != null;
        }
        
        return false;
    }
}

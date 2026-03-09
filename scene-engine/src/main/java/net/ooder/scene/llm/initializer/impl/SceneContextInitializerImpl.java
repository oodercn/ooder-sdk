package net.ooder.scene.llm.initializer.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.ooder.scene.llm.context.*;
import net.ooder.scene.llm.initializer.SceneContextInitializeRequest;
import net.ooder.scene.llm.initializer.SceneContextInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 场景上下文初始化器实现
 * 
 * <p>负责创建和管理 LlmSceneContext 的完整生命周期。</p>
 *
 * @author Ooder Team
 * @since 2.3.1
 */
public class SceneContextInitializerImpl implements SceneContextInitializer {
    
    private static final Logger log = LoggerFactory.getLogger(SceneContextInitializerImpl.class);
    
    private static final long DEFAULT_TTL = 30 * 60 * 1000; // 30分钟
    private static final long DEFAULT_IDLE_TIMEOUT = 10 * 60 * 1000; // 10分钟
    
    private final LlmContextRegistry contextRegistry;
    private final ObjectMapper objectMapper;
    
    // 上下文元数据存储
    private final Map<String, ContextMetadata> contextMetadata;
    
    public SceneContextInitializerImpl(LlmContextRegistry contextRegistry) {
        this.contextRegistry = contextRegistry;
        this.objectMapper = new ObjectMapper();
        this.contextMetadata = new ConcurrentHashMap<>();
    }
    
    @Override
    public LlmSceneContext initialize(String sceneId, SceneContextInitializeRequest request) {
        if (sceneId == null || sceneId.isEmpty()) {
            throw new IllegalArgumentException("SceneId must not be null or empty");
        }
        
        log.debug("Initializing context for scene: {}", sceneId);
        
        // 创建基础上下文
        LlmSceneContext context = new LlmSceneContext();
        context.setContextId(generateContextId());
        context.setSceneId(sceneId);
        context.setAgentId(request.getAgentId());
        context.setSandboxId(request.getSandboxId());
        
        // 构建5种子上下文
        context.setUserContext(buildUserContext(request));
        context.setNlpContext(buildNlpContext(request));
        context.setKnowledgeContext(buildKnowledgeContext(request));
        context.setSecurityContext(buildSecurityContext(request));
        
        // 添加扩展属性
        if (request.getExtendedAttributes() != null) {
            request.getExtendedAttributes().forEach(context::setExtendedAttribute);
        }
        
        // 注册上下文
        contextRegistry.register(context);
        
        // 记录元数据
        ContextMetadata metadata = new ContextMetadata();
        metadata.contextId = context.getContextId();
        metadata.sceneId = sceneId;
        metadata.createdAt = System.currentTimeMillis();
        metadata.ttlMillis = request.getTtlMillis() != null ? request.getTtlMillis() : DEFAULT_TTL;
        metadata.idleTimeoutMillis = request.getIdleTimeoutMillis() != null ? 
            request.getIdleTimeoutMillis() : DEFAULT_IDLE_TIMEOUT;
        contextMetadata.put(context.getContextId(), metadata);
        
        log.info("Context initialized: contextId={}, sceneId={}", context.getContextId(), sceneId);
        
        return context;
    }
    
    @Override
    public LlmSceneContext restore(String contextId) {
        if (contextId == null || contextId.isEmpty()) {
            return null;
        }
        
        LlmSceneContext context = contextRegistry.get(contextId);
        if (context != null) {
            context.touch();
            log.debug("Context restored: contextId={}", contextId);
        }
        
        return context;
    }
    
    @Override
    public LlmSceneContext restoreFromSerialized(String serialized) {
        if (serialized == null || serialized.isEmpty()) {
            throw new IllegalArgumentException("Serialized context must not be null or empty");
        }
        
        try {
            LlmSceneContext context = objectMapper.readValue(serialized, LlmSceneContext.class);
            // 生成新的contextId避免冲突
            context.setContextId(generateContextId());
            contextRegistry.register(context);
            
            log.info("Context restored from serialized data: contextId={}", context.getContextId());
            return context;
        } catch (Exception e) {
            log.error("Failed to restore context from serialized data", e);
            throw new RuntimeException("Failed to restore context", e);
        }
    }
    
    @Override
    public String serialize(LlmSceneContext context) {
        if (context == null) {
            return null;
        }
        
        try {
            return objectMapper.writeValueAsString(context);
        } catch (Exception e) {
            log.error("Failed to serialize context: {}", e.getMessage());
            throw new RuntimeException("Failed to serialize context", e);
        }
    }
    
    @Override
    public String serializePartial(LlmSceneContext context, Set<ContextPart> parts) {
        if (context == null) {
            return null;
        }
        
        if (parts == null || parts.isEmpty()) {
            return serialize(context);
        }
        
        try {
            Map<String, Object> partial = new HashMap<>();
            partial.put("contextId", context.getContextId());
            partial.put("sceneId", context.getSceneId());
            partial.put("agentId", context.getAgentId());
            partial.put("sandboxId", context.getSandboxId());
            partial.put("createdAt", context.getCreatedAt());
            partial.put("lastAccessedAt", context.getLastAccessedAt());
            
            for (ContextPart part : parts) {
                switch (part) {
                    case SCENE_CONTEXT:
                        // 基础信息已在上面添加
                        break;
                    case NLP_CONTEXT:
                        partial.put("nlpContext", context.getNlpContext());
                        break;
                    case KNOWLEDGE_CONTEXT:
                        partial.put("knowledgeContext", context.getKnowledgeContext());
                        break;
                    case SECURITY_CONTEXT:
                        partial.put("securityContext", context.getSecurityContext());
                        break;
                    case TOOL_CONTEXT:
                        // ToolContext 暂未实现
                        break;
                }
            }
            
            return objectMapper.writeValueAsString(partial);
        } catch (Exception e) {
            log.error("Failed to serialize partial context: {}", e.getMessage());
            throw new RuntimeException("Failed to serialize partial context", e);
        }
    }
    
    @Override
    public void destroy(String contextId) {
        if (contextId == null || contextId.isEmpty()) {
            return;
        }
        
        contextRegistry.remove(contextId);
        contextMetadata.remove(contextId);
        
        log.info("Context destroyed: contextId={}", contextId);
    }
    
    @Override
    public boolean isExpired(String contextId) {
        if (contextId == null || contextId.isEmpty()) {
            return true;
        }
        
        ContextMetadata metadata = contextMetadata.get(contextId);
        if (metadata == null) {
            return true;
        }
        
        LlmSceneContext context = contextRegistry.get(contextId);
        if (context == null) {
            return true;
        }
        
        long now = System.currentTimeMillis();
        long age = now - metadata.createdAt;
        long idle = now - context.getLastAccessedAt();
        
        return age > metadata.ttlMillis || idle > metadata.idleTimeoutMillis;
    }
    
    @Override
    public ContextStatus getStatus(String contextId) {
        if (contextId == null || contextId.isEmpty()) {
            return ContextStatus.NOT_FOUND;
        }
        
        LlmSceneContext context = contextRegistry.get(contextId);
        if (context == null) {
            return ContextStatus.NOT_FOUND;
        }
        
        if (isExpired(contextId)) {
            return ContextStatus.EXPIRED;
        }
        
        return ContextStatus.ACTIVE;
    }
    
    // ============ 私有方法 ============
    
    private String generateContextId() {
        return "ctx-" + UUID.randomUUID().toString().replace("-", "").substring(0, 16);
    }
    
    private UserContext buildUserContext(SceneContextInitializeRequest request) {
        UserContext userContext = new UserContext();
        userContext.setUserId(request.getUserId());
        userContext.setUserName(request.getUserName());
        userContext.setDomainId(request.getDomainId());
        userContext.setSessionId(request.getSessionId());
        
        if (request.getUserRoles() != null) {
            request.getUserRoles().forEach(userContext::addRole);
        }
        
        if (request.getUserPermissions() != null) {
            request.getUserPermissions().forEach(userContext::addPermission);
        }
        
        return userContext;
    }
    
    private NlpContext buildNlpContext(SceneContextInitializeRequest request) {
        NlpContext nlpContext = new NlpContext();
        nlpContext.setComponentType(request.getComponentType());
        nlpContext.setModuleViewType(request.getModuleViewType());
        
        if (request.getNlpConfig() != null) {
            nlpContext.setModuleConfig(new HashMap<>(request.getNlpConfig()));
        }
        
        return nlpContext;
    }
    
    private KnowledgeContext buildKnowledgeContext(SceneContextInitializeRequest request) {
        KnowledgeContext knowledgeContext = new KnowledgeContext();
        knowledgeContext.setKnowledgeBaseId(request.getKnowledgeBaseId());
        
        if (request.getAccessibleKnowledgeBases() != null) {
            request.getAccessibleKnowledgeBases().forEach(knowledgeContext::addAccessibleKnowledgeBase);
        }
        
        if (request.getSearchFilters() != null) {
            request.getSearchFilters().forEach(knowledgeContext::addSearchFilter);
        }
        
        return knowledgeContext;
    }
    
    private SecurityContext buildSecurityContext(SceneContextInitializeRequest request) {
        SecurityContext securityContext = new SecurityContext();
        securityContext.setSecurityLevel(request.getSecurityLevel());
        securityContext.setSessionId(request.getSessionId());
        securityContext.setTraceId(request.getTraceId());
        securityContext.setIpAddress(request.getIpAddress());
        securityContext.setUserAgent(request.getUserAgent());
        
        return securityContext;
    }
    
    /**
     * 上下文元数据
     */
    private static class ContextMetadata {
        String contextId;
        String sceneId;
        long createdAt;
        long ttlMillis;
        long idleTimeoutMillis;
    }
}

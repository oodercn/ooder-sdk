package net.ooder.sdk.llm.scene.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONWriter;
import lombok.extern.slf4j.Slf4j;
import net.ooder.sdk.llm.scene.*;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 场景上下文初始化器实现
 */
@Slf4j
public class SceneContextInitializerImpl implements SceneContextInitializer {

    private final Map<String, LlmSceneContext> contextStore = new ConcurrentHashMap<>();
    private final ScheduledExecutorService cleanupScheduler;

    public SceneContextInitializerImpl() {
        this.cleanupScheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "scene-context-cleanup");
            t.setDaemon(true);
            return t;
        });
        // 启动定时清理任务，每5分钟执行一次
        this.cleanupScheduler.scheduleAtFixedRate(this::cleanupExpiredContexts, 5, 5, TimeUnit.MINUTES);
    }

    @Override
    public LlmSceneContext initialize(String sceneId, SceneContextInitializeRequest request) {
        String contextId = generateContextId();

        LlmSceneContext context = LlmSceneContext.builder()
                .contextId(contextId)
                .sceneId(sceneId)
                .userId(request.getUserId())
                .sceneContext(request.getSceneContext())
                .nlpContext(request.getNlpContext())
                .knowledgeContext(request.getKnowledgeContext())
                .toolContext(request.getToolContext())
                .securityContext(request.getSecurityContext())
                .status(LlmSceneContext.ContextStatus.INITIALIZING)
                .build();

        // 如果有父上下文，复制相关配置
        if (request.getParentContextId() != null) {
            LlmSceneContext parentContext = contextStore.get(request.getParentContextId());
            if (parentContext != null) {
                inheritFromParent(context, parentContext);
            }
        }

        // 存储上下文
        contextStore.put(contextId, context);
        context.setStatus(LlmSceneContext.ContextStatus.ACTIVE);

        log.info("Scene context initialized: {} for scene: {}", contextId, sceneId);
        return context;
    }

    @Override
    public LlmSceneContext restore(String contextId) {
        LlmSceneContext context = contextStore.get(contextId);
        if (context == null) {
            log.warn("Context not found for restore: {}", contextId);
            return null;
        }

        if (context.isExpired()) {
            log.warn("Context expired and cannot be restored: {}", contextId);
            context.setStatus(LlmSceneContext.ContextStatus.EXPIRED);
            return null;
        }

        context.touch();
        context.setStatus(LlmSceneContext.ContextStatus.ACTIVE);
        log.info("Scene context restored: {}", contextId);
        return context;
    }

    @Override
    public String serialize(LlmSceneContext context) {
        if (context == null) {
            return null;
        }
        return JSON.toJSONString(context, JSONWriter.Feature.WriteMapNullValue);
    }

    @Override
    public String serializePartial(LlmSceneContext context, Set<LlmSceneContext.ContextPart> parts) {
        if (context == null || parts == null || parts.isEmpty()) {
            return serialize(context);
        }

        JSONObject json = new JSONObject();
        json.put("contextId", context.getContextId());
        json.put("sceneId", context.getSceneId());
        json.put("userId", context.getUserId());
        json.put("createdAt", context.getCreatedAt());
        json.put("lastAccessedAt", context.getLastAccessedAt());
        json.put("expireAfter", context.getExpireAfter());
        json.put("status", context.getStatus());
        json.put("metadata", context.getMetadata());

        // 只序列化指定的部分
        for (LlmSceneContext.ContextPart part : parts) {
            Object subContext = context.getSubContext(part);
            if (subContext != null) {
                json.put(part.name().toLowerCase(), subContext);
            }
        }

        return json.toJSONString();
    }

    @Override
    public LlmSceneContext deserialize(String serialized) {
        if (serialized == null || serialized.isEmpty()) {
            return null;
        }
        return JSON.parseObject(serialized, LlmSceneContext.class);
    }

    @Override
    public void destroy(String contextId) {
        LlmSceneContext context = contextStore.remove(contextId);
        if (context != null) {
            context.setStatus(LlmSceneContext.ContextStatus.DESTROYED);
            log.info("Scene context destroyed: {}", contextId);
        }
    }

    @Override
    public boolean exists(String contextId) {
        return contextStore.containsKey(contextId);
    }

    @Override
    public void touch(String contextId) {
        LlmSceneContext context = contextStore.get(contextId);
        if (context != null) {
            context.touch();
        }
    }

    /**
     * 从父上下文继承配置
     */
    private void inheritFromParent(LlmSceneContext child, LlmSceneContext parent) {
        // 继承安全配置
        if (child.getSecurityContext() == null && parent.getSecurityContext() != null) {
            child.setSecurityContext(parent.getSecurityContext());
        }

        // 继承知识库配置
        if (child.getKnowledgeContext() == null && parent.getKnowledgeContext() != null) {
            child.setKnowledgeContext(parent.getKnowledgeContext());
        }

        // 继承元数据
        if (parent.getMetadata() != null) {
            child.getMetadata().putAll(parent.getMetadata());
        }

        // 设置过期时间与父上下文相同
        child.setExpireAfter(parent.getExpireAfter());
    }

    /**
     * 生成上下文ID
     */
    private String generateContextId() {
        return "ctx_" + UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * 清理过期上下文
     */
    private void cleanupExpiredContexts() {
        int count = 0;
        for (Map.Entry<String, LlmSceneContext> entry : contextStore.entrySet()) {
            if (entry.getValue().isExpired()) {
                entry.getValue().setStatus(LlmSceneContext.ContextStatus.EXPIRED);
                contextStore.remove(entry.getKey());
                count++;
            }
        }
        if (count > 0) {
            log.info("Cleaned up {} expired scene contexts", count);
        }
    }

    /**
     * 关闭清理调度器
     */
    public void shutdown() {
        cleanupScheduler.shutdown();
    }
}

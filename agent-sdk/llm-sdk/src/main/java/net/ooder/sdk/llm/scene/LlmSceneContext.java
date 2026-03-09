package net.ooder.sdk.llm.scene;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.HashMap;
import java.util.Map;

/**
 * LLM 场景上下文
 * 包含5种子上下文：场景、NLP、知识库、工具、安全
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LlmSceneContext {

    /**
     * 上下文ID
     */
    private String contextId;

    /**
     * 场景ID
     */
    private String sceneId;

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 场景上下文
     */
    private SceneContext sceneContext;

    /**
     * NLP上下文
     */
    private NlpContext nlpContext;

    /**
     * 知识库上下文
     */
    private KnowledgeContext knowledgeContext;

    /**
     * 工具上下文
     */
    private ToolContext toolContext;

    /**
     * 安全上下文
     */
    private SecurityContext securityContext;

    /**
     * 创建时间
     */
    @Builder.Default
    private long createdAt = System.currentTimeMillis();

    /**
     * 最后访问时间
     */
    @Builder.Default
    private long lastAccessedAt = System.currentTimeMillis();

    /**
     * 过期时间（毫秒）
     */
    @Builder.Default
    private long expireAfter = 30 * 60 * 1000; // 默认30分钟

    /**
     * 元数据
     */
    @Builder.Default
    private Map<String, Object> metadata = new HashMap<>();

    /**
     * 状态
     */
    @Builder.Default
    private ContextStatus status = ContextStatus.ACTIVE;

    /**
     * 上下文状态枚举
     */
    public enum ContextStatus {
        CREATED,    // 已创建
        INITIALIZING, // 初始化中
        ACTIVE,     // 活跃
        EXPIRED,    // 已过期
        DESTROYED   // 已销毁
    }

    /**
     * 更新访问时间
     */
    public void touch() {
        this.lastAccessedAt = System.currentTimeMillis();
    }

    /**
     * 检查是否过期
     */
    public boolean isExpired() {
        return System.currentTimeMillis() - lastAccessedAt > expireAfter;
    }

    /**
     * 获取指定类型的子上下文
     */
    @SuppressWarnings("unchecked")
    public <T> T getSubContext(ContextPart part) {
        switch (part) {
            case SCENE_CONTEXT:
                return (T) sceneContext;
            case NLP_CONTEXT:
                return (T) nlpContext;
            case KNOWLEDGE_CONTEXT:
                return (T) knowledgeContext;
            case TOOL_CONTEXT:
                return (T) toolContext;
            case SECURITY_CONTEXT:
                return (T) securityContext;
            default:
                return null;
        }
    }

    /**
     * 设置子上下文
     */
    public void setSubContext(ContextPart part, Object subContext) {
        switch (part) {
            case SCENE_CONTEXT:
                this.sceneContext = (SceneContext) subContext;
                break;
            case NLP_CONTEXT:
                this.nlpContext = (NlpContext) subContext;
                break;
            case KNOWLEDGE_CONTEXT:
                this.knowledgeContext = (KnowledgeContext) subContext;
                break;
            case TOOL_CONTEXT:
                this.toolContext = (ToolContext) subContext;
                break;
            case SECURITY_CONTEXT:
                this.securityContext = (SecurityContext) subContext;
                break;
        }
    }

    /**
     * 上下文部分枚举
     */
    public enum ContextPart {
        SCENE_CONTEXT,
        NLP_CONTEXT,
        KNOWLEDGE_CONTEXT,
        TOOL_CONTEXT,
        SECURITY_CONTEXT
    }
}

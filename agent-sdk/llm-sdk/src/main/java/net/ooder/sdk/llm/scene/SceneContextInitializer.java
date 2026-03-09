package net.ooder.sdk.llm.scene;

import java.util.Set;

/**
 * 场景上下文初始化器
 * 负责创建和管理 LlmSceneContext 的生命周期
 */
public interface SceneContextInitializer {

    /**
     * 初始化场景上下文
     *
     * @param sceneId 场景ID
     * @param request 初始化请求
     * @return 场景上下文
     */
    LlmSceneContext initialize(String sceneId, SceneContextInitializeRequest request);

    /**
     * 恢复上下文
     *
     * @param contextId 上下文ID
     * @return 场景上下文
     */
    LlmSceneContext restore(String contextId);

    /**
     * 序列化上下文
     *
     * @param context 场景上下文
     * @return 序列化后的字符串
     */
    String serialize(LlmSceneContext context);

    /**
     * 序列化部分上下文
     *
     * @param context 场景上下文
     * @param parts   需要序列化的部分
     * @return 序列化后的字符串
     */
    String serializePartial(LlmSceneContext context, Set<LlmSceneContext.ContextPart> parts);

    /**
     * 反序列化上下文
     *
     * @param serialized 序列化字符串
     * @return 场景上下文
     */
    LlmSceneContext deserialize(String serialized);

    /**
     * 销毁上下文
     *
     * @param contextId 上下文ID
     */
    void destroy(String contextId);

    /**
     * 检查上下文是否存在
     *
     * @param contextId 上下文ID
     * @return 是否存在
     */
    boolean exists(String contextId);

    /**
     * 更新上下文访问时间
     *
     * @param contextId 上下文ID
     */
    void touch(String contextId);

    /**
     * 初始化请求
     */
    class SceneContextInitializeRequest {
        private String userId;
        private String parentContextId;
        private SceneContext sceneContext;
        private NlpContext nlpContext;
        private KnowledgeContext knowledgeContext;
        private ToolContext toolContext;
        private SecurityContext securityContext;

        // Getters and Setters
        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }

        public String getParentContextId() { return parentContextId; }
        public void setParentContextId(String parentContextId) { this.parentContextId = parentContextId; }

        public SceneContext getSceneContext() { return sceneContext; }
        public void setSceneContext(SceneContext sceneContext) { this.sceneContext = sceneContext; }

        public NlpContext getNlpContext() { return nlpContext; }
        public void setNlpContext(NlpContext nlpContext) { this.nlpContext = nlpContext; }

        public KnowledgeContext getKnowledgeContext() { return knowledgeContext; }
        public void setKnowledgeContext(KnowledgeContext knowledgeContext) { this.knowledgeContext = knowledgeContext; }

        public ToolContext getToolContext() { return toolContext; }
        public void setToolContext(ToolContext toolContext) { this.toolContext = toolContext; }

        public SecurityContext getSecurityContext() { return securityContext; }
        public void setSecurityContext(SecurityContext securityContext) { this.securityContext = securityContext; }
    }
}

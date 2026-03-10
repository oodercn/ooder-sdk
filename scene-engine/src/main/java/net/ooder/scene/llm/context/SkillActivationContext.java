package net.ooder.scene.llm.context;

import net.ooder.scene.discovery.UnifiedSkillRegistry;
import net.ooder.scene.llm.knowledge.SkillsMdLoader;
import net.ooder.skills.api.SkillPackage;

import java.io.Serializable;
import java.util.*;

/**
 * Skill 激活上下文
 * 
 * <p>在 Skill 激活时创建，包含完整的 LLM 运行时所需信息：</p>
 * <ul>
 *   <li>角色上下文 - 定义 AI 助手的角色和行为</li>
 *   <li>知识库上下文 - 多级加载的知识内容</li>
 *   <li>函数上下文 - Function Calling 定义</li>
 *   <li>记忆上下文 - 对话历史</li>
 * </ul>
 *
 * <h3>使用示例</h3>
 * <pre>
 * // 激活 Skill
 * SkillActivationContext context = SkillActivationContext.activate(
 *     ActivationRequest.builder()
 *         .skillId("recruitment-skill")
 *         .userId("user-xxx")
 *         .roleId("hr-assistant")
 *         .knowledgeLevel(KnowledgeLoadLevel.ADVANCED)
 *         .build()
 * );
 *
 * // 获取函数定义
 * List&lt;Map&lt;String, Object&gt;&gt; tools = context.getFunctionContext().toTools();
 *
 * // 获取系统提示词
 * String prompt = context.buildSystemPrompt();
 * </pre>
 *
 * @author Ooder Team
 * @version 2.3.1
 * @since 2.3.1
 */
public class SkillActivationContext implements Serializable {

    private static final long serialVersionUID = 1L;

    private String activationId;
    private String skillId;
    private String sceneId;
    private String userId;
    private String sessionId;

    private RoleContext roleContext;
    private KnowledgeContext knowledgeContext;
    private FunctionContext functionContext;
    private MemoryContext memoryContext;

    private ActivationState state;
    private long activatedAt;
    private long lastActiveAt;
    private Map<String, Object> metadata;

    // 静态引用
    private static UnifiedSkillRegistry skillRegistry;
    private static SkillsMdLoader skillsMdLoader;

    public SkillActivationContext() {
        this.metadata = new HashMap<>();
        this.state = ActivationState.CREATED;
    }

    /**
     * 设置 SkillRegistry（在初始化时调用）
     */
    public static void setSkillRegistry(UnifiedSkillRegistry registry) {
        skillRegistry = registry;
        skillsMdLoader = new SkillsMdLoader(registry);
    }

    /**
     * 激活 Skill 并创建上下文（使用 ActivationRequest）
     */
    public static SkillActivationContext activate(ActivationRequest request) {
        SkillActivationContext context = new SkillActivationContext();
        context.activationId = generateActivationId();
        context.skillId = request.getSkillId();
        context.sceneId = request.getSceneId();
        context.userId = request.getUserId();
        context.sessionId = request.getSessionId();
        context.state = ActivationState.CREATED;

        try {
            // 1. 加载角色上下文
            context.roleContext = loadRoleContext(request.getRoleId());

            // 2. 加载知识库上下文（多级加载）
            context.knowledgeContext = loadKnowledgeContext(
                request.getSkillId(), 
                request.getKnowledgeBaseIds(),
                request.getKnowledgeLevel()
            );

            // 3. 注入函数定义
            context.functionContext = loadFunctionContext(request.getSkillId());

            // 4. 恢复对话记忆
            context.memoryContext = loadMemoryContext(request.getSessionId());

            context.state = ActivationState.ACTIVATED;
            context.activatedAt = System.currentTimeMillis();
            context.lastActiveAt = context.activatedAt;

        } catch (Exception e) {
            context.state = ActivationState.ERROR;
            throw new RuntimeException("Failed to activate skill: " + request.getSkillId(), e);
        }

        return context;
    }

    /**
     * 激活 Skill 并创建上下文（简化版本）
     */
    public static SkillActivationContext activate(String skillId, String userId, 
                                                   String sessionId, String roleId) {
        ActivationRequest request = ActivationRequest.builder()
            .skillId(skillId)
            .userId(userId)
            .sessionId(sessionId)
            .roleId(roleId)
            .build();
        return activate(request);
    }

    private static String generateActivationId() {
        return "act-" + UUID.randomUUID().toString().substring(0, 8);
    }

    private static RoleContext loadRoleContext(String roleId) {
        if (roleId == null || roleId.isEmpty()) {
            return RoleContext.defaultRole();
        }
        return RoleContext.load(roleId);
    }

    private static KnowledgeContext loadKnowledgeContext(String skillId, 
                                                          List<String> kbIds,
                                                          KnowledgeContext.KnowledgeLoadLevel level) {
        KnowledgeContext context = new KnowledgeContext();
        context.setSkillId(skillId);
        context.setLoadLevel(level != null ? level : KnowledgeContext.KnowledgeLoadLevel.ADVANCED);
        
        if (kbIds != null) {
            context.setAccessibleKnowledgeBases(kbIds);
        }
        
        // 异步加载知识（如果需要）
        if (skillsMdLoader != null) {
            try {
                KnowledgeContext loadedContext = skillsMdLoader.load(skillId, level).get();
                if (loadedContext != null && !loadedContext.getLoadedChunks().isEmpty()) {
                    return loadedContext;
                }
            } catch (Exception e) {
                // 加载失败时使用空上下文
            }
        }
        
        return context;
    }

    private static FunctionContext loadFunctionContext(String skillId) {
        if (skillRegistry != null) {
            try {
                SkillPackage skillPackage = skillRegistry.getSkill(skillId).get();
                if (skillPackage != null) {
                    return FunctionContext.loadFromSkill(skillId, skillPackage);
                }
            } catch (Exception e) {
                // 加载失败时返回空上下文
            }
        }
        return new FunctionContext(skillId);
    }

    private static MemoryContext loadMemoryContext(String sessionId) {
        MemoryContext context = new MemoryContext();
        context.setSessionId(sessionId);
        return context;
    }

    /**
     * 暂停 Skill
     */
    public void pause() {
        if (state == ActivationState.ACTIVATED) {
            this.state = ActivationState.PAUSED;
        }
    }

    /**
     * 恢复 Skill
     */
    public void resume() {
        if (state == ActivationState.PAUSED) {
            this.state = ActivationState.ACTIVATED;
            this.lastActiveAt = System.currentTimeMillis();
        }
    }

    /**
     * 销毁 Skill
     */
    public void destroy() {
        this.state = ActivationState.DESTROYED;
        // 清理资源
        this.memoryContext = null;
        this.functionContext = null;
    }

    /**
     * 构建系统提示词
     */
    public String buildSystemPrompt() {
        StringBuilder sb = new StringBuilder();

        if (roleContext != null) {
            sb.append(roleContext.buildPromptSection());
            sb.append("\n\n");
        }

        if (knowledgeContext != null) {
            sb.append(knowledgeContext.buildPromptSection());
        }

        return sb.toString();
    }

    /**
     * 获取 LLM 调用所需的 Tools
     */
    public List<Map<String, Object>> getTools() {
        if (functionContext != null) {
            return functionContext.toTools();
        }
        return Collections.emptyList();
    }

    /**
     * 获取消息历史
     */
    public List<Map<String, Object>> getMessages() {
        if (memoryContext != null) {
            return memoryContext.getHistory();
        }
        return Collections.emptyList();
    }

    /**
     * 添加用户消息
     */
    public void addUserMessage(String content) {
        if (memoryContext != null) {
            memoryContext.addMessage("user", content);
        }
        this.lastActiveAt = System.currentTimeMillis();
    }

    /**
     * 添加助手消息
     */
    public void addAssistantMessage(String content) {
        if (memoryContext != null) {
            memoryContext.addMessage("assistant", content);
        }
        this.lastActiveAt = System.currentTimeMillis();
    }

    /**
     * 执行函数调用
     */
    public Object executeFunction(String functionName, Map<String, Object> args) {
        if (functionContext != null) {
            return functionContext.execute(functionName, args, this);
        }
        throw new RuntimeException("FunctionContext not initialized");
    }

    public void touch() {
        this.lastActiveAt = System.currentTimeMillis();
    }

    public boolean isExpired(long timeoutMs) {
        return System.currentTimeMillis() - lastActiveAt > timeoutMs;
    }

    public boolean isActive() {
        return state != null && state.isActive();
    }

    // Getters and Setters

    public String getActivationId() {
        return activationId;
    }

    public void setActivationId(String activationId) {
        this.activationId = activationId;
    }

    public String getSkillId() {
        return skillId;
    }

    public void setSkillId(String skillId) {
        this.skillId = skillId;
    }

    public String getSceneId() {
        return sceneId;
    }

    public void setSceneId(String sceneId) {
        this.sceneId = sceneId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public RoleContext getRoleContext() {
        return roleContext;
    }

    public void setRoleContext(RoleContext roleContext) {
        this.roleContext = roleContext;
    }

    public KnowledgeContext getKnowledgeContext() {
        return knowledgeContext;
    }

    public void setKnowledgeContext(KnowledgeContext knowledgeContext) {
        this.knowledgeContext = knowledgeContext;
    }

    public FunctionContext getFunctionContext() {
        return functionContext;
    }

    public void setFunctionContext(FunctionContext functionContext) {
        this.functionContext = functionContext;
    }

    public MemoryContext getMemoryContext() {
        return memoryContext;
    }

    public void setMemoryContext(MemoryContext memoryContext) {
        this.memoryContext = memoryContext;
    }

    public ActivationState getState() {
        return state;
    }

    public void setState(ActivationState state) {
        this.state = state;
    }

    public long getActivatedAt() {
        return activatedAt;
    }

    public void setActivatedAt(long activatedAt) {
        this.activatedAt = activatedAt;
    }

    public long getLastActiveAt() {
        return lastActiveAt;
    }

    public void setLastActiveAt(long lastActiveAt) {
        this.lastActiveAt = lastActiveAt;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }
}

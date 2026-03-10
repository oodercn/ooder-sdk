package net.ooder.scene.llm.context;

import net.ooder.scene.discovery.UnifiedSkillRegistry;
import net.ooder.scene.llm.knowledge.SkillsMdLoader;
import net.ooder.skills.api.SkillPackage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * LLM 运行时上下文组装器
 * 
 * <p>负责整合所有上下文信息，组装成完整的 LLM 运行时上下文：</p>
 * <ul>
 *   <li>角色上下文 (RoleContext)</li>
 *   <li>知识库上下文 (KnowledgeContext)</li>
 *   <li>函数定义上下文 (FunctionContext)</li>
 *   <li>记忆上下文 (MemoryContext)</li>
 * </ul>
 *
 * <h3>使用示例</h3>
 * <pre>
 * LlmRuntimeContextAssembler assembler = new LlmRuntimeContextAssembler(skillRegistry);
 * 
 * AssemblyRequest request = AssemblyRequest.builder()
 *     .skillId("recruitment-skill")
 *     .roleId("hr-assistant")
 *     .sessionId("session-123")
 *     .knowledgeLevel(KnowledgeLoadLevel.ADVANCED)
 *     .build();
 * 
 * LlmSceneContext context = assembler.assemble(request).get();
 * </pre>
 *
 * @author Ooder Team
 * @version 2.3.1
 * @since 2.3.1
 */
public class LlmRuntimeContextAssembler {

    private static final Logger log = LoggerFactory.getLogger(LlmRuntimeContextAssembler.class);

    private final UnifiedSkillRegistry skillRegistry;
    private final SkillsMdLoader skillsMdLoader;

    public LlmRuntimeContextAssembler(UnifiedSkillRegistry skillRegistry) {
        this.skillRegistry = skillRegistry;
        this.skillsMdLoader = new SkillsMdLoader(skillRegistry);
    }

    /**
     * 组装 LLM 运行时上下文
     *
     * @param request 组装请求
     * @return 完整的 LLM 场景上下文
     */
    public CompletableFuture<LlmSceneContext> assemble(AssemblyRequest request) {
        return CompletableFuture.supplyAsync(() -> {
            log.info("Assembling LLM runtime context: skillId={}, roleId={}, sessionId={}",
                    request.getSkillId(), request.getRoleId(), request.getSessionId());

            LlmSceneContext context = new LlmSceneContext();
            context.setContextId(generateContextId());

            try {
                // 获取 Skill 包
                SkillPackage skillPackage = skillRegistry.getSkill(request.getSkillId()).get();
                if (skillPackage == null) {
                    throw new RuntimeException("Skill not found: " + request.getSkillId());
                }

                // 设置扩展属性
                context.setExtendedAttribute("sessionId", request.getSessionId());
                context.setExtendedAttribute("skillId", request.getSkillId());

                // 1. 加载角色上下文
                RoleContext roleContext = loadRoleContext(request.getRoleId(), skillPackage);
                context.setExtendedAttribute("roleContext", roleContext);

                // 2. 加载知识库上下文
                KnowledgeContext knowledgeContext = loadKnowledgeContext(
                        request.getSkillId(), 
                        request.getKnowledgeLevel(),
                        request.getKnowledgeBaseIds()
                ).get();
                context.setKnowledgeContext(knowledgeContext);

                // 3. 加载函数定义上下文
                FunctionContext functionContext = FunctionContext.loadFromSkill(
                        request.getSkillId(), 
                        skillPackage
                );
                context.setExtendedAttribute("functionContext", functionContext);

                // 4. 加载记忆上下文
                MemoryContext memoryContext = loadMemoryContext(request.getSessionId());
                context.setExtendedAttribute("memoryContext", memoryContext);

                // 5. 组装系统提示词
                String systemPrompt = assembleSystemPrompt(roleContext, knowledgeContext);
                context.setExtendedAttribute("systemPrompt", systemPrompt);

                // 6. 组装工具定义
                List<Map<String, Object>> tools = functionContext.toTools();
                context.setExtendedAttribute("tools", tools);

                // 7. 组装消息历史
                List<Map<String, Object>> messages = memoryContext.getHistory();
                context.setExtendedAttribute("messages", messages);

                log.info("LLM runtime context assembled successfully: contextId={}", context.getContextId());

                return context;

            } catch (Exception e) {
                log.error("Failed to assemble LLM runtime context", e);
                throw new RuntimeException("Failed to assemble LLM runtime context", e);
            }
        });
    }

    /**
     * 加载角色上下文
     */
    private RoleContext loadRoleContext(String roleId, SkillPackage skillPackage) {
        // 1. 尝试从请求的角色 ID 加载
        if (roleId != null && !roleId.isEmpty()) {
            RoleContext role = RoleContext.load(roleId);
            if (role != null) {
                return role;
            }
        }

        // 2. 尝试从 Skill 元数据中获取默认角色
        Object defaultRole = skillPackage.getMetadata().get("defaultRole");
        if (defaultRole instanceof String) {
            RoleContext role = RoleContext.load((String) defaultRole);
            if (role != null) {
                return role;
            }
        }

        // 3. 使用默认角色
        return RoleContext.load("assistant");
    }

    /**
     * 加载知识库上下文
     */
    private CompletableFuture<KnowledgeContext> loadKnowledgeContext(
            String skillId,
            KnowledgeContext.KnowledgeLoadLevel level,
            List<String> knowledgeBaseIds) {
        
        // 1. 从 skills.md 加载知识
        CompletableFuture<KnowledgeContext> skillKnowledge = skillsMdLoader.load(skillId, level);

        // 2. 如果有额外的知识库 ID，也加载它们
        if (knowledgeBaseIds != null && !knowledgeBaseIds.isEmpty()) {
            return skillKnowledge.thenCompose(context -> {
                // 这里可以添加从其他知识库加载的逻辑
                // 例如：knowledgeService.loadMultiple(knowledgeBaseIds, level)
                return CompletableFuture.completedFuture(context);
            });
        }

        return skillKnowledge;
    }

    /**
     * 加载记忆上下文
     */
    private MemoryContext loadMemoryContext(String sessionId) {
        // 从会话存储中加载记忆
        // 这里可以实现从数据库或缓存加载历史消息
        MemoryContext memory = new MemoryContext();
        memory.setSessionId(sessionId);
        
        // TODO: 从持久化存储加载历史消息
        // List<Map<String, Object>> history = sessionService.getHistory(sessionId);
        // memory.setHistory(history);
        
        return memory;
    }

    /**
     * 组装系统提示词
     */
    private String assembleSystemPrompt(RoleContext roleContext, KnowledgeContext knowledgeContext) {
        StringBuilder sb = new StringBuilder();

        // 1. 角色定义
        if (roleContext != null) {
            sb.append(roleContext.buildPromptSection());
            sb.append("\n\n");
        }

        // 2. 知识库内容
        if (knowledgeContext != null) {
            sb.append(knowledgeContext.buildPromptSection());
        }

        return sb.toString().trim();
    }

    /**
     * 生成上下文 ID
     */
    private String generateContextId() {
        return "ctx-" + java.util.UUID.randomUUID().toString().substring(0, 8);
    }

    /**
     * 组装请求
     */
    public static class AssemblyRequest {
        private String skillId;
        private String roleId;
        private String sessionId;
        private KnowledgeContext.KnowledgeLoadLevel knowledgeLevel = KnowledgeContext.KnowledgeLoadLevel.ADVANCED;
        private List<String> knowledgeBaseIds;

        public static Builder builder() {
            return new Builder();
        }

        // Getters and Setters
        public String getSkillId() { return skillId; }
        public void setSkillId(String skillId) { this.skillId = skillId; }
        public String getRoleId() { return roleId; }
        public void setRoleId(String roleId) { this.roleId = roleId; }
        public String getSessionId() { return sessionId; }
        public void setSessionId(String sessionId) { this.sessionId = sessionId; }
        public KnowledgeContext.KnowledgeLoadLevel getKnowledgeLevel() { return knowledgeLevel; }
        public void setKnowledgeLevel(KnowledgeContext.KnowledgeLoadLevel knowledgeLevel) { this.knowledgeLevel = knowledgeLevel; }
        public List<String> getKnowledgeBaseIds() { return knowledgeBaseIds; }
        public void setKnowledgeBaseIds(List<String> knowledgeBaseIds) { this.knowledgeBaseIds = knowledgeBaseIds; }

        public static class Builder {
            private AssemblyRequest request = new AssemblyRequest();

            public Builder skillId(String skillId) {
                request.setSkillId(skillId);
                return this;
            }

            public Builder roleId(String roleId) {
                request.setRoleId(roleId);
                return this;
            }

            public Builder sessionId(String sessionId) {
                request.setSessionId(sessionId);
                return this;
            }

            public Builder knowledgeLevel(KnowledgeContext.KnowledgeLoadLevel level) {
                request.setKnowledgeLevel(level);
                return this;
            }

            public Builder knowledgeBaseIds(List<String> ids) {
                request.setKnowledgeBaseIds(ids);
                return this;
            }

            public AssemblyRequest build() {
                return request;
            }
        }
    }
}

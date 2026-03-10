package net.ooder.scene.llm.context;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * LLM 运行时上下文
 * 
 * <p>组装完整的 LLM 调用所需上下文，包含：</p>
 * <ul>
 *   <li>SystemPrompt - 组装后的系统提示词</li>
 *   <li>Tools - 函数定义列表</li>
 *   <li>Messages - 消息历史</li>
 *   <li>子上下文引用（RoleContext, KnowledgeContext, FunctionContext, MemoryContext）</li>
 * </ul>
 *
 * <h3>使用示例</h3>
 * <pre>
 * // 组装运行时上下文
 * LlmRuntimeContext context = LlmRuntimeContext.assemble(
 *     LlmRuntimeContext.AssemblyRequest.builder()
 *         .skillId("recruitment-skill")
 *         .roleId("hr-assistant")
 *         .sessionId("session-123")
 *         .knowledgeLevel(KnowledgeLoadLevel.ADVANCED)
 *         .build()
 * );
 * 
 * // 获取 LLM 调用参数
 * String systemPrompt = context.getSystemPrompt();
 * List&lt;Map&lt;String, Object&gt;&gt; tools = context.getTools();
 * List&lt;Map&lt;String, Object&gt;&gt; messages = context.getMessages();
 * </pre>
 *
 * @author Ooder Team
 * @version 2.3.1
 * @since 2.3.1
 */
public class LlmRuntimeContext {
    
    private String contextId;
    private String sessionId;
    private String skillId;
    private String sceneId;
    
    // 组装后的 LLM 输入
    private String systemPrompt;              // 组装后的系统提示词
    private List<Map<String, Object>> tools;  // 函数定义
    private List<Map<String, Object>> messages; // 消息历史
    
    // 子上下文引用
    private RoleContext roleContext;
    private KnowledgeContext knowledgeContext;
    private FunctionContext functionContext;
    private MemoryContext memoryContext;
    
    // 扩展属性
    private Map<String, Object> extendedAttributes;
    
    public LlmRuntimeContext() {
        this.contextId = generateContextId();
    }
    
    /**
     * 生成上下文 ID
     */
    private static String generateContextId() {
        return "ctx-" + UUID.randomUUID().toString().substring(0, 8);
    }
    
    /**
     * 从 SkillActivationContext 创建 LlmRuntimeContext
     */
    public static LlmRuntimeContext fromSkillActivationContext(SkillActivationContext activationContext) {
        LlmRuntimeContext context = new LlmRuntimeContext();
        
        context.sessionId = activationContext.getSessionId();
        context.skillId = activationContext.getSkillId();
        context.sceneId = activationContext.getSceneId();
        
        context.roleContext = activationContext.getRoleContext();
        context.knowledgeContext = activationContext.getKnowledgeContext();
        context.functionContext = activationContext.getFunctionContext();
        context.memoryContext = activationContext.getMemoryContext();
        
        context.systemPrompt = activationContext.buildSystemPrompt();
        context.tools = activationContext.getTools();
        context.messages = activationContext.getMessages();
        
        return context;
    }
    
    /**
     * 组装系统提示词
     */
    public static String assembleSystemPrompt(RoleContext role, KnowledgeContext knowledge) {
        StringBuilder prompt = new StringBuilder();
        
        if (role != null) {
            prompt.append("# 角色定义\n\n");
            prompt.append(role.buildPromptSection()).append("\n\n");
        }
        
        if (knowledge != null) {
            prompt.append(knowledge.buildPromptSection());
        }
        
        return prompt.toString().trim();
    }
    
    // Getters and Setters
    
    public String getContextId() {
        return contextId;
    }
    
    public void setContextId(String contextId) {
        this.contextId = contextId;
    }
    
    public String getSessionId() {
        return sessionId;
    }
    
    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
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
    
    public String getSystemPrompt() {
        return systemPrompt;
    }
    
    public void setSystemPrompt(String systemPrompt) {
        this.systemPrompt = systemPrompt;
    }
    
    public List<Map<String, Object>> getTools() {
        return tools;
    }
    
    public void setTools(List<Map<String, Object>> tools) {
        this.tools = tools;
    }
    
    public List<Map<String, Object>> getMessages() {
        return messages;
    }
    
    public void setMessages(List<Map<String, Object>> messages) {
        this.messages = messages;
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
    
    public Map<String, Object> getExtendedAttributes() {
        return extendedAttributes;
    }
    
    public void setExtendedAttributes(Map<String, Object> extendedAttributes) {
        this.extendedAttributes = extendedAttributes;
    }
    
    /**
     * 获取扩展属性
     */
    @SuppressWarnings("unchecked")
    public <T> T getExtendedAttribute(String key) {
        if (extendedAttributes == null) {
            return null;
        }
        return (T) extendedAttributes.get(key);
    }
    
    /**
     * 设置扩展属性
     */
    public void setExtendedAttribute(String key, Object value) {
        if (extendedAttributes == null) {
            extendedAttributes = new java.util.HashMap<>();
        }
        extendedAttributes.put(key, value);
    }
}

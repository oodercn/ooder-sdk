package net.ooder.scene.skill.llm.driver;

import java.util.List;
import java.util.Map;

/**
 * Skill LLM 配置
 * 
 * <p>从 Skill 元数据中提取的 LLM 驱动配置，支持：</p>
 * <ul>
 *   <li>动态系统提示词</li>
 *   <li>动态 Function Calling 定义</li>
 *   <li>模型参数配置</li>
 *   <li>能力到函数的映射</li>
 * </ul>
 *
 * <h3>元数据结构示例</h3>
 * <pre>
 * {
 *   "llmConfig": {
 *     "systemPrompt": "你是招聘场景的智能助手...",
 *     "temperature": 0.7,
 *     "maxTokens": 2000,
 *     "functions": [
 *       {
 *         "name": "scan_resume",
 *         "description": "扫描并解析简历",
 *         "parameters": {
 *           "resumeId": {"type": "string", "description": "简历ID"}
 *         },
 *         "required": ["resumeId"],
 *         "capability": "resume_scan"
 *       }
 *     ]
 *   },
 *   "sceneCapabilities": [
 *     {
 *       "capId": "40",
 *       "name": "简历扫描",
 *       "type": "executor",
 *       "llmFunction": {
 *         "name": "scan_resume",
 *         "description": "扫描并解析简历"
 *       }
 *     }
 *   ]
 * }
 * </pre>
 *
 * @author ooder Team
 * @since 2.3.1
 */
public class SkillLlmConfig {

    private String skillId;
    private String skillName;
    
    private String systemPrompt;
    private Double temperature;
    private Integer maxTokens;
    private String defaultModel;
    private String defaultProvider;
    
    private List<FunctionDefinition> functions;
    private List<CapabilityMapping> capabilityMappings;
    
    private Map<String, Object> extendedConfig;

    public SkillLlmConfig() {
    }

    public SkillLlmConfig(String skillId, String skillName) {
        this.skillId = skillId;
        this.skillName = skillName;
    }

    public String getSkillId() {
        return skillId;
    }

    public void setSkillId(String skillId) {
        this.skillId = skillId;
    }

    public String getSkillName() {
        return skillName;
    }

    public void setSkillName(String skillName) {
        this.skillName = skillName;
    }

    public String getSystemPrompt() {
        return systemPrompt;
    }

    public void setSystemPrompt(String systemPrompt) {
        this.systemPrompt = systemPrompt;
    }

    public Double getTemperature() {
        return temperature;
    }

    public void setTemperature(Double temperature) {
        this.temperature = temperature;
    }

    public Integer getMaxTokens() {
        return maxTokens;
    }

    public void setMaxTokens(Integer maxTokens) {
        this.maxTokens = maxTokens;
    }

    public String getDefaultModel() {
        return defaultModel;
    }

    public void setDefaultModel(String defaultModel) {
        this.defaultModel = defaultModel;
    }

    public String getDefaultProvider() {
        return defaultProvider;
    }

    public void setDefaultProvider(String defaultProvider) {
        this.defaultProvider = defaultProvider;
    }

    public List<FunctionDefinition> getFunctions() {
        return functions;
    }

    public void setFunctions(List<FunctionDefinition> functions) {
        this.functions = functions;
    }

    public List<CapabilityMapping> getCapabilityMappings() {
        return capabilityMappings;
    }

    public void setCapabilityMappings(List<CapabilityMapping> capabilityMappings) {
        this.capabilityMappings = capabilityMappings;
    }

    public Map<String, Object> getExtendedConfig() {
        return extendedConfig;
    }

    public void setExtendedConfig(Map<String, Object> extendedConfig) {
        this.extendedConfig = extendedConfig;
    }

    public boolean hasFunctions() {
        return functions != null && !functions.isEmpty();
    }

    public boolean hasCapabilityMappings() {
        return capabilityMappings != null && !capabilityMappings.isEmpty();
    }

    public FunctionDefinition getFunction(String name) {
        if (functions == null) {
            return null;
        }
        for (FunctionDefinition func : functions) {
            if (func.getName().equals(name)) {
                return func;
            }
        }
        return null;
    }

    public CapabilityMapping getCapabilityMapping(String functionName) {
        if (capabilityMappings == null) {
            return null;
        }
        for (CapabilityMapping mapping : capabilityMappings) {
            if (mapping.getFunctionName().equals(functionName)) {
                return mapping;
            }
        }
        return null;
    }

    /**
     * 函数定义
     */
    public static class FunctionDefinition {
        private String name;
        private String description;
        private Map<String, Object> parameters;
        private List<String> required;
        private String capability;
        private FunctionExecutor executor;

        public FunctionDefinition() {
        }

        public FunctionDefinition(String name, String description) {
            this.name = name;
            this.description = description;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public Map<String, Object> getParameters() {
            return parameters;
        }

        public void setParameters(Map<String, Object> parameters) {
            this.parameters = parameters;
        }

        public List<String> getRequired() {
            return required;
        }

        public void setRequired(List<String> required) {
            this.required = required;
        }

        public String getCapability() {
            return capability;
        }

        public void setCapability(String capability) {
            this.capability = capability;
        }

        public FunctionExecutor getExecutor() {
            return executor;
        }

        public void setExecutor(FunctionExecutor executor) {
            this.executor = executor;
        }
    }

    /**
     * 能力映射
     */
    public static class CapabilityMapping {
        private String functionName;
        private String capabilityId;
        private String capabilityName;
        private Map<String, String> parameterMapping;
        private Map<String, Object> defaultParameters;

        public CapabilityMapping() {
        }

        public CapabilityMapping(String functionName, String capabilityId) {
            this.functionName = functionName;
            this.capabilityId = capabilityId;
        }

        public String getFunctionName() {
            return functionName;
        }

        public void setFunctionName(String functionName) {
            this.functionName = functionName;
        }

        public String getCapabilityId() {
            return capabilityId;
        }

        public void setCapabilityId(String capabilityId) {
            this.capabilityId = capabilityId;
        }

        public String getCapabilityName() {
            return capabilityName;
        }

        public void setCapabilityName(String capabilityName) {
            this.capabilityName = capabilityName;
        }

        public Map<String, String> getParameterMapping() {
            return parameterMapping;
        }

        public void setParameterMapping(Map<String, String> parameterMapping) {
            this.parameterMapping = parameterMapping;
        }

        public Map<String, Object> getDefaultParameters() {
            return defaultParameters;
        }

        public void setDefaultParameters(Map<String, Object> defaultParameters) {
            this.defaultParameters = defaultParameters;
        }
    }

    /**
     * 函数执行器接口
     */
    @FunctionalInterface
    public interface FunctionExecutor {
        Object execute(Map<String, Object> arguments, ExecutionContext context);
    }

    /**
     * 执行上下文
     */
    public static class ExecutionContext {
        private String skillId;
        private String userId;
        private String sessionId;
        private Map<String, Object> context;

        public ExecutionContext(String skillId, String userId) {
            this.skillId = skillId;
            this.userId = userId;
        }

        public String getSkillId() {
            return skillId;
        }

        public String getUserId() {
            return userId;
        }

        public String getSessionId() {
            return sessionId;
        }

        public void setSessionId(String sessionId) {
            this.sessionId = sessionId;
        }

        public Map<String, Object> getContext() {
            return context;
        }

        public void setContext(Map<String, Object> context) {
            this.context = context;
        }

        public static ExecutionContext of(String skillId, String userId) {
            return new ExecutionContext(skillId, userId);
        }
    }
}

package net.ooder.scene.llm.context;

import java.io.Serializable;
import java.util.*;

/**
 * 函数上下文
 * 
 * <p>在 Skill 激活时注入，包含所有可用的函数定义：</p>
 * <ul>
 *   <li>函数定义列表 - LLM 可调用的函数</li>
 *   <li>函数执行器 - 实际执行函数的逻辑</li>
 *   <li>Capability 映射 - 函数到能力的映射</li>
 * </ul>
 *
 * <h3>使用示例</h3>
 * <pre>
 * // 从 Skill 加载函数定义
 * FunctionContext context = FunctionContext.loadFromSkill("recruitment-skill");
 *
 * // 获取 LLM Tools 格式
 * List&lt;Map&lt;String, Object&gt;&gt; tools = context.toTools();
 *
 * // 执行函数
 * Object result = context.execute("scan_resume", args, activationContext);
 * </pre>
 *
 * @author ooder Team
 * @since 2.3.1
 */
public class FunctionContext implements Serializable {

    private static final long serialVersionUID = 1L;

    private String skillId;
    private List<FunctionDefinition> functions = new ArrayList<>();
    private Map<String, FunctionExecutor> executors = new HashMap<>();
    private Map<String, String> capabilityMappings = new HashMap<>();
    private Map<String, Object> metadata = new HashMap<>();

    public FunctionContext() {
    }

    public FunctionContext(String skillId) {
        this.skillId = skillId;
    }

    /**
     * 从 Skill 元数据加载函数定义
     * 
     * 加载来源：
     * 1. skill.json 中的 sceneCapabilities → 自动生成 Function 定义
     * 2. metadata.llmConfig.functions → 显式配置的函数定义
     * 
     * @param skillId Skill ID
     * @param skillPackage Skill 包（包含元数据）
     * @return 函数上下文
     */
    public static FunctionContext loadFromSkill(String skillId, net.ooder.skills.api.SkillPackage skillPackage) {
        FunctionContext context = new FunctionContext(skillId);
        
        if (skillPackage == null) {
            return context;
        }
        
        Map<String, Object> metadata = skillPackage.getMetadata();
        
        // 1. 从 sceneCapabilities 自动生成 Function 定义
        loadFunctionsFromCapabilities(context, metadata);
        
        // 2. 从 llmConfig.functions 加载显式配置的函数
        loadFunctionsFromLlmConfig(context, metadata);
        
        return context;
    }
    
    /**
     * 从 sceneCapabilities 自动生成 Function 定义
     */
    @SuppressWarnings("unchecked")
    private static void loadFunctionsFromCapabilities(FunctionContext context, Map<String, Object> metadata) {
        Object capabilities = metadata.get("sceneCapabilities");
        if (!(capabilities instanceof List)) {
            return;
        }
        
        List<Map<String, Object>> capList = (List<Map<String, Object>>) capabilities;
        for (Map<String, Object> cap : capList) {
            String capId = (String) cap.get("id");
            String capName = (String) cap.get("name");
            String capDescription = (String) cap.get("description");
            
            if (capId == null || capId.isEmpty()) {
                continue;
            }
            
            // 生成函数名称（将 capability ID 转换为函数名）
            String functionName = convertToFunctionName(capId);
            
            // 创建函数定义
            FunctionDefinition funcDef = new FunctionDefinition();
            funcDef.setName(functionName);
            funcDef.setDescription(capDescription != null ? capDescription : "Execute " + capName);
            funcDef.setCapability(capId);
            
            // 解析输入参数
            Object input = cap.get("input");
            if (input instanceof Map) {
                Map<String, ParameterDefinition> params = parseParameters((Map<String, Object>) input);
                funcDef.setParameters(params);
                funcDef.setRequired(new ArrayList<>(params.keySet()));
            }
            
            context.registerFunction(funcDef);
        }
    }
    
    /**
     * 从 llmConfig.functions 加载显式配置的函数
     */
    @SuppressWarnings("unchecked")
    private static void loadFunctionsFromLlmConfig(FunctionContext context, Map<String, Object> metadata) {
        Object llmConfig = metadata.get("llmConfig");
        if (!(llmConfig instanceof Map)) {
            return;
        }
        
        Object functions = ((Map<String, Object>) llmConfig).get("functions");
        if (!(functions instanceof List)) {
            return;
        }
        
        List<Map<String, Object>> funcList = (List<Map<String, Object>>) functions;
        for (Map<String, Object> funcMap : funcList) {
            FunctionDefinition funcDef = FunctionDefinition.fromMap(funcMap);
            context.registerFunction(funcDef);
        }
    }
    
    /**
     * 将 Capability ID 转换为函数名
     */
    private static String convertToFunctionName(String capId) {
        // 将 camelCase 或 dot.notation 转换为 snake_case
        String name = capId.replaceAll("([a-z])([A-Z])", "$1_$2")
                          .replace(".", "_")
                          .toLowerCase();
        
        // 确保以字母开头
        if (Character.isDigit(name.charAt(0))) {
            name = "func_" + name;
        }
        
        return name;
    }
    
    /**
     * 解析参数定义
     */
    @SuppressWarnings("unchecked")
    private static Map<String, ParameterDefinition> parseParameters(Map<String, Object> input) {
        Map<String, ParameterDefinition> params = new HashMap<>();
        
        Object properties = input.get("properties");
        if (properties instanceof Map) {
            Map<String, Object> props = (Map<String, Object>) properties;
            for (Map.Entry<String, Object> entry : props.entrySet()) {
                if (entry.getValue() instanceof Map) {
                    Map<String, Object> propDef = (Map<String, Object>) entry.getValue();
                    ParameterDefinition paramDef = new ParameterDefinition();
                    paramDef.setType((String) propDef.getOrDefault("type", "string"));
                    paramDef.setDescription((String) propDef.get("description"));
                    
                    Object enumVal = propDef.get("enum");
                    if (enumVal instanceof List) {
                        paramDef.setEnumValues((List<String>) enumVal);
                    }
                    
                    params.put(entry.getKey(), paramDef);
                }
            }
        }
        
        return params;
    }

    /**
     * 注册函数定义
     */
    public void registerFunction(FunctionDefinition function) {
        functions.add(function);
        if (function.getCapability() != null) {
            capabilityMappings.put(function.getName(), function.getCapability());
        }
    }

    /**
     * 注册函数执行器
     */
    public void registerExecutor(String functionName, FunctionExecutor executor) {
        executors.put(functionName, executor);
    }

    /**
     * 执行函数
     */
    public Object execute(String functionName, Map<String, Object> args, SkillActivationContext context) {
        FunctionExecutor executor = executors.get(functionName);
        if (executor == null) {
            Map<String, Object> result = new HashMap<>();
            result.put("error", true);
            result.put("message", "Function executor not found: " + functionName);
            return result;
        }

        try {
            return executor.execute(args, context);
        } catch (Exception e) {
            Map<String, Object> result = new HashMap<>();
            result.put("error", true);
            result.put("message", e.getMessage());
            return result;
        }
    }

    /**
     * 转换为 LLM API 可用的 Tools 格式
     */
    public List<Map<String, Object>> toTools() {
        List<Map<String, Object>> tools = new ArrayList<>();

        for (FunctionDefinition def : functions) {
            Map<String, Object> tool = new LinkedHashMap<>();
            tool.put("type", "function");

            Map<String, Object> function = new LinkedHashMap<>();
            function.put("name", def.getName());
            function.put("description", def.getDescription());

            Map<String, Object> parameters = new LinkedHashMap<>();
            parameters.put("type", "object");

            Map<String, Object> properties = new LinkedHashMap<>();
            if (def.getParameters() != null) {
                for (Map.Entry<String, ParameterDefinition> entry : def.getParameters().entrySet()) {
                    properties.put(entry.getKey(), entry.getValue().toMap());
                }
            }
            parameters.put("properties", properties);

            if (def.getRequired() != null && !def.getRequired().isEmpty()) {
                parameters.put("required", def.getRequired());
            }

            function.put("parameters", parameters);
            tool.put("function", function);

            tools.add(tool);
        }

        return tools;
    }

    /**
     * 获取 Capability 映射
     */
    public String getCapabilityMapping(String functionName) {
        return capabilityMappings.get(functionName);
    }

    /**
     * 检查是否有函数定义
     */
    public boolean hasFunctions() {
        return !functions.isEmpty();
    }

    /**
     * 获取函数数量
     */
    public int getFunctionCount() {
        return functions.size();
    }

    /**
     * 获取函数名称列表
     */
    public List<String> getFunctionNames() {
        List<String> names = new ArrayList<>();
        for (FunctionDefinition def : functions) {
            names.add(def.getName());
        }
        return names;
    }

    // Getters and Setters

    public String getSkillId() {
        return skillId;
    }

    public void setSkillId(String skillId) {
        this.skillId = skillId;
    }

    public List<FunctionDefinition> getFunctions() {
        return functions;
    }

    public void setFunctions(List<FunctionDefinition> functions) {
        this.functions = functions != null ? functions : new ArrayList<>();
    }

    public Map<String, FunctionExecutor> getExecutors() {
        return executors;
    }

    public void setExecutors(Map<String, FunctionExecutor> executors) {
        this.executors = executors != null ? executors : new HashMap<>();
    }

    public Map<String, String> getCapabilityMappings() {
        return capabilityMappings;
    }

    public void setCapabilityMappings(Map<String, String> capabilityMappings) {
        this.capabilityMappings = capabilityMappings != null ? capabilityMappings : new HashMap<>();
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata != null ? metadata : new HashMap<>();
    }

    /**
     * 函数定义
     */
    public static class FunctionDefinition implements Serializable {
        private String name;
        private String description;
        private Map<String, ParameterDefinition> parameters;
        private List<String> required;
        private String capability;

        public FunctionDefinition() {
        }

        public FunctionDefinition(String name, String description) {
            this.name = name;
            this.description = description;
        }

        public static FunctionDefinition fromMap(Map<String, Object> map) {
            FunctionDefinition def = new FunctionDefinition();
            def.name = (String) map.get("name");
            def.description = (String) map.get("description");
            def.capability = (String) map.get("capability");

            Object params = map.get("parameters");
            if (params instanceof Map) {
                def.parameters = new HashMap<>();
                @SuppressWarnings("unchecked")
                Map<String, Object> paramsMap = (Map<String, Object>) params;
                for (Map.Entry<String, Object> entry : paramsMap.entrySet()) {
                    if (entry.getValue() instanceof Map) {
                        @SuppressWarnings("unchecked")
                        ParameterDefinition pd = ParameterDefinition.fromMap(
                            (Map<String, Object>) entry.getValue());
                        def.parameters.put(entry.getKey(), pd);
                    }
                }
            }

            Object required = map.get("required");
            if (required instanceof List) {
                @SuppressWarnings("unchecked")
                List<String> requiredList = (List<String>) required;
                def.required = requiredList;
            }

            return def;
        }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public Map<String, ParameterDefinition> getParameters() { return parameters; }
        public void setParameters(Map<String, ParameterDefinition> parameters) { this.parameters = parameters; }
        public List<String> getRequired() { return required; }
        public void setRequired(List<String> required) { this.required = required; }
        public String getCapability() { return capability; }
        public void setCapability(String capability) { this.capability = capability; }
    }

    /**
     * 参数定义
     */
    public static class ParameterDefinition implements Serializable {
        private String type;
        private String description;
        private List<String> enumValues;

        public ParameterDefinition() {
        }

        public ParameterDefinition(String type, String description) {
            this.type = type;
            this.description = description;
        }

        public static ParameterDefinition fromMap(Map<String, Object> map) {
            ParameterDefinition def = new ParameterDefinition();
            def.type = (String) map.get("type");
            def.description = (String) map.get("description");

            Object enumVal = map.get("enum");
            if (enumVal instanceof List) {
                @SuppressWarnings("unchecked")
                List<String> enumList = (List<String>) enumVal;
                def.enumValues = enumList;
            }

            return def;
        }

        public Map<String, Object> toMap() {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("type", type != null ? type : "string");
            if (description != null) {
                map.put("description", description);
            }
            if (enumValues != null && !enumValues.isEmpty()) {
                map.put("enum", enumValues);
            }
            return map;
        }

        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public List<String> getEnumValues() { return enumValues; }
        public void setEnumValues(List<String> enumValues) { this.enumValues = enumValues; }
    }

    /**
     * 函数执行器接口
     */
    @FunctionalInterface
    public interface FunctionExecutor extends Serializable {
        Object execute(Map<String, Object> args, SkillActivationContext context);
    }
}

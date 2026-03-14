package net.ooder.scene.skill.llm.driver;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import net.ooder.scene.skill.llm.FunctionCall;
import net.ooder.scene.skill.llm.LlmProvider;
import net.ooder.scene.skill.llm.StreamHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static net.ooder.scene.skill.llm.driver.SkillLlmConfig.*;

/**
 * Skills 动态 LLM 驱动器
 * 
 * <p>根据 Skill 元数据动态生成 LLM 配置，包括：</p>
 * <ul>
 *   <li>动态系统提示词</li>
 *   <li>动态 Function Calling 定义</li>
 *   <li>能力到函数的映射</li>
 *   <li>模型参数配置</li>
 * </ul>
 *
 * <h3>设计理念</h3>
 * <p>将硬编码的提示词和 Function Calling 转换为从 Skill 元数据动态获取：</p>
 * <pre>
 * 硬编码方式（旧）:
 * private static final String SYSTEM_PROMPT = "你是Ooder场景技能平台的智能助手...";
 * functionRegistry.register("start_scan", "开始扫描发现能力", params, executor);
 *
 * 动态方式（新）:
 * SkillLlmConfig config = driver.loadConfig(skillPackage);
 * String systemPrompt = config.getSystemPrompt();
 * List&lt;FunctionCall&gt; functions = driver.getFunctionCalls(skillId);
 * </pre>
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
public class SkillLlmDriver {

    private static final Logger log = LoggerFactory.getLogger(SkillLlmDriver.class);

    private final Map<String, SkillLlmConfig> configs = new ConcurrentHashMap<>();
    private final Map<String, SkillLlmConfig.FunctionExecutor> functionExecutors = new ConcurrentHashMap<>();
    
    private final LlmProvider defaultProvider;
    private final String defaultModel;

    public SkillLlmDriver(LlmProvider defaultProvider, String defaultModel) {
        this.defaultProvider = defaultProvider;
        this.defaultModel = defaultModel;
    }

    /**
     * 从 SkillPackage 加载 LLM 配置
     */
    @SuppressWarnings("unchecked")
    public SkillLlmConfig loadConfig(Object skillPackage) {
        String skillId = extractSkillId(skillPackage);
        
        if (configs.containsKey(skillId)) {
            return configs.get(skillId);
        }
        
        Map<String, Object> metadata = extractMetadata(skillPackage);
        SkillLlmConfig config = parseConfig(skillId, metadata);
        
        configs.put(skillId, config);
        log.info("Loaded LLM config for skill: {}", skillId);
        
        return config;
    }

    private String extractSkillId(Object skillPackage) {
        try {
            return (String) skillPackage.getClass().getMethod("getSkillId").invoke(skillPackage);
        } catch (Exception e) {
            return skillPackage.toString();
        }
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> extractMetadata(Object skillPackage) {
        try {
            return (Map<String, Object>) skillPackage.getClass().getMethod("getMetadata").invoke(skillPackage);
        } catch (Exception e) {
            return Collections.emptyMap();
        }
    }

    private SkillLlmConfig parseConfig(String skillId, Map<String, Object> metadata) {
        SkillLlmConfig config = new SkillLlmConfig();
        config.setSkillId(skillId);
        
        if (metadata == null) {
            return config;
        }
        
        Object llmConfig = metadata.get("llmConfig");
        if (llmConfig instanceof Map) {
            parseLlmConfig(config, (Map<String, Object>) llmConfig);
        }
        
        List<CapabilityMapping> mappings = extractCapabilityMappings(metadata);
        config.setCapabilityMappings(mappings);
        
        List<FunctionDefinition> functions = extractFunctionsFromMetadata(metadata);
        config.setFunctions(functions);
        
        return config;
    }

    @SuppressWarnings("unchecked")
    private void parseLlmConfig(SkillLlmConfig config, Map<String, Object> llmConfig) {
        String systemPrompt = (String) llmConfig.get("systemPrompt");
        if (systemPrompt != null) {
            config.setSystemPrompt(systemPrompt);
        }
        
        Object temp = llmConfig.get("temperature");
        if (temp instanceof Number) {
            config.setTemperature(((Number) temp).doubleValue());
        }
        
        Object maxTokens = llmConfig.get("maxTokens");
        if (maxTokens instanceof Number) {
            config.setMaxTokens(((Number) maxTokens).intValue());
        }
        
        String model = (String) llmConfig.get("defaultModel");
        if (model != null) {
            config.setDefaultModel(model);
        }
        
        String provider = (String) llmConfig.get("defaultProvider");
        if (provider != null) {
            config.setDefaultProvider(provider);
        }
        
        Object extended = llmConfig.get("extendedConfig");
        if (extended instanceof Map) {
            config.setExtendedConfig((Map<String, Object>) extended);
        }
    }

    @SuppressWarnings("unchecked")
    private List<CapabilityMapping> extractCapabilityMappings(Map<String, Object> metadata) {
        List<CapabilityMapping> mappings = new ArrayList<>();
        
        Object caps = metadata.get("sceneCapabilities");
        if (!(caps instanceof List)) {
            return mappings;
        }
        
        for (Object cap : (List<?>) caps) {
            if (cap instanceof Map) {
                Map<?, ?> capMap = (Map<?, ?>) cap;
                Object llmFunc = capMap.get("llmFunction");
                if (llmFunc instanceof Map) {
                    mappings.add(createCapabilityMapping(capMap, (Map<String, Object>) llmFunc));
                }
            }
        }
        
        return mappings;
    }

    private CapabilityMapping createCapabilityMapping(Map<?, ?> capMap, Map<String, Object> funcMap) {
        CapabilityMapping mapping = new CapabilityMapping();
        mapping.setCapabilityId((String) capMap.get("capId"));
        mapping.setCapabilityName((String) capMap.get("name"));
        
        mapping.setParameterMapping(new HashMap<String, String>());
        mapping.setDefaultParameters(new HashMap<String, Object>());
        
        return mapping;
    }

    private List<FunctionDefinition> extractFunctionsFromMetadata(Map<String, Object> metadata) {
        List<FunctionDefinition> functions = new ArrayList<>();
        
        Object llmConfig = metadata.get("llmConfig");
        if (llmConfig instanceof Map) {
            Map<String, Object> configMap = (Map<String, Object>) llmConfig;
            
            Object funcs = configMap.get("functions");
            if (funcs instanceof List) {
                for (Object func : (List<?>) funcs) {
                    if (func instanceof Map) {
                        FunctionDefinition def = parseFunctionDefinition((Map<String, Object>) func);
                        if (def != null) {
                            functions.add(def);
                        }
                    }
                }
            }
        }
        
        return functions;
    }

    @SuppressWarnings("unchecked")
    private FunctionDefinition parseFunctionDefinition(Map<String, Object> map) {
        String name = (String) map.get("name");
        String description = (String) map.get("description");
        
        if (name == null || name.isEmpty()) {
            return null;
        }
        
        FunctionDefinition def = new FunctionDefinition();
        def.setName(name);
        def.setDescription(description != null ? description : "");
        
        Object params = map.get("parameters");
        if (params instanceof Map) {
            def.setParameters((Map<String, Object>) params);
        } else {
            def.setParameters(new HashMap<String, Object>());
        }
        
        Object required = map.get("required");
        if (required instanceof List) {
            def.setRequired((List<String>) required);
        }
        
        Object capability = map.get("capability");
        if (capability != null) {
            def.setCapability(capability.toString());
        }
        
        return def;
    }

    public void registerFunctionExecutor(String skillId, String functionName, SkillLlmConfig.FunctionExecutor executor) {
        String key = skillId + ":" + functionName;
        functionExecutors.put(key, executor);
        log.info("Registered function executor: {}", key);
    }

    public Object executeFunction(String skillId, String functionName, 
                                   Map<String, Object> arguments, 
                                   ExecutionContext context) {
        String key = skillId + ":" + functionName;
        SkillLlmConfig.FunctionExecutor executor = functionExecutors.get(key);
        
        if (executor == null) {
            log.warn("No executor found for function: {}", key);
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Function executor not found: " + functionName);
            return error;
        }
        
        try {
            return executor.execute(arguments, context);
        } catch (Exception e) {
            log.error("Function execution failed: {}", key, e);
            Map<String, Object> error = new HashMap<>();
            error.put("error", e.getMessage());
            return error;
        }
    }

    public SkillLlmConfig getLlmConfig(String skillId) {
        return configs.get(skillId);
    }

    public List<FunctionDefinition> getFunctions(String skillId) {
        SkillLlmConfig config = configs.get(skillId);
        return config != null ? config.getFunctions() : Collections.emptyList();
    }

    public String getSystemPrompt(String skillId) {
        SkillLlmConfig config = configs.get(skillId);
        return config != null ? config.getSystemPrompt() : null;
    }

    public List<FunctionCall> getFunctionCalls(String skillId) {
        List<FunctionDefinition> functions = getFunctions(skillId);
        List<FunctionCall> calls = new ArrayList<>();
        
        for (FunctionDefinition def : functions) {
            FunctionCall fc = new FunctionCall(def.getName(), def.getDescription(), def.getParameters());
            fc.setRequired(def.getRequired());
            calls.add(fc);
        }
        
        return calls;
    }

    public void clear() {
        configs.clear();
        functionExecutors.clear();
    }
}

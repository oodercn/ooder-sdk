package net.ooder.scene.skill.llm.driver;

import net.ooder.scene.skill.adapter.SkillSDKAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static net.ooder.scene.skill.llm.driver.SkillLlmConfig.*;

/**
 * Skill 函数执行器
 * 
 * <p>将 LLM Function Calling 映射到 Skill Capability 执行，实现：</p>
 * <ul>
 *   <li>函数名到 Capability ID 的映射</li>
 *   <li>参数转换和适配</li>
 *   <li>执行结果转换</li>
 *   <li>错误处理</li>
 * </ul>
 *
 * <h3>工作流程</h3>
 * <pre>
 * LLM Function Call ──▶ SkillFunctionExecutor ──▶ SkillSDKAdapter ──▶ Capability
 *        │                      │                       │                │
 *   {name, args}         参数转换/映射          invokeCapability      执行业务逻辑
 *        │                      │                       │                │
 *        └──────────────────────┴───────────────────────┴────────────────┘
 *                              返回执行结果
 * </pre>
 *
 * @author ooder Team
 * @since 2.3.1
 */
public class SkillFunctionExecutor {

    private static final Logger log = LoggerFactory.getLogger(SkillFunctionExecutor.class);

    private final SkillSDKAdapter skillSDKAdapter;
    private final Map<String, FunctionMapping> mappings = new HashMap<>();

    public SkillFunctionExecutor(SkillSDKAdapter skillSDKAdapter) {
        this.skillSDKAdapter = skillSDKAdapter;
    }

    /**
     * 注册函数到 Capability 的映射
     */
    public void registerMapping(String skillId, FunctionDefinition function) {
        String key = buildKey(skillId, function.getName());
        
        FunctionMapping mapping = new FunctionMapping();
        mapping.setSkillId(skillId);
        mapping.setFunctionName(function.getName());
        mapping.setCapabilityId(function.getCapability());
        
        mappings.put(key, mapping);
        log.info("Registered function mapping: {} -> {}", function.getName(), function.getCapability());
    }

    /**
     * 批量注册映射
     */
    public void registerMappings(String skillId, List<FunctionDefinition> functions) {
        for (FunctionDefinition function : functions) {
            registerMapping(skillId, function);
        }
    }

    /**
     * 执行函数调用
     */
    public Object execute(String skillId, String functionName, 
                          Map<String, Object> arguments,
                          ExecutionContext context) {
        String key = buildKey(skillId, functionName);
        FunctionMapping mapping = mappings.get(key);
        
        if (mapping == null) {
            log.warn("No mapping found for function: {}", key);
            return createErrorResult("Function mapping not found: " + functionName);
        }
        
        try {
            String capabilityId = mapping.getCapabilityId();
            if (capabilityId == null || capabilityId.isEmpty()) {
                capabilityId = functionName;
            }
            
            Map<String, Object> params = transformParameters(mapping, arguments, context);
            
            log.info("Invoking capability: skillId={}, capability={}, params={}", 
                skillId, capabilityId, params);
            
            Object result = skillSDKAdapter.invokeCapability(
                context.getUserId(),
                skillId,
                capabilityId,
                params
            );
            
            return transformResult(result, mapping);
            
        } catch (Exception e) {
            log.error("Function execution failed: {}", key, e);
            return createErrorResult("Execution failed: " + e.getMessage());
        }
    }

    /**
     * 参数转换
     */
    private Map<String, Object> transformParameters(FunctionMapping mapping,
                                                     Map<String, Object> arguments,
                                                     ExecutionContext context) {
        Map<String, Object> params = new HashMap<>();
        
        if (mapping.getParameterMapping() != null) {
            for (Map.Entry<String, String> entry : mapping.getParameterMapping().entrySet()) {
                String argKey = entry.getKey();
                String paramKey = entry.getValue();
                if (arguments.containsKey(argKey)) {
                    params.put(paramKey, arguments.get(argKey));
                }
            }
        } else {
            params.putAll(arguments);
        }
        
        if (mapping.getDefaultParameters() != null) {
            for (Map.Entry<String, Object> entry : mapping.getDefaultParameters().entrySet()) {
                if (!params.containsKey(entry.getKey())) {
                    params.put(entry.getKey(), entry.getValue());
                }
            }
        }
        
        if (context.getContext() != null) {
            params.put("_context", context.getContext());
        }
        
        return params;
    }

    /**
     * 结果转换
     */
    private Object transformResult(Object result, FunctionMapping mapping) {
        if (result == null) {
            return createSuccessResult();
        }
        
        if (result instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<String, Object> resultMap = (Map<String, Object>) result;
            
            if (!resultMap.containsKey("success")) {
                resultMap.put("success", true);
            }
            
            return resultMap;
        }
        
        Map<String, Object> wrappedResult = new HashMap<>();
        wrappedResult.put("success", true);
        wrappedResult.put("data", result);
        return wrappedResult;
    }

    private String buildKey(String skillId, String functionName) {
        return skillId + ":" + functionName;
    }

    private Map<String, Object> createErrorResult(String message) {
        Map<String, Object> result = new HashMap<>();
        result.put("success", false);
        result.put("error", message);
        return result;
    }

    private Map<String, Object> createSuccessResult() {
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        return result;
    }

    /**
     * 函数映射配置
     */
    public static class FunctionMapping {
        private String skillId;
        private String functionName;
        private String capabilityId;
        private Map<String, String> parameterMapping;
        private Map<String, Object> defaultParameters;
        private ResultTransformer resultTransformer;

        public String getSkillId() {
            return skillId;
        }

        public void setSkillId(String skillId) {
            this.skillId = skillId;
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

        public ResultTransformer getResultTransformer() {
            return resultTransformer;
        }

        public void setResultTransformer(ResultTransformer resultTransformer) {
            this.resultTransformer = resultTransformer;
        }
    }

    /**
     * 结果转换器接口
     */
    @FunctionalInterface
    public interface ResultTransformer {
        Object transform(Object result);
    }
}

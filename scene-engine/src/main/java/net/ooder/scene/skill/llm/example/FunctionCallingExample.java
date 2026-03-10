package net.ooder.scene.skill.llm.example;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import net.ooder.scene.skill.llm.FunctionCall;

import java.util.*;

/**
 * Function Calling 标准实现示例
 * 
 * <p>本示例展示如何正确实现 Function Calling 功能，包括：</p>
 * <ul>
 *   <li>函数定义和参数规范</li>
 *   <li>函数执行和结果处理</li>
 *   <li>多轮对话中的函数调用</li>
 *   <li>错误处理和重试机制</li>
 * </ul>
 *
 * <h3>使用方式</h3>
 * <pre>
 * // 1. 创建函数注册表
 * FunctionCallingExample registry = new FunctionCallingExample();
 * 
 * // 2. 注册函数
 * registry.registerFunction("get_weather", "获取天气信息", 
 *     createWeatherParams(), this::executeWeatherFunction);
 * 
 * // 3. 获取函数定义（用于 LLM API 调用）
 * List&lt;Map&lt;String, Object&gt;&gt; tools = registry.getToolsForLLM();
 * 
 * // 4. 处理 LLM 返回的函数调用
 * if (response.containsKey("tool_calls")) {
 *     for (Map&lt;String, Object&gt; toolCall : response.get("tool_calls")) {
 *         Object result = registry.executeFunctionCall(toolCall);
 *     }
 * }
 * </pre>
 *
 * @author ooder Team
 * @since 2.3.1
 */
public class FunctionCallingExample {

    private final Map<String, FunctionDefinition> functions = new HashMap<>();
    private final Map<String, FunctionExecutor> executors = new HashMap<>();

    public interface FunctionExecutor {
        Object execute(Map<String, Object> arguments);
    }

    public static class FunctionDefinition {
        private String name;
        private String description;
        private Map<String, Object> parameters;
        private List<String> required;

        public FunctionDefinition(String name, String description, 
                                   Map<String, Object> parameters, List<String> required) {
            this.name = name;
            this.description = description;
            this.parameters = parameters;
            this.required = required;
        }

        public String getName() { return name; }
        public String getDescription() { return description; }
        public Map<String, Object> getParameters() { return parameters; }
        public List<String> getRequired() { return required; }
    }

    public void registerFunction(String name, String description,
                                  Map<String, Object> parameters,
                                  List<String> required,
                                  FunctionExecutor executor) {
        FunctionDefinition def = new FunctionDefinition(name, description, parameters, required);
        functions.put(name, def);
        executors.put(name, executor);
    }

    public void registerFunction(String name, String description,
                                  Map<String, Object> parameters,
                                  FunctionExecutor executor) {
        registerFunction(name, description, parameters, null, executor);
    }

    public List<Map<String, Object>> getToolsForLLM() {
        List<Map<String, Object>> tools = new ArrayList<>();
        
        for (FunctionDefinition def : functions.values()) {
            Map<String, Object> tool = new LinkedHashMap<>();
            tool.put("type", "function");
            
            Map<String, Object> function = new LinkedHashMap<>();
            function.put("name", def.getName());
            function.put("description", def.getDescription());
            
            Map<String, Object> parameters = new LinkedHashMap<>();
            parameters.put("type", "object");
            
            Map<String, Object> properties = new LinkedHashMap<>();
            for (Map.Entry<String, Object> entry : def.getParameters().entrySet()) {
                properties.put(entry.getKey(), entry.getValue());
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

    public List<FunctionCall> getFunctionCalls() {
        List<FunctionCall> calls = new ArrayList<>();
        
        for (FunctionDefinition def : functions.values()) {
            FunctionCall fc = new FunctionCall(def.getName(), def.getDescription(), def.getParameters());
            fc.setRequired(def.getRequired());
            calls.add(fc);
        }
        
        return calls;
    }

    public Object executeFunctionCall(Map<String, Object> toolCall) {
        Map<String, Object> func = (Map<String, Object>) toolCall.get("function");
        String funcName = (String) func.get("name");
        String argsJson = (String) func.get("arguments");
        
        Map<String, Object> args = parseArguments(argsJson);
        return executeFunction(funcName, args);
    }

    public Object executeFunction(String name, Map<String, Object> arguments) {
        FunctionExecutor executor = executors.get(name);
        if (executor == null) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Function not found: " + name);
            return error;
        }
        
        try {
            return executor.execute(arguments);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", e.getMessage());
            return error;
        }
    }

    private Map<String, Object> parseArguments(String argsJson) {
        Map<String, Object> result = new HashMap<>();
        
        if (argsJson == null || argsJson.isEmpty()) {
            return result;
        }
        
        try {
            JSONObject json = JSON.parseObject(argsJson);
            for (String key : json.keySet()) {
                result.put(key, json.get(key));
            }
        } catch (Exception e) {
            System.err.println("Failed to parse arguments: " + argsJson);
        }
        
        return result;
    }

    public boolean hasFunction(String name) {
        return functions.containsKey(name);
    }

    public Set<String> getFunctionNames() {
        return functions.keySet();
    }

    public static Map<String, Object> createStringParam(String description) {
        Map<String, Object> param = new LinkedHashMap<>();
        param.put("type", "string");
        param.put("description", description);
        return param;
    }

    public static Map<String, Object> createStringParam(String description, List<String> enumValues) {
        Map<String, Object> param = createStringParam(description);
        param.put("enum", enumValues);
        return param;
    }

    public static Map<String, Object> createIntegerParam(String description) {
        Map<String, Object> param = new LinkedHashMap<>();
        param.put("type", "integer");
        param.put("description", description);
        return param;
    }

    public static Map<String, Object> createNumberParam(String description) {
        Map<String, Object> param = new LinkedHashMap<>();
        param.put("type", "number");
        param.put("description", description);
        return param;
    }

    public static Map<String, Object> createBooleanParam(String description) {
        Map<String, Object> param = new LinkedHashMap<>();
        param.put("type", "boolean");
        param.put("description", description);
        return param;
    }

    public static Map<String, Object> createArrayParam(String description, Map<String, Object> items) {
        Map<String, Object> param = new LinkedHashMap<>();
        param.put("type", "array");
        param.put("description", description);
        param.put("items", items);
        return param;
    }

    public static Map<String, Object> createObjectParam(String description, Map<String, Object> properties) {
        Map<String, Object> param = new LinkedHashMap<>();
        param.put("type", "object");
        param.put("description", description);
        param.put("properties", properties);
        return param;
    }

    public static void main(String[] args) {
        FunctionCallingExample registry = new FunctionCallingExample();
        
        Map<String, Object> weatherParams = new LinkedHashMap<>();
        weatherParams.put("city", createStringParam("城市名称"));
        weatherParams.put("unit", createStringParam("温度单位", Arrays.asList("celsius", "fahrenheit")));
        
        registry.registerFunction("get_weather", "获取指定城市的天气信息",
            weatherParams, Arrays.asList("city"),
            arguments -> {
                String city = (String) arguments.get("city");
                String unit = (String) arguments.getOrDefault("unit", "celsius");
                
                Map<String, Object> result = new HashMap<>();
                result.put("city", city);
                result.put("temperature", 25);
                result.put("unit", unit);
                result.put("condition", "晴");
                return result;
            });
        
        Map<String, Object> searchParams = new LinkedHashMap<>();
        searchParams.put("query", createStringParam("搜索关键词"));
        searchParams.put("limit", createIntegerParam("返回结果数量限制"));
        
        registry.registerFunction("search_knowledge", "搜索知识库",
            searchParams, Arrays.asList("query"),
            arguments -> {
                String query = (String) arguments.get("query");
                Integer limit = (Integer) arguments.getOrDefault("limit", 5);
                
                List<Map<String, Object>> results = new ArrayList<>();
                for (int i = 0; i < limit; i++) {
                    Map<String, Object> item = new HashMap<>();
                    item.put("id", "doc-" + i);
                    item.put("title", "文档 " + i);
                    item.put("content", "关于 " + query + " 的内容...");
                    results.add(item);
                }
                
                Map<String, Object> result = new HashMap<>();
                result.put("results", results);
                result.put("total", limit);
                return result;
            });
        
        System.out.println("=== 注册的函数 ===");
        for (String name : registry.getFunctionNames()) {
            System.out.println("- " + name);
        }
        
        System.out.println("\n=== LLM Tools 格式 ===");
        List<Map<String, Object>> tools = registry.getToolsForLLM();
        System.out.println(JSON.toJSONString(tools, true));
        
        System.out.println("\n=== 模拟函数调用 ===");
        Map<String, Object> toolCall = new HashMap<>();
        Map<String, Object> func = new HashMap<>();
        func.put("name", "get_weather");
        func.put("arguments", "{\"city\":\"北京\",\"unit\":\"celsius\"}");
        toolCall.put("function", func);
        
        Object result = registry.executeFunctionCall(toolCall);
        System.out.println(JSON.toJSONString(result, true));
    }
}

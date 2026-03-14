package net.ooder.scene.skill.tool.browser;

import net.ooder.scene.skill.tool.ToolContext;
import net.ooder.scene.skill.tool.ToolResult;
import net.ooder.scene.skill.tool.ext.ToolExtension;
import net.ooder.scene.skill.tool.ext.ToolExtensionConfig;

import java.util.*;
import java.util.concurrent.*;

/**
 * 执行脚本工具
 *
 * <p>允许LLM在浏览器环境中执行JavaScript代码</p>
 *
 * <p>架构层次：应用层 - 浏览器工具</p>
 *
 * @author ooder
 * @since 2.3
 */
public class ExecuteScriptTool implements ToolExtension {

    private static final String ID = "execute_script";
    private static final String NAME = "execute_script";
    private static final String DESCRIPTION = "在浏览器环境中执行JavaScript代码。可用于操作DOM、获取页面数据、与页面交互等。";

    private final ToolExtensionConfig config;
    private final ScriptBridge bridge;
    private final Map<String, AsyncTask> asyncTasks = new ConcurrentHashMap<>();
    private final ExecutorService executorService = Executors.newCachedThreadPool();

    public ExecuteScriptTool(ScriptBridge bridge) {
        this.bridge = bridge;
        // 默认配置：需要用户确认（安全考虑）
        this.config = ToolExtensionConfig.defaults()
                .async(false)
                .requireConfirmation(true)
                .timeout(10000)
                .cancellable(true);
    }

    public ExecuteScriptTool(ScriptBridge bridge, ToolExtensionConfig config) {
        this.bridge = bridge;
        this.config = config != null ? config : ToolExtensionConfig.defaults();
    }

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public String getDescription() {
        return DESCRIPTION;
    }

    @Override
    public ToolExtensionConfig getConfig() {
        return config;
    }

    @Override
    public Map<String, Object> getParameters() {
        return getParametersSchema();
    }

    @Override
    public Map<String, Object> getParametersSchema() {
        Map<String, Object> schema = new LinkedHashMap<>();
        schema.put("type", "object");

        Map<String, Object> properties = new LinkedHashMap<>();

        Map<String, Object> scriptProp = new LinkedHashMap<>();
        scriptProp.put("type", "string");
        scriptProp.put("description", "要执行的JavaScript代码");
        properties.put("script", scriptProp);

        Map<String, Object> asyncProp = new LinkedHashMap<>();
        asyncProp.put("type", "boolean");
        asyncProp.put("description", "是否异步执行脚本");
        asyncProp.put("default", false);
        properties.put("async", asyncProp);

        Map<String, Object> timeoutProp = new LinkedHashMap<>();
        timeoutProp.put("type", "integer");
        timeoutProp.put("description", "执行超时时间（毫秒）");
        timeoutProp.put("default", 10000);
        properties.put("timeout", timeoutProp);

        Map<String, Object> returnTypeProp = new LinkedHashMap<>();
        returnTypeProp.put("type", "string");
        returnTypeProp.put("enum", Arrays.asList("auto", "string", "json", "number", "boolean"));
        returnTypeProp.put("description", "返回值类型");
        returnTypeProp.put("default", "auto");
        properties.put("returnType", returnTypeProp);

        schema.put("properties", properties);
        schema.put("required", Collections.singletonList("script"));

        return schema;
    }

    @Override
    public boolean requireUserConfirmation(Map<String, Object> arguments) {
        // 检查脚本内容是否包含敏感操作
        String script = (String) arguments.get("script");
        if (script != null) {
            String lowerScript = script.toLowerCase();
            // 敏感操作需要确认
            String[] sensitiveOps = {"eval(", "function(", "settimeout", "setinterval", "xmlhttprequest", "fetch(", "websocket"};
            for (String op : sensitiveOps) {
                if (lowerScript.contains(op)) {
                    return true;
                }
            }
        }
        return config.isRequireConfirmation();
    }

    @Override
    public String getConfirmationMessage(Map<String, Object> arguments) {
        String script = (String) arguments.get("script");
        String preview = script != null && script.length() > 50 ? script.substring(0, 50) + "..." : script;
        return String.format("确认要在页面执行以下脚本吗？\n\n%s", preview);
    }

    @Override
    public ToolResult execute(Map<String, Object> arguments, ToolContext context) {
        boolean async = (Boolean) arguments.getOrDefault("async", false);

        // 如果参数指定异步，但配置不允许，则使用配置
        if (async && !config.isAsync()) {
            return ToolResult.failure("ASYNC_NOT_ALLOWED", "Async execution is not allowed for this tool");
        }

        return doExecute(arguments, null);
    }

    @Override
    public String executeAsync(Map<String, Object> arguments, ToolContext context, ToolExecutionCallback callback) {
        String taskId = "script_task_" + UUID.randomUUID().toString().substring(0, 8);

        Future<?> future = executorService.submit(() -> {
            try {
                callback.onProgress(0, "准备执行脚本...");

                ToolResult result = doExecute(arguments, callback);

                if (result.isSuccess()) {
                    callback.onProgress(100, "脚本执行完成");
                    callback.onComplete(result);
                } else {
                    String errorCode = result.getData() != null ? 
                        (String) result.getData().get("code") : "SCRIPT_FAILED";
                    callback.onError(errorCode, result.getMessage());
                }

            } catch (Exception e) {
                callback.onError("EXECUTION_ERROR", e.getMessage());
            } finally {
                asyncTasks.remove(taskId);
            }
        });

        asyncTasks.put(taskId, new AsyncTask(taskId, future, callback));
        return taskId;
    }

    @Override
    public boolean cancelAsync(String taskId) {
        AsyncTask task = asyncTasks.get(taskId);
        if (task != null && task.future != null) {
            boolean cancelled = task.future.cancel(true);
            if (cancelled && task.callback != null) {
                task.callback.onCancelled();
            }
            asyncTasks.remove(taskId);
            return cancelled;
        }
        return false;
    }

    @Override
    public AsyncTaskStatus getAsyncStatus(String taskId) {
        AsyncTask task = asyncTasks.get(taskId);
        if (task == null) {
            return AsyncTaskStatus.UNKNOWN;
        }

        if (task.future.isDone()) {
            return task.future.isCancelled() ? AsyncTaskStatus.CANCELLED : AsyncTaskStatus.COMPLETED;
        } else if (task.future.isCancelled()) {
            return AsyncTaskStatus.CANCELLED;
        }
        return AsyncTaskStatus.RUNNING;
    }

    private ToolResult doExecute(Map<String, Object> arguments, ToolExecutionCallback callback) {
        try {
            if (bridge == null) {
                return ToolResult.failure("BRIDGE_NOT_AVAILABLE", "Script bridge is not available");
            }

            String script = (String) arguments.get("script");
            int timeout = ((Number) arguments.getOrDefault("timeout", config.getTimeout())).intValue();
            String returnType = (String) arguments.getOrDefault("returnType", "auto");

            if (script == null || script.isEmpty()) {
                return ToolResult.failure("INVALID_SCRIPT", "Script is required");
            }

            if (callback != null) {
                callback.onProgress(20, "正在执行脚本...");
            }

            // 执行脚本
            ScriptResult scriptResult = bridge.executeScript(script, timeout);

            if (callback != null) {
                callback.onProgress(80, "处理执行结果...");
            }

            if (scriptResult.isSuccess()) {
                Object result = processResult(scriptResult.getResult(), returnType);

                Map<String, Object> data = new LinkedHashMap<>();
                data.put("result", result);
                data.put("executionTime", scriptResult.getExecutionTime());
                data.put("returnType", returnType);

                return ToolResult.success(data);
            } else {
                return ToolResult.failure("SCRIPT_ERROR", scriptResult.getErrorMessage());
            }

        } catch (Exception e) {
            return ToolResult.failure("EXECUTION_ERROR", e.getMessage());
        }
    }

    private Object processResult(Object rawResult, String returnType) {
        if (rawResult == null) {
            return null;
        }

        switch (returnType) {
            case "string":
                return rawResult.toString();
            case "json":
                if (rawResult instanceof String) {
                    try {
                        return new com.fasterxml.jackson.databind.ObjectMapper().readValue((String) rawResult, Object.class);
                    } catch (Exception e) {
                        return rawResult;
                    }
                }
                return rawResult;
            case "number":
                if (rawResult instanceof Number) {
                    return rawResult;
                }
                try {
                    return Double.parseDouble(rawResult.toString());
                } catch (NumberFormatException e) {
                    return rawResult;
                }
            case "boolean":
                if (rawResult instanceof Boolean) {
                    return rawResult;
                }
                return Boolean.parseBoolean(rawResult.toString());
            case "auto":
            default:
                return rawResult;
        }
    }

    @Override
    public String getCategory() {
        return "browser";
    }

    @Override
    public List<String> getTags() {
        return Arrays.asList("browser", "script", "javascript", "dom");
    }

    /**
     * 脚本执行桥接接口
     */
    public interface ScriptBridge {
        ScriptResult executeScript(String script, int timeout);
    }

    /**
     * 脚本执行结果
     */
    public static class ScriptResult {
        private boolean success;
        private Object result;
        private long executionTime;
        private String errorMessage;

        public static ScriptResult success(Object result, long executionTime) {
            ScriptResult r = new ScriptResult();
            r.success = true;
            r.result = result;
            r.executionTime = executionTime;
            return r;
        }

        public static ScriptResult failure(String errorMessage) {
            ScriptResult r = new ScriptResult();
            r.success = false;
            r.errorMessage = errorMessage;
            return r;
        }

        public boolean isSuccess() { return success; }
        public Object getResult() { return result; }
        public long getExecutionTime() { return executionTime; }
        public String getErrorMessage() { return errorMessage; }
    }

    private static class AsyncTask {
        final String taskId;
        final Future<?> future;
        final ToolExecutionCallback callback;

        AsyncTask(String taskId, Future<?> future, ToolExecutionCallback callback) {
            this.taskId = taskId;
            this.future = future;
            this.callback = callback;
        }
    }
}

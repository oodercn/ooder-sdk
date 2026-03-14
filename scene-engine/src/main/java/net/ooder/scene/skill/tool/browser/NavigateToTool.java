package net.ooder.scene.skill.tool.browser;

import net.ooder.scene.skill.tool.ToolContext;
import net.ooder.scene.skill.tool.ToolResult;
import net.ooder.scene.skill.tool.ext.ToolExtension;
import net.ooder.scene.skill.tool.ext.ToolExtensionConfig;

import java.util.*;
import java.util.concurrent.*;

/**
 * 页面导航工具
 *
 * <p>支持页面跳转，可配置异步执行、用户确认等选项</p>
 *
 * <p>架构层次：应用层 - 浏览器工具</p>
 *
 * @author ooder
 * @since 2.3
 */
public class NavigateToTool implements ToolExtension {

    private static final String ID = "navigate_to";
    private static final String NAME = "navigate_to";
    private static final String DESCRIPTION = "导航到指定URL地址。支持在当前页或新标签页打开，可配置等待页面加载完成。";

    private final ToolExtensionConfig config;
    private final NavigationBridge bridge;
    private final Map<String, AsyncTask> asyncTasks = new ConcurrentHashMap<>();
    private final ExecutorService executorService = Executors.newCachedThreadPool();

    public NavigateToTool(NavigationBridge bridge) {
        this.bridge = bridge;
        // 默认配置：异步执行，需要用户确认
        this.config = ToolExtensionConfig.defaults()
                .async(true)
                .requireConfirmation(true)
                .timeout(30000)
                .cancellable(true);
    }

    public NavigateToTool(NavigationBridge bridge, ToolExtensionConfig config) {
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

        Map<String, Object> urlProp = new LinkedHashMap<>();
        urlProp.put("type", "string");
        urlProp.put("description", "目标URL地址");
        properties.put("url", urlProp);

        Map<String, Object> targetProp = new LinkedHashMap<>();
        targetProp.put("type", "string");
        targetProp.put("enum", Arrays.asList("_self", "_blank"));
        targetProp.put("description", "打开方式：_self当前页，_blank新标签页");
        targetProp.put("default", "_self");
        properties.put("target", targetProp);

        Map<String, Object> waitForLoadProp = new LinkedHashMap<>();
        waitForLoadProp.put("type", "boolean");
        waitForLoadProp.put("description", "是否等待页面加载完成");
        waitForLoadProp.put("default", true);
        properties.put("waitForLoad", waitForLoadProp);

        Map<String, Object> timeoutProp = new LinkedHashMap<>();
        timeoutProp.put("type", "integer");
        timeoutProp.put("description", "加载超时时间（毫秒）");
        timeoutProp.put("default", 30000);
        properties.put("timeout", timeoutProp);

        schema.put("properties", properties);
        schema.put("required", Collections.singletonList("url"));

        return schema;
    }

    @Override
    public boolean requireUserConfirmation(Map<String, Object> arguments) {
        // 外部链接需要确认
        String url = (String) arguments.get("url");
        if (url != null && !url.isEmpty()) {
            // 检查是否是外部链接
            return isExternalUrl(url);
        }
        return config.isRequireConfirmation();
    }

    @Override
    public String getConfirmationMessage(Map<String, Object> arguments) {
        String url = (String) arguments.get("url");
        String target = (String) arguments.getOrDefault("target", "_self");
        String targetDesc = "_blank".equals(target) ? "新标签页" : "当前页";
        return String.format("确认要跳转到 %s 吗？（将在%s打开）", url, targetDesc);
    }

    @Override
    public ToolResult execute(Map<String, Object> arguments, ToolContext context) {
        // 如果是异步配置，返回提示信息
        if (config.isAsync()) {
            return ToolResult.failure("USE_ASYNC", "This tool is configured for async execution. Please use executeAsync.");
        }

        return doNavigate(arguments, null);
    }

    @Override
    public String executeAsync(Map<String, Object> arguments, ToolContext context, ToolExecutionCallback callback) {
        String taskId = "nav_task_" + UUID.randomUUID().toString().substring(0, 8);

        Future<?> future = executorService.submit(() -> {
            try {
                callback.onProgress(0, "开始导航...");

                ToolResult result = doNavigate(arguments, callback);

                if (result.isSuccess()) {
                    callback.onProgress(100, "导航完成");
                    callback.onComplete(result);
                } else {
                    String errorCode = result.getData() != null ? 
                        (String) result.getData().get("code") : "NAVIGATION_FAILED";
                    callback.onError(errorCode, result.getMessage());
                }

            } catch (Exception e) {
                callback.onError("NAVIGATION_ERROR", e.getMessage());
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

    private ToolResult doNavigate(Map<String, Object> arguments, ToolExecutionCallback callback) {
        try {
            if (bridge == null) {
                return ToolResult.failure("BRIDGE_NOT_AVAILABLE", "Navigation bridge is not available");
            }

            String url = (String) arguments.get("url");
            String target = (String) arguments.getOrDefault("target", "_self");
            boolean waitForLoad = (Boolean) arguments.getOrDefault("waitForLoad", true);
            int timeout = ((Number) arguments.getOrDefault("timeout", 30000)).intValue();

            if (url == null || url.isEmpty()) {
                return ToolResult.failure("INVALID_URL", "URL is required");
            }

            if (callback != null) {
                callback.onProgress(10, "正在跳转...");
            }

            // 执行导航
            NavigationResult navResult = bridge.navigateTo(url, target, waitForLoad, timeout);

            if (callback != null) {
                callback.onProgress(80, "页面加载中...");
            }

            Map<String, Object> data = new LinkedHashMap<>();
            data.put("url", url);
            data.put("target", target);
            data.put("success", navResult.isSuccess());
            data.put("loadTime", navResult.getLoadTime());

            if (navResult.isSuccess()) {
                data.put("finalUrl", navResult.getFinalUrl());
                data.put("title", navResult.getTitle());
                return ToolResult.success(data);
            } else {
                return ToolResult.failure("NAVIGATION_FAILED", navResult.getErrorMessage());
            }

        } catch (Exception e) {
            return ToolResult.failure("NAVIGATION_ERROR", e.getMessage());
        }
    }

    private boolean isExternalUrl(String url) {
        if (bridge == null) return false;
        String currentUrl = bridge.getCurrentUrl();
        if (currentUrl == null) return true;

        try {
            java.net.URL current = new java.net.URL(currentUrl);
            java.net.URL target = new java.net.URL(url);
            return !current.getHost().equals(target.getHost());
        } catch (Exception e) {
            return true;
        }
    }

    @Override
    public String getCategory() {
        return "browser";
    }

    @Override
    public List<String> getTags() {
        return Arrays.asList("browser", "navigation", "url");
    }

    /**
     * 导航桥接接口
     */
    public interface NavigationBridge {
        String getCurrentUrl();
        NavigationResult navigateTo(String url, String target, boolean waitForLoad, int timeout);
    }

    /**
     * 导航结果
     */
    public static class NavigationResult {
        private boolean success;
        private String finalUrl;
        private String title;
        private long loadTime;
        private String errorMessage;

        public static NavigationResult success(String finalUrl, String title, long loadTime) {
            NavigationResult r = new NavigationResult();
            r.success = true;
            r.finalUrl = finalUrl;
            r.title = title;
            r.loadTime = loadTime;
            return r;
        }

        public static NavigationResult failure(String errorMessage) {
            NavigationResult r = new NavigationResult();
            r.success = false;
            r.errorMessage = errorMessage;
            return r;
        }

        public boolean isSuccess() { return success; }
        public String getFinalUrl() { return finalUrl; }
        public String getTitle() { return title; }
        public long getLoadTime() { return loadTime; }
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

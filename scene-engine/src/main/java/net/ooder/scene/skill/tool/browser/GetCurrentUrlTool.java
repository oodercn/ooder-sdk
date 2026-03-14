package net.ooder.scene.skill.tool.browser;

import net.ooder.scene.skill.tool.ToolContext;
import net.ooder.scene.skill.tool.ToolResult;
import net.ooder.scene.skill.tool.ext.ToolExtension;
import net.ooder.scene.skill.tool.ext.ToolExtensionConfig;

import java.util.*;

/**
 * 获取当前页面URL工具
 *
 * <p>获取浏览器当前页面的URL信息</p>
 *
 * <p>架构层次：应用层 - 浏览器工具</p>
 *
 * @author ooder
 * @since 2.3
 */
public class GetCurrentUrlTool implements ToolExtension {

    private static final String ID = "get_current_url";
    private static final String NAME = "get_current_url";
    private static final String DESCRIPTION = "获取当前页面的URL地址，包括完整URL、路径、查询参数等信息。";

    private final ToolExtensionConfig config;
    private final BrowserBridge bridge;

    public GetCurrentUrlTool(BrowserBridge bridge) {
        this.bridge = bridge;
        this.config = ToolExtensionConfig.defaults()
                .async(false)
                .requireConfirmation(false);
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
        schema.put("properties", new LinkedHashMap<>());
        schema.put("required", Collections.emptyList());
        return schema;
    }

    @Override
    public ToolResult execute(Map<String, Object> arguments, ToolContext context) {
        try {
            if (bridge == null) {
                return ToolResult.failure("BRIDGE_NOT_AVAILABLE", "Browser bridge is not available");
            }

            String url = bridge.getCurrentUrl();
            String path = bridge.getPath();
            String query = bridge.getQuery();
            String hash = bridge.getHash();

            Map<String, Object> data = new LinkedHashMap<>();
            data.put("url", url);
            data.put("path", path);
            data.put("query", query);
            data.put("hash", hash);
            data.put("protocol", extractProtocol(url));
            data.put("host", extractHost(url));
            data.put("port", extractPort(url));

            // 解析查询参数
            Map<String, String> queryParams = parseQueryString(query);
            data.put("queryParams", queryParams);

            return ToolResult.success(data);

        } catch (Exception e) {
            return ToolResult.failure("EXECUTION_ERROR", "Failed to get current URL: " + e.getMessage());
        }
    }

    @Override
    public String executeAsync(Map<String, Object> arguments, ToolContext context, ToolExecutionCallback callback) {
        // 同步工具，直接执行
        ToolResult result = execute(arguments, context);
        callback.onComplete(result);
        return UUID.randomUUID().toString();
    }

    @Override
    public String getCategory() {
        return "browser";
    }

    @Override
    public List<String> getTags() {
        return Arrays.asList("browser", "url", "navigation");
    }

    private String extractProtocol(String url) {
        if (url == null) return "";
        int idx = url.indexOf(":");
        return idx > 0 ? url.substring(0, idx) : "";
    }

    private String extractHost(String url) {
        if (url == null) return "";
        try {
            java.net.URL u = new java.net.URL(url);
            return u.getHost();
        } catch (Exception e) {
            return "";
        }
    }

    private int extractPort(String url) {
        if (url == null) return -1;
        try {
            java.net.URL u = new java.net.URL(url);
            return u.getPort();
        } catch (Exception e) {
            return -1;
        }
    }

    private Map<String, String> parseQueryString(String query) {
        Map<String, String> params = new LinkedHashMap<>();
        if (query == null || query.isEmpty()) {
            return params;
        }

        String[] pairs = query.split("&");
        for (String pair : pairs) {
            int idx = pair.indexOf("=");
            if (idx > 0) {
                String key = pair.substring(0, idx);
                String value = idx < pair.length() - 1 ? pair.substring(idx + 1) : "";
                try {
                    params.put(key, java.net.URLDecoder.decode(value, java.nio.charset.StandardCharsets.UTF_8.name()));
                } catch (Exception e) {
                    params.put(key, value);
                }
            }
        }
        return params;
    }

    /**
     * 浏览器桥接接口
     */
    public interface BrowserBridge {
        String getCurrentUrl();
        String getPath();
        String getQuery();
        String getHash();
    }
}

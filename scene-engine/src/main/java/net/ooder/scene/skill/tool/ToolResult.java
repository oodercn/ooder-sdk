package net.ooder.scene.skill.tool;

import java.util.Map;

/**
 * 工具执行结果
 *
 * @author Ooder Team
 * @version 2.3.1
 */
public class ToolResult {

    private boolean success;
    private String message;
    private Map<String, Object> data;

    public ToolResult() {}

    public ToolResult(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public ToolResult(boolean success, String message, Map<String, Object> data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    public static ToolResult success(Map<String, Object> data) {
        ToolResult result = new ToolResult();
        result.success = true;
        result.data = data;
        return result;
    }

    public static ToolResult error(String message) {
        ToolResult result = new ToolResult();
        result.success = false;
        result.message = message;
        return result;
    }

    public static ToolResult failure(String code, String message) {
        ToolResult result = new ToolResult();
        result.success = false;
        result.message = message;
        if (result.data == null) {
            result.data = new java.util.HashMap<>();
        }
        result.data.put("code", code);
        result.data.put("error", message);
        return result;
    }

    public String asText() {
        if (data != null && data.containsKey("text")) {
            return (String) data.get("text");
        }
        if (data != null && data.containsKey("content")) {
            return (String) data.get("content");
        }
        return message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }

    /**
     * 获取错误代码
     * @return 错误代码，如果没有则返回 null
     */
    public String getCode() {
        if (data != null && data.containsKey("code")) {
            return (String) data.get("code");
        }
        return success ? "SUCCESS" : "ERROR";
    }
}

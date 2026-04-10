package net.ooder.skill.common.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 统一响应模型
 * 
 * <p>提供标准化的API响应结构，所有Controller应使用此类返回结果。</p>
 *
 * @author ooder
 * @version 3.0.2
 * @param <T> 响应数据类型
 */
public class ResultModel<T> {
    
    // HTTP 状态码
    private int code;
    
    // 响应状态: "success" 或 "error"
    private String status;
    
    // 消息描述
    private String message;
    
    // 响应数据
    private T data;
    
    // 时间戳 (ISO-8601格式)
    private String timestamp;
    
    // 请求ID
    private String requestId;

    // ==================== 状态码常量 ====================
    
    /** HTTP 200 - 成功 */
    public static final int CODE_SUCCESS = 200;
    
    /** HTTP 400 - 请求参数错误 */
    public static final int CODE_BAD_REQUEST = 400;
    
    /** HTTP 401 - 未授权 */
    public static final int CODE_UNAUTHORIZED = 401;
    
    /** HTTP 403 - 禁止访问 */
    public static final int CODE_FORBIDDEN = 403;
    
    /** HTTP 404 - 资源不存在 */
    public static final int CODE_NOT_FOUND = 404;
    
    /** HTTP 405 - 方法不允许 */
    public static final int CODE_METHOD_NOT_ALLOWED = 405;
    
    /** HTTP 409 - 资源冲突 */
    public static final int CODE_CONFLICT = 409;
    
    /** HTTP 500 - 服务器内部错误 */
    public static final int CODE_INTERNAL_SERVER_ERROR = 500;
    
    /** HTTP 503 - 服务不可用 */
    public static final int CODE_SERVICE_UNAVAILABLE = 503;

    // ==================== 静态工厂方法 ====================

    /**
     * 成功响应 (带数据)
     */
    public static <T> ResultModel<T> success(T data) {
        return new ResultModel<>(CODE_SUCCESS, "操作成功", data, true, generateRequestId());
    }

    /**
     * 成功响应 (无数据)
     */
    public static <T> ResultModel<T> success() {
        return new ResultModel<>(CODE_SUCCESS, "操作成功", null, true, generateRequestId());
    }

    /**
     * 成功响应 (自定义消息)
     */
    public static <T> ResultModel<T> success(String message, T data) {
        return new ResultModel<>(CODE_SUCCESS, message, data, true, generateRequestId());
    }

    /**
     * 通用错误响应
     */
    public static <T> ResultModel<T> error(int code, String message) {
        return new ResultModel<>(code, message, null, false, generateRequestId());
    }

    /**
     * 服务器内部错误
     */
    public static <T> ResultModel<T> error(String message) {
        return new ResultModel<>(CODE_INTERNAL_SERVER_ERROR, message, null, false, generateRequestId());
    }

    /**
     * 请求参数错误 (400)
     */
    public static <T> ResultModel<T> badRequest(String message) {
        return new ResultModel<>(CODE_BAD_REQUEST, message, null, false, generateRequestId());
    }

    /**
     * 未授权 (401)
     */
    public static <T> ResultModel<T> unauthorized(String message) {
        return new ResultModel<>(CODE_UNAUTHORIZED, message, null, false, generateRequestId());
    }

    /**
     * 禁止访问 (403)
     */
    public static <T> ResultModel<T> forbidden(String message) {
        return new ResultModel<>(CODE_FORBIDDEN, message, null, false, generateRequestId());
    }

    /**
     * 资源不存在 (404)
     */
    public static <T> ResultModel<T> notFound(String message) {
        return new ResultModel<>(CODE_NOT_FOUND, message, null, false, generateRequestId());
    }

    /**
     * 资源冲突 (409)
     */
    public static <T> ResultModel<T> conflict(String message) {
        return new ResultModel<>(CODE_CONFLICT, message, null, false, generateRequestId());
    }

    /**
     * 服务不可用 (503)
     */
    public static <T> ResultModel<T> serviceUnavailable(String message) {
        return new ResultModel<>(CODE_SERVICE_UNAVAILABLE, message, null, false, generateRequestId());
    }

    // ==================== Getter/Setter ====================
    
    public int getCode() { return code; }
    public void setCode(int code) { this.code = code; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    
    public T getData() { return data; }
    public void setData(T data) { this.data = data; }
    
    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
    
    public String getRequestId() { return requestId; }
    public void setRequestId(String requestId) { this.requestId = requestId; }

    // ==================== 构造函数 ====================

    public ResultModel() {
        this.code = CODE_SUCCESS;
        this.status = "success";
        this.timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        this.requestId = generateRequestId();
    }

    public ResultModel(int code, String message, T data, boolean success, String requestId) {
        this.code = code;
        this.status = success ? "success" : "error";
        this.message = message;
        this.data = data;
        this.requestId = requestId;
        this.timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    private static String generateRequestId() {
        return "REQ_" + System.currentTimeMillis() + "_" + (int)(Math.random() * 10000);
    }
}

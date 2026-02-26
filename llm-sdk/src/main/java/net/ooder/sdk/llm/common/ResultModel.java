package net.ooder.sdk.llm.common;

/**
 * 统一 API 响应模型
 * 
 * 标准化所有 API 的响应格式
 */
public class ResultModel<T> {
    
    private Integer requestStatus;
    private String message;
    private String title;
    private T data;
    private Object ctx;
    private Long timestamp;
    
    public ResultModel() {
        this.timestamp = System.currentTimeMillis();
    }
    
    // 成功响应
    public static <T> ResultModel<T> success(T data) {
        ResultModel<T> result = new ResultModel<>();
        result.setRequestStatus(200);
        result.setMessage("操作成功");
        result.setData(data);
        return result;
    }
    
    public static <T> ResultModel<T> success(String message, T data) {
        ResultModel<T> result = new ResultModel<>();
        result.setRequestStatus(200);
        result.setMessage(message);
        result.setData(data);
        return result;
    }
    
    // 错误响应
    public static <T> ResultModel<T> error(String message) {
        ResultModel<T> result = new ResultModel<>();
        result.setRequestStatus(500);
        result.setMessage(message);
        return result;
    }
    
    public static <T> ResultModel<T> error(int status, String message) {
        ResultModel<T> result = new ResultModel<>();
        result.setRequestStatus(status);
        result.setMessage(message);
        return result;
    }
    
    public static <T> ResultModel<T> error(int status, String message, String title) {
        ResultModel<T> result = new ResultModel<>();
        result.setRequestStatus(status);
        result.setMessage(message);
        result.setTitle(title);
        return result;
    }
    
    // 常用状态码
    public static <T> ResultModel<T> notFound(String message) {
        return error(404, message, "资源不存在");
    }
    
    public static <T> ResultModel<T> badRequest(String message) {
        return error(400, message, "请求参数错误");
    }
    
    public static <T> ResultModel<T> unauthorized(String message) {
        return error(401, message, "未授权");
    }
    
    public static <T> ResultModel<T> forbidden(String message) {
        return error(403, message, "禁止访问");
    }
    
    // Getters and Setters
    public Integer getRequestStatus() {
        return requestStatus;
    }
    
    public void setRequestStatus(Integer requestStatus) {
        this.requestStatus = requestStatus;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public T getData() {
        return data;
    }
    
    public void setData(T data) {
        this.data = data;
    }
    
    public Object getCtx() {
        return ctx;
    }
    
    public void setCtx(Object ctx) {
        this.ctx = ctx;
    }
    
    public Long getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
    
    public boolean isSuccess() {
        return requestStatus != null && requestStatus >= 200 && requestStatus < 300;
    }
}

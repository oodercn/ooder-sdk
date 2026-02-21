package net.ooder.scene.core;

/**
 * 统一返回结果类
 *
 * <p>所有Provider接口方法都应使用此类包装返回结果</p>
 */
public class Result<T> {

    private boolean success;
    private T data;
    private String error;
    private int code;
    private long timestamp;

    public Result() {
        this.timestamp = System.currentTimeMillis();
    }

    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.setSuccess(true);
        result.setData(data);
        result.setCode(200);
        return result;
    }

    public static <T> Result<T> success() {
        return success(null);
    }

    public static <T> Result<T> error(String error) {
        return error(error, 500);
    }

    public static <T> Result<T> error(String error, int code) {
        Result<T> result = new Result<>();
        result.setSuccess(false);
        result.setError(error);
        result.setCode(code);
        return result;
    }

    public static <T> Result<T> notFound(String message) {
        return error(message, 404);
    }

    public static <T> Result<T> badRequest(String message) {
        return error(message, 400);
    }

    public static <T> Result<T> unauthorized(String message) {
        return error(message, 401);
    }

    public static <T> Result<T> forbidden(String message) {
        return error(message, 403);
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}

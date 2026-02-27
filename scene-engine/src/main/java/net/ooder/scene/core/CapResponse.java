package net.ooder.scene.core;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * CAP 能力响应
 * 
 * <p>封装能力调用的响应信息，包含请求ID、能力ID、执行结果和元数据。</p>
 * 
 * <h3>核心属性：</h3>
 * <ul>
 *   <li>requestId - 请求唯一标识，与请求对应</li>
 *   <li>capId - 能力ID，标识被调用的能力</li>
 *   <li>success - 执行是否成功</li>
 *   <li>result - 执行结果（成功时）</li>
 *   <li>errorMessage - 错误信息（失败时）</li>
 *   <li>metadata - 元数据，包含执行时间、版本等信息</li>
 * </ul>
 * 
 * <h3>使用示例：</h3>
 * <pre>
 * // 创建成功响应
 * CapResponse response = CapResponse.success(requestId, capId, result);
 * 
 * // 创建失败响应
 * CapResponse response = CapResponse.failure(requestId, capId, "Error message");
 * 
 * // 添加元数据
 * response.addMetadata("executionTime", 100);
 * response.addMetadata("version", "1.0.0");
 * </pre>
 * 
 * @author Ooder Team
 * @version 2.3
 * @since 0.7.0
 * @see CapRequest
 * @see CapRouter
 */
public class CapResponse {

    /** 请求唯一标识 */
    private final String requestId;

    /** 能力ID */
    private final String capId;

    /** 执行是否成功 */
    private boolean success;

    /** 执行结果 */
    private Object result;

    /** 错误信息 */
    private String errorMessage;

    /** 元数据 */
    private final Map<String, Object> metadata;

    /**
     * 构造器
     * 
     * @param requestId 请求唯一标识
     * @param capId 能力ID
     */
    public CapResponse(String requestId, String capId) {
        this.requestId = requestId;
        this.capId = capId;
        this.metadata = new ConcurrentHashMap<>();
    }

    /**
     * 获取请求ID
     * 
     * @return 请求唯一标识
     */
    public String getRequestId() {
        return requestId;
    }

    /**
     * 获取能力ID
     * 
     * @return 能力ID
     */
    public String getCapId() {
        return capId;
    }

    /**
     * 是否成功
     * 
     * @return 执行是否成功
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     * 设置成功状态
     * 
     * @param success 是否成功
     */
    public void setSuccess(boolean success) {
        this.success = success;
    }

    /**
     * 获取执行结果
     * 
     * @return 执行结果
     */
    public Object getResult() {
        return result;
    }

    /**
     * 设置执行结果
     * 
     * @param result 执行结果
     */
    public void setResult(Object result) {
        this.result = result;
    }

    /**
     * 获取错误信息
     * 
     * @return 错误信息
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * 设置错误信息
     * 
     * @param errorMessage 错误信息
     */
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    /**
     * 添加元数据
     * 
     * @param key 键
     * @param value 值
     */
    public void addMetadata(String key, Object value) {
        metadata.put(key, value);
    }

    /**
     * 获取元数据
     * 
     * @param key 键
     * @return 值
     */
    public Object getMetadata(String key) {
        return metadata.get(key);
    }

    /**
     * 获取所有元数据
     * 
     * @return 元数据映射表
     */
    public Map<String, Object> getMetadata() {
        return new ConcurrentHashMap<>(metadata);
    }

    /**
     * 创建成功响应
     * 
     * <p>便捷方法，用于快速创建成功的响应对象。</p>
     * 
     * @param requestId 请求ID
     * @param capId 能力ID
     * @param result 执行结果
     * @return 成功响应
     */
    public static CapResponse success(String requestId, String capId, Object result) {
        CapResponse response = new CapResponse(requestId, capId);
        response.setSuccess(true);
        response.setResult(result);
        return response;
    }

    /**
     * 创建失败响应
     * 
     * <p>便捷方法，用于快速创建失败的响应对象。</p>
     * 
     * @param requestId 请求ID
     * @param capId 能力ID
     * @param errorMessage 错误信息
     * @return 失败响应
     */
    public static CapResponse failure(String requestId, String capId, String errorMessage) {
        CapResponse response = new CapResponse(requestId, capId);
        response.setSuccess(false);
        response.setErrorMessage(errorMessage);
        return response;
    }

    @Override
    public String toString() {
        return "CapResponse{" +
                "requestId='" + requestId + '\'' +
                ", capId='" + capId + '\'' +
                ", success=" + success +
                ", result=" + result +
                ", errorMessage='" + errorMessage + '\'' +
                ", metadata=" + metadata +
                '}';
    }
}

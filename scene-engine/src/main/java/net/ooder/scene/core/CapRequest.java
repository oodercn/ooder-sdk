package net.ooder.scene.core;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * CAP 能力请求
 * 
 * <p>封装能力调用的请求信息，包含请求ID、能力ID和参数。</p>
 * 
 * <h3>核心属性：</h3>
 * <ul>
 *   <li>requestId - 请求唯一标识，用于追踪和幂等性控制</li>
 *   <li>capId - 能力ID，标识要调用的能力</li>
 *   <li>parameters - 请求参数，键值对形式</li>
 * </ul>
 * 
 * <h3>使用示例：</h3>
 * <pre>
 * // 创建请求
 * CapRequest request = new CapRequest(UUID.randomUUID().toString(), "40");
 * 
 * // 设置参数
 * request.setParameter("message", "Hello World");
 * request.setParameter("target", "user123");
 * 
 * // 获取参数
 * String message = (String) request.getParameter("message");
 * </pre>
 * 
 * @author Ooder Team
 * @version 2.3
 * @since 0.7.0
 * @see CapResponse
 * @see CapRouter
 */
public class CapRequest {

    /** 请求唯一标识 */
    private final String requestId;

    /** 能力ID */
    private final String capId;

    /** 请求参数 */
    private final Map<String, Object> parameters;

    /**
     * 构造器
     * 
     * @param requestId 请求唯一标识
     * @param capId 能力ID
     */
    public CapRequest(String requestId, String capId) {
        this.requestId = requestId;
        this.capId = capId;
        this.parameters = new ConcurrentHashMap<>();
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
     * 设置参数
     * 
     * @param key 参数名
     * @param value 参数值
     */
    public void setParameter(String key, Object value) {
        parameters.put(key, value);
    }

    /**
     * 获取参数
     * 
     * @param key 参数名
     * @return 参数值，不存在返回null
     */
    public Object getParameter(String key) {
        return parameters.get(key);
    }

    /**
     * 获取参数（带默认值）
     * 
     * @param key 参数名
     * @param defaultValue 默认值
     * @return 参数值，不存在返回默认值
     */
    public Object getParameter(String key, Object defaultValue) {
        return parameters.getOrDefault(key, defaultValue);
    }

    /**
     * 获取所有参数
     * 
     * @return 参数映射表
     */
    public Map<String, Object> getParameters() {
        return new ConcurrentHashMap<>(parameters);
    }

    /**
     * 检查是否包含参数
     * 
     * @param key 参数名
     * @return 是否包含
     */
    public boolean hasParameter(String key) {
        return parameters.containsKey(key);
    }

    /**
     * 移除参数
     * 
     * @param key 参数名
     * @return 被移除的参数值
     */
    public Object removeParameter(String key) {
        return parameters.remove(key);
    }

    /**
     * 清空所有参数
     */
    public void clearParameters() {
        parameters.clear();
    }

    @Override
    public String toString() {
        return "CapRequest{" +
                "requestId='" + requestId + '\'' +
                ", capId='" + capId + '\'' +
                ", parameters=" + parameters +
                '}';
    }
}

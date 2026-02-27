package net.ooder.scene.discovery;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 发现配置类
 * 
 * <p>封装发现服务的配置信息，包括发现范围、超时时间、重试次数等。</p>
 * 
 * <h3>配置项：</h3>
 * <ul>
 *   <li>configId - 配置唯一标识</li>
 *   <li>scope - 发现范围（PERSONAL/DEPARTMENT/COMPANY/PUBLIC）</li>
 *   <li>timeout - 超时时间（毫秒）</li>
 *   <li>retryCount - 重试次数</li>
 *   <li>properties - 扩展属性</li>
 * </ul>
 * 
 * <h3>使用示例：</h3>
 * <pre>
 * DiscoveryConfig config = new DiscoveryConfig("my-config");
 * config.setScope(DiscoveryScope.DEPARTMENT);
 * config.setTimeout(10000);
 * config.setProperty("skillCenterUrl", "https://skillcenter.ooder.io");
 * </pre>
 * 
 * @author Ooder Team
 * @version 2.3
 * @since 0.8.0
 * @see DiscoveryProvider
 * @see DiscoveryScope
 */
public class DiscoveryConfig {

    /** 配置唯一标识 */
    private String configId;

    /** 发现范围 */
    private DiscoveryScope scope;

    /** 超时时间（毫秒） */
    private int timeout;

    /** 重试次数 */
    private int retryCount;

    /** 扩展属性 */
    private Map<String, Object> properties;

    /**
     * 构造器
     * 
     * @param configId 配置唯一标识
     */
    public DiscoveryConfig(String configId) {
        this.configId = configId;
        this.scope = DiscoveryScope.PERSONAL;
        this.timeout = 5000;
        this.retryCount = 3;
        this.properties = new ConcurrentHashMap<>();
    }

    /**
     * 获取配置ID
     * 
     * @return 配置唯一标识
     */
    public String getConfigId() {
        return configId;
    }

    /**
     * 获取发现范围
     * 
     * @return 发现范围
     */
    public DiscoveryScope getScope() {
        return scope;
    }

    /**
     * 设置发现范围
     * 
     * @param scope 发现范围
     */
    public void setScope(DiscoveryScope scope) {
        this.scope = scope;
    }

    /**
     * 获取超时时间
     * 
     * @return 超时时间（毫秒）
     */
    public int getTimeout() {
        return timeout;
    }

    /**
     * 设置超时时间
     * 
     * @param timeout 超时时间（毫秒）
     */
    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    /**
     * 获取重试次数
     * 
     * @return 重试次数
     */
    public int getRetryCount() {
        return retryCount;
    }

    /**
     * 设置重试次数
     * 
     * @param retryCount 重试次数
     */
    public void setRetryCount(int retryCount) {
        this.retryCount = retryCount;
    }

    /**
     * 设置扩展属性
     * 
     * @param key 属性名
     * @param value 属性值
     */
    public void setProperty(String key, Object value) {
        properties.put(key, value);
    }

    /**
     * 获取扩展属性
     * 
     * @param key 属性名
     * @return 属性值
     */
    public Object getProperty(String key) {
        return properties.get(key);
    }

    /**
     * 获取所有扩展属性
     * 
     * @return 属性映射表
     */
    public Map<String, Object> getProperties() {
        return properties;
    }
}

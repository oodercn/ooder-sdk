package net.ooder.sdk.api.connection;

/**
 * 能力端点配置
 * 定义能力端点的连接信息
 *
 * @author ooder
 * @since 2.3
 */
public class CapabilityEndpoint {
    
    /** 能力ID */
    private String capabilityId;
    
    /** 接口ID */
    private String interfaceId;
    
    /** 端点地址 */
    private String endpoint;
    
    /** 超时时间(毫秒) */
    private int timeout;
    
    /**
     * 默认构造函数
     */
    public CapabilityEndpoint() {
        this.timeout = 5000; // 默认5秒超时
    }
    
    /**
     * 全参数构造函数
     * @param capabilityId 能力ID
     * @param interfaceId 接口ID
     * @param endpoint 端点地址
     * @param timeout 超时时间
     */
    public CapabilityEndpoint(String capabilityId, String interfaceId, String endpoint, int timeout) {
        this.capabilityId = capabilityId;
        this.interfaceId = interfaceId;
        this.endpoint = endpoint;
        this.timeout = timeout;
    }
    
    public String getCapabilityId() {
        return capabilityId;
    }
    
    public void setCapabilityId(String capabilityId) {
        this.capabilityId = capabilityId;
    }
    
    public String getInterfaceId() {
        return interfaceId;
    }
    
    public void setInterfaceId(String interfaceId) {
        this.interfaceId = interfaceId;
    }
    
    public String getEndpoint() {
        return endpoint;
    }
    
    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }
    
    public int getTimeout() {
        return timeout;
    }
    
    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }
}

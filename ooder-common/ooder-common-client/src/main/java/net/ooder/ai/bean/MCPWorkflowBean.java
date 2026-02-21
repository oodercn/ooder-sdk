/**
 * $RCSfile: MCPWorkflowBean.java,v $
 * $Revision: 1.0 $
 * $Date: 2025/08/25 $
 * <p>
 * Copyright (c) 2025 ooder.net
 * </p>
 * <p>
 * Company: ooder.net
 * </p>
 * <p>
 * License: MIT License
 * </p>
 */
package net.ooder.ai.bean;

import net.ooder.annotation.ProtocolType;

import java.io.Serializable;
import java.util.Map;

/**
 * MCPWorkflow注解对应的Bean类
 */
public class MCPWorkflowBean implements Serializable {
    private static final long serialVersionUID = 1L;

    private String serviceName;
    private String method;
    private ProtocolType protocol;
    private long timeout;
    private Map<String, String> headers;
    private String fallbackMethod;
    private boolean async;

    // 构造函数
    public MCPWorkflowBean() {
        this.protocol = ProtocolType.HTTP;
        this.timeout = 30000; // 默认超时30秒
        this.async = false;
    }

    // Getter和Setter方法
    public String getServiceName() { return serviceName; }
    public void setServiceName(String serviceName) { this.serviceName = serviceName; }

    public String getMethod() { return method; }
    public void setMethod(String method) { this.method = method; }

    public ProtocolType getProtocol() { return protocol; }
    public void setProtocol(ProtocolType protocol) { this.protocol = protocol; }

    public long getTimeout() { return timeout; }
    public void setTimeout(long timeout) { this.timeout = timeout; }

    public Map<String, String> getHeaders() { return headers; }
    public void setHeaders(Map<String, String> headers) { this.headers = headers; }

    public String getFallbackMethod() { return fallbackMethod; }
    public void setFallbackMethod(String fallbackMethod) { this.fallbackMethod = fallbackMethod; }

    public boolean isAsync() { return async; }
    public void setAsync(boolean async) { this.async = async; }
}
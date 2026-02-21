/**
 * $RCSfile: MCPClientAnnotationBean.java,v $
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

import net.ooder.annotation.*;

/**
 * MCP客户端注解对应的Bean类
 */
@AnnotationType(clazz = MCPClientAnnotation.class)
public class MCPClientAnnotationBean implements CustomBean {
    private String id;
    private String serviceId;
    private ProtocolType protocol;
    private LoadBalanceStrategy loadBalanceStrategy;
    private String version;
    private String description;
    private int timeout;
    private boolean loadBalance;


    public ProtocolType getProtocol() {
        return protocol;
    }

    public void setProtocol(ProtocolType protocol) {
        this.protocol = protocol;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public boolean isLoadBalance() {
        return loadBalance;
    }

    public void setLoadBalance(boolean loadBalance) {
        this.loadBalance = loadBalance;
    }

    @Override
    public String toAnnotationStr() {
        StringBuilder sb = new StringBuilder();
        sb.append("@MCPClientAnnotation(");
        sb.append("id=\"").append(id).append("\", ");
        sb.append("serviceId=\"").append(serviceId).append("\", ");
        sb.append("protocol=ProtocolType.").append(protocol).append(", ");
        sb.append("loadBalanceStrategy=LoadBalanceStrategy.").append(loadBalanceStrategy).append(", ");
        sb.append("version=\"").append(version).append("\", ");
        sb.append("description=\"").append(description).append("\", ");
        sb.append("timeout=").append(timeout).append(", ");
        sb.append("loadBalance=").append(loadBalance);
        sb.append(")");
        return sb.toString();
    }

    // Getters and setters
    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }
}
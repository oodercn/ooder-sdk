package net.ooder.sdk.a2a.capability;

import java.util.Map;

/**
 * 能力端点
 *
 * @author Agent-SDK Team
 * @version 3.0
 * @since 3.0
 */
public class CapabilityEndpoint {
    private String id;
    private String name;
    private String endpoint;
    private String method;
    private Map<String, Object> params;
    
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getEndpoint() { return endpoint; }
    public void setEndpoint(String endpoint) { this.endpoint = endpoint; }
    
    public String getMethod() { return method; }
    public void setMethod(String method) { this.method = method; }
    
    public Map<String, Object> getParams() { return params; }
    public void setParams(Map<String, Object> params) { this.params = params; }
}

package net.ooder.scene.llm.command;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * A2A 命令体
 *
 * @author Ooder Team
 * @since 2.4.0
 */
public class CommandBody implements Serializable {

    private static final long serialVersionUID = 1L;

    private AgentInfo source;
    private AgentInfo target;
    private String targetEndpoint;
    private Map<String, Object> params;
    private Object payload;

    public CommandBody() {
        this.params = new HashMap<>();
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    public Object getParam(String key) {
        return params != null ? params.get(key) : null;
    }
    
    public void setParam(String key, Object value) {
        if (params == null) {
            params = new HashMap<>();
        }
        params.put(key, value);
    }
    
    public AgentInfo getSource() { return source; }
    public void setSource(AgentInfo source) { this.source = source; }
    
    public AgentInfo getTarget() { return target; }
    public void setTarget(AgentInfo target) { this.target = target; }
    
    public String getTargetEndpoint() { return targetEndpoint; }
    public void setTargetEndpoint(String targetEndpoint) { this.targetEndpoint = targetEndpoint; }
    
    public Map<String, Object> getParams() { return params; }
    public void setParams(Map<String, Object> params) { this.params = params; }
    
    public Object getPayload() { return payload; }
    public void setPayload(Object payload) { this.payload = payload; }
    
    public static class Builder {
        private CommandBody body = new CommandBody();
        
        public Builder source(AgentInfo source) {
            body.setSource(source);
            return this;
        }
        
        public Builder target(AgentInfo target) {
            body.setTarget(target);
            return this;
        }
        
        public Builder targetEndpoint(String endpoint) {
            body.setTargetEndpoint(endpoint);
            return this;
        }
        
        public Builder param(String key, Object value) {
            body.setParam(key, value);
            return this;
        }
        
        public Builder params(Map<String, Object> params) {
            body.setParams(params);
            return this;
        }
        
        public Builder payload(Object payload) {
            body.setPayload(payload);
            return this;
        }
        
        public CommandBody build() {
            return body;
        }
    }
}

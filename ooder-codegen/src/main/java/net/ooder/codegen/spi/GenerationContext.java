package net.ooder.codegen.spi;

import java.util.HashMap;
import java.util.Map;

/**
 * 代码生成上下文
 */
public class GenerationContext {
    
    private String type;
    private String packageName;
    private String className;
    private Map<String, Object> parameters = new HashMap<>();
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public String getPackageName() {
        return packageName;
    }
    
    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }
    
    public String getClassName() {
        return className;
    }
    
    public void setClassName(String className) {
        this.className = className;
    }
    
    public Map<String, Object> getParameters() {
        return parameters;
    }
    
    public void setParameters(Map<String, Object> parameters) {
        this.parameters = parameters;
    }
    
    public void addParameter(String key, Object value) {
        this.parameters.put(key, value);
    }
    
    public Object getParameter(String key) {
        return this.parameters.get(key);
    }
}

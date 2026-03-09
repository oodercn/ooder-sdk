package net.ooder.scene.skill.tool;

import java.util.Map;

/**
 * 工具调用
 *
 * @author ooder
 * @since 2.3
 */
public class ToolCall {
    
    private String id;
    private String name;
    private Map<String, Object> arguments;
    
    public ToolCall() {
    }
    
    public ToolCall(String id, String name, Map<String, Object> arguments) {
        this.id = id;
        this.name = name;
        this.arguments = arguments;
    }
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public Map<String, Object> getArguments() {
        return arguments;
    }
    
    public void setArguments(Map<String, Object> arguments) {
        this.arguments = arguments;
    }
    
    public Object getArgument(String key) {
        return arguments != null ? arguments.get(key) : null;
    }
    
    public String getArgumentAsString(String key) {
        Object value = getArgument(key);
        return value != null ? value.toString() : null;
    }
    
    public Integer getArgumentAsInt(String key) {
        Object value = getArgument(key);
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        return null;
    }
}

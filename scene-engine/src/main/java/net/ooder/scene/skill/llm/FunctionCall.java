package net.ooder.scene.skill.llm;

import java.util.List;
import java.util.Map;

/**
 * 函数调用定义
 * 用于 LLM 函数调用能力
 *
 * @author ooder
 * @since 2.3
 */
public class FunctionCall {
    
    /** 函数名称 */
    private String name;
    
    /** 函数描述 */
    private String description;
    
    /** 参数定义 */
    private Map<String, Object> parameters;
    
    /** 必需参数列表 */
    private List<String> required;
    
    public FunctionCall() {}
    
    public FunctionCall(String name, String description, Map<String, Object> parameters) {
        this.name = name;
        this.description = description;
        this.parameters = parameters;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public Map<String, Object> getParameters() {
        return parameters;
    }
    
    public void setParameters(Map<String, Object> parameters) {
        this.parameters = parameters;
    }
    
    public List<String> getRequired() {
        return required;
    }
    
    public void setRequired(List<String> required) {
        this.required = required;
    }
}

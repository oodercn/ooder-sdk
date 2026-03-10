package net.ooder.scene.skill.llm;

import java.util.List;
import java.util.Map;

/**
 * 函数调用
 *
 * @author Ooder Team
 * @version 2.3.1
 */
public class FunctionCall {

    private String id;
    private String name;
    private String description;
    private Map<String, Object> parameters;
    private Map<String, Object> arguments;
    private List<String> required;

    public FunctionCall() {}

    public FunctionCall(String id, String name, Map<String, Object> arguments) {
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

    public Map<String, Object> getArguments() {
        return arguments;
    }

    public void setArguments(Map<String, Object> arguments) {
        this.arguments = arguments;
    }

    public List<String> getRequired() {
        return required;
    }

    public void setRequired(List<String> required) {
        this.required = required;
    }
}

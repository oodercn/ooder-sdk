package net.ooder.scene.llm;

import java.util.Map;

/**
 * Function 调用
 *
 * @author ooder
 * @since 2.4
 */
public class FunctionCall {

    private String id;
    private String name;
    private Map<String, Object> arguments;

    public FunctionCall() {}

    public FunctionCall(String name, Map<String, Object> arguments) {
        this.name = name;
        this.arguments = arguments;
    }

    public static FunctionCall of(String name, Map<String, Object> arguments) {
        return new FunctionCall(name, arguments);
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Map<String, Object> getArguments() { return arguments; }
    public void setArguments(Map<String, Object> arguments) { this.arguments = arguments; }
}

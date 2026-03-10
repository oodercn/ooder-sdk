package net.ooder.scene.llm;

import java.util.Map;

/**
 * 消息
 *
 * @author ooder
 * @since 2.4
 */
public class Message {

    private String role;
    private String content;
    private String name;
    private Map<String, Object> functionCall;

    public Message() {}

    public Message(String role, String content) {
        this.role = role;
        this.content = content;
    }

    public static Message system(String content) {
        return new Message("system", content);
    }

    public static Message user(String content) {
        return new Message("user", content);
    }

    public static Message assistant(String content) {
        return new Message("assistant", content);
    }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Map<String, Object> getFunctionCall() { return functionCall; }
    public void setFunctionCall(Map<String, Object> functionCall) { this.functionCall = functionCall; }
}

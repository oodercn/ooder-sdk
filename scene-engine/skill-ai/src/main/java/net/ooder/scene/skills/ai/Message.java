package net.ooder.scene.skills.ai;

/**
 * 对话消息
 */
public class Message {

    public enum Role {
        SYSTEM, USER, ASSISTANT, TOOL
    }

    private Role role;
    private String content;
    private String toolCallId;
    private String name;

    public Message() {}

    public Message(Role role, String content) {
        this.role = role;
        this.content = content;
    }

    public static Message system(String content) {
        return new Message(Role.SYSTEM, content);
    }

    public static Message user(String content) {
        return new Message(Role.USER, content);
    }

    public static Message assistant(String content) {
        return new Message(Role.ASSISTANT, content);
    }

    public static Message tool(String content, String toolCallId) {
        Message msg = new Message(Role.TOOL, content);
        msg.toolCallId = toolCallId;
        return msg;
    }

    // Getters and Setters
    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getToolCallId() { return toolCallId; }
    public void setToolCallId(String toolCallId) { this.toolCallId = toolCallId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}

package net.ooder.scene.llm;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 聊天请求
 *
 * @author ooder
 * @since 2.4
 */
public class ChatRequest {

    private String provider;
    private String model;
    private List<Message> messages = new ArrayList<>();
    private Map<String, Object> parameters = new LinkedHashMap<>();
    private List<FunctionCall> functions = new ArrayList<>();
    private boolean stream;

    public ChatRequest() {}

    public ChatRequest(String message) {
        this.messages.add(new Message("user", message));
    }

    public ChatRequest(List<Message> messages) {
        this.messages = messages;
    }

    public static ChatRequest of(String message) {
        return new ChatRequest(message);
    }

    public ChatRequest provider(String provider) {
        this.provider = provider;
        return this;
    }

    public ChatRequest model(String model) {
        this.model = model;
        return this;
    }

    public ChatRequest addMessage(String role, String content) {
        this.messages.add(new Message(role, content));
        return this;
    }

    public ChatRequest system(String content) {
        this.messages.add(0, new Message("system", content));
        return this;
    }

    public ChatRequest user(String content) {
        this.messages.add(new Message("user", content));
        return this;
    }

    public ChatRequest assistant(String content) {
        this.messages.add(new Message("assistant", content));
        return this;
    }

    public ChatRequest temperature(double temperature) {
        this.parameters.put("temperature", temperature);
        return this;
    }

    public ChatRequest maxTokens(int maxTokens) {
        this.parameters.put("max_tokens", maxTokens);
        return this;
    }

    public ChatRequest stream(boolean stream) {
        this.stream = stream;
        return this;
    }

    public ChatRequest addFunction(FunctionCall function) {
        this.functions.add(function);
        return this;
    }

    public String getProvider() { return provider; }
    public void setProvider(String provider) { this.provider = provider; }
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
    public List<Message> getMessages() { return messages; }
    public void setMessages(List<Message> messages) { this.messages = messages; }
    public Map<String, Object> getParameters() { return parameters; }
    public void setParameters(Map<String, Object> parameters) { this.parameters = parameters; }
    public List<FunctionCall> getFunctions() { return functions; }
    public void setFunctions(List<FunctionCall> functions) { this.functions = functions; }
    public boolean isStream() { return stream; }
    public void setStream(boolean stream) { this.stream = stream; }

    public boolean hasFunctions() {
        return functions != null && !functions.isEmpty();
    }

    /**
     * 消息
     */
    public static class Message {
        private String role;
        private String content;
        private String name;
        private FunctionCall functionCall;

        public Message() {}

        public Message(String role, String content) {
            this.role = role;
            this.content = content;
        }

        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public FunctionCall getFunctionCall() { return functionCall; }
        public void setFunctionCall(FunctionCall functionCall) { this.functionCall = functionCall; }
    }

    /**
     * Function 调用
     */
    public static class FunctionCall {
        private String id;
        private String name;
        private String arguments;

        public FunctionCall() {}

        public FunctionCall(String name, String arguments) {
            this.name = name;
            this.arguments = arguments;
        }

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getArguments() { return arguments; }
        public void setArguments(String arguments) { this.arguments = arguments; }
    }
}

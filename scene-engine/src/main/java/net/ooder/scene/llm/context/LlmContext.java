package net.ooder.scene.llm.context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * LLM 上下文
 *
 * @author ooder Team
 * @since 2.3.1
 */
public class LlmContext {

    private String contextId;
    private ContextLevel level;
    private String systemPrompt;
    private List<Map<String, Object>> tools;
    private List<Map<String, Object>> messages;
    private Map<String, Object> variables;
    private long createTime;
    private long updateTime;

    public LlmContext() {
        this.tools = new ArrayList<>();
        this.messages = new ArrayList<>();
        this.variables = new HashMap<>();
        this.createTime = System.currentTimeMillis();
        this.updateTime = this.createTime;
    }

    public static LlmContext create(ContextLevel level) {
        LlmContext context = new LlmContext();
        context.setLevel(level);
        context.setContextId(generateContextId(level));
        return context;
    }

    private static String generateContextId(ContextLevel level) {
        return level.getCode() + "-" + System.currentTimeMillis();
    }

    public String getContextId() {
        return contextId;
    }

    public void setContextId(String contextId) {
        this.contextId = contextId;
    }

    public ContextLevel getLevel() {
        return level;
    }

    public void setLevel(ContextLevel level) {
        this.level = level;
    }

    public String getSystemPrompt() {
        return systemPrompt;
    }

    public void setSystemPrompt(String systemPrompt) {
        this.systemPrompt = systemPrompt;
    }

    public List<Map<String, Object>> getTools() {
        return tools;
    }

    public void setTools(List<Map<String, Object>> tools) {
        this.tools = tools != null ? tools : new ArrayList<>();
    }

    public void addTool(Map<String, Object> tool) {
        this.tools.add(tool);
    }

    public List<Map<String, Object>> getMessages() {
        return messages;
    }

    public void setMessages(List<Map<String, Object>> messages) {
        this.messages = messages != null ? messages : new ArrayList<>();
    }

    public void addMessage(Map<String, Object> message) {
        this.messages.add(message);
        this.updateTime = System.currentTimeMillis();
    }

    public Map<String, Object> getVariables() {
        return variables;
    }

    public void setVariables(Map<String, Object> variables) {
        this.variables = variables != null ? variables : new HashMap<>();
    }

    public void setVariable(String key, Object value) {
        this.variables.put(key, value);
    }

    public Object getVariable(String key) {
        return this.variables.get(key);
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }
}

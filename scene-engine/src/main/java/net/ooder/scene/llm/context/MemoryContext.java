package net.ooder.scene.llm.context;

import java.io.Serializable;
import java.util.*;

/**
 * 记忆上下文
 * 
 * <p>管理对话历史和会话记忆</p>
 *
 * @author ooder Team
 * @since 2.3.1
 */
public class MemoryContext implements Serializable {

    private static final long serialVersionUID = 1L;

    private String sessionId;
    private List<Map<String, Object>> history = new ArrayList<>();
    private int maxHistoryLength = 50;
    private Map<String, Object> summary = new HashMap<>();

    public MemoryContext() {
    }

    public MemoryContext(String sessionId) {
        this.sessionId = sessionId;
    }

    /**
     * 添加消息
     */
    public void addMessage(String role, String content) {
        Map<String, Object> message = new LinkedHashMap<>();
        message.put("role", role);
        message.put("content", content);
        message.put("timestamp", System.currentTimeMillis());
        history.add(message);

        while (history.size() > maxHistoryLength) {
            history.remove(0);
        }
    }

    /**
     * 添加带工具调用的消息
     */
    public void addAssistantMessageWithTools(String content, List<Map<String, Object>> toolCalls) {
        Map<String, Object> message = new LinkedHashMap<>();
        message.put("role", "assistant");
        if (content != null && !content.isEmpty()) {
            message.put("content", content);
        }
        message.put("tool_calls", toolCalls);
        message.put("timestamp", System.currentTimeMillis());
        history.add(message);

        while (history.size() > maxHistoryLength) {
            history.remove(0);
        }
    }

    /**
     * 添加工具结果消息
     */
    public void addToolResultMessage(String toolCallId, String content) {
        Map<String, Object> message = new LinkedHashMap<>();
        message.put("role", "tool");
        message.put("tool_call_id", toolCallId);
        message.put("content", content);
        message.put("timestamp", System.currentTimeMillis());
        history.add(message);

        while (history.size() > maxHistoryLength) {
            history.remove(0);
        }
    }

    /**
     * 获取历史记录
     */
    public List<Map<String, Object>> getHistory() {
        return new ArrayList<>(history);
    }

    /**
     * 获取最近 N 条消息
     */
    public List<Map<String, Object>> getRecentHistory(int n) {
        if (n >= history.size()) {
            return new ArrayList<>(history);
        }
        return new ArrayList<>(history.subList(history.size() - n, history.size()));
    }

    /**
     * 清除历史
     */
    public void clearHistory() {
        history.clear();
    }

    /**
     * 获取消息数量
     */
    public int getMessageCount() {
        return history.size();
    }

    /**
     * 获取最后一条用户消息
     */
    public String getLastUserMessage() {
        for (int i = history.size() - 1; i >= 0; i--) {
            Map<String, Object> msg = history.get(i);
            if ("user".equals(msg.get("role"))) {
                return (String) msg.get("content");
            }
        }
        return null;
    }

    /**
     * 获取最后一条助手消息
     */
    public String getLastAssistantMessage() {
        for (int i = history.size() - 1; i >= 0; i--) {
            Map<String, Object> msg = history.get(i);
            if ("assistant".equals(msg.get("role"))) {
                return (String) msg.get("content");
            }
        }
        return null;
    }

    /**
     * 构建对话摘要
     */
    public String buildSummary() {
        if (history.isEmpty()) {
            return "暂无对话历史";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("对话历史摘要 (共 ").append(history.size()).append(" 条消息):\n");

        int start = Math.max(0, history.size() - 5);
        for (int i = start; i < history.size(); i++) {
            Map<String, Object> msg = history.get(i);
            String role = (String) msg.get("role");
            String content = (String) msg.get("content");
            if (content != null && content.length() > 50) {
                content = content.substring(0, 50) + "...";
            }
            sb.append("- ").append(role).append(": ").append(content).append("\n");
        }

        return sb.toString();
    }

    // Getters and Setters

    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }
    public List<Map<String, Object>> getHistoryList() { return history; }
    public void setHistory(List<Map<String, Object>> history) { this.history = history != null ? history : new ArrayList<>(); }
    public int getMaxHistoryLength() { return maxHistoryLength; }
    public void setMaxHistoryLength(int maxHistoryLength) { this.maxHistoryLength = maxHistoryLength; }
    public Map<String, Object> getSummary() { return summary; }
    public void setSummary(Map<String, Object> summary) { this.summary = summary != null ? summary : new HashMap<>(); }
}

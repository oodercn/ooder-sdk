package net.ooder.skill.common.controller;

import net.ooder.skill.common.model.ResultModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api/v1/chat")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class ChatSessionController {

    private static final Logger log = LoggerFactory.getLogger(ChatSessionController.class);
    
    private final Map<String, ChatSession> sessions = new ConcurrentHashMap<>();
    private final Map<String, List<ChatMessage>> messages = new ConcurrentHashMap<>();

    @PostMapping("/sessions")
    public ResultModel<ChatSession> createSession(@RequestBody Map<String, Object> request) {
        String userId = (String) request.getOrDefault("userId", "default-user");
        String title = (String) request.getOrDefault("title", "新对话");
        
        String sessionId = "session-" + System.currentTimeMillis() + "-" + UUID.randomUUID().toString().substring(0, 8);
        
        ChatSession session = new ChatSession();
        session.setSessionId(sessionId);
        session.setUserId(userId);
        session.setTitle(title);
        session.setCreateTime(System.currentTimeMillis());
        session.setUpdateTime(System.currentTimeMillis());
        session.setMessageCount(0);
        
        sessions.put(sessionId, session);
        messages.put(sessionId, new ArrayList<>());
        
        log.info("Created chat session: {} for user: {}", sessionId, userId);
        return ResultModel.success(session);
    }

    @GetMapping("/sessions/{sessionId}")
    public ResultModel<ChatSession> getSession(@PathVariable String sessionId) {
        ChatSession session = sessions.get(sessionId);
        if (session == null) {
            return ResultModel.notFound("Session not found: " + sessionId);
        }
        return ResultModel.success(session);
    }

    @GetMapping("/sessions")
    public ResultModel<List<ChatSession>> listSessions(
            @RequestParam(required = false) String userId,
            @RequestParam(required = false, defaultValue = "20") int limit) {
        
        List<ChatSession> result = new ArrayList<>();
        for (ChatSession session : sessions.values()) {
            if (userId == null || userId.isEmpty() || userId.equals(session.getUserId())) {
                result.add(session);
            }
        }
        
        result.sort((a, b) -> Long.compare(b.getUpdateTime(), a.getUpdateTime()));
        
        if (result.size() > limit) {
            result = result.subList(0, limit);
        }
        
        return ResultModel.success(result);
    }

    @DeleteMapping("/sessions/{sessionId}")
    public ResultModel<Boolean> deleteSession(@PathVariable String sessionId) {
        sessions.remove(sessionId);
        messages.remove(sessionId);
        log.info("Deleted chat session: {}", sessionId);
        return ResultModel.success(true);
    }

    @GetMapping("/sessions/{sessionId}/messages")
    public ResultModel<List<ChatMessage>> getMessages(@PathVariable String sessionId) {
        List<ChatMessage> msgs = messages.get(sessionId);
        if (msgs == null) {
            return ResultModel.notFound("Session not found: " + sessionId);
        }
        return ResultModel.success(msgs);
    }

    @PostMapping("/sessions/{sessionId}/messages")
    public ResultModel<ChatMessage> addMessage(
            @PathVariable String sessionId,
            @RequestBody Map<String, Object> request) {
        
        ChatSession session = sessions.get(sessionId);
        if (session == null) {
            return ResultModel.notFound("Session not found: " + sessionId);
        }
        
        String content = (String) request.get("content");
        String role = (String) request.getOrDefault("role", "user");
        
        ChatMessage message = new ChatMessage();
        message.setMessageId("msg-" + System.currentTimeMillis());
        message.setSessionId(sessionId);
        message.setRole(role);
        message.setContent(content);
        message.setCreateTime(System.currentTimeMillis());
        
        List<ChatMessage> msgs = messages.get(sessionId);
        msgs.add(message);
        
        session.setMessageCount(msgs.size());
        session.setUpdateTime(System.currentTimeMillis());
        
        log.info("Added message to session: {}, role: {}", sessionId, role);
        return ResultModel.success(message);
    }

    public static class ChatSession {
        private String sessionId;
        private String userId;
        private String title;
        private long createTime;
        private long updateTime;
        private int messageCount;

        public String getSessionId() { return sessionId; }
        public void setSessionId(String sessionId) { this.sessionId = sessionId; }
        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public long getCreateTime() { return createTime; }
        public void setCreateTime(long createTime) { this.createTime = createTime; }
        public long getUpdateTime() { return updateTime; }
        public void setUpdateTime(long updateTime) { this.updateTime = updateTime; }
        public int getMessageCount() { return messageCount; }
        public void setMessageCount(int messageCount) { this.messageCount = messageCount; }
    }

    public static class ChatMessage {
        private String messageId;
        private String sessionId;
        private String role;
        private String content;
        private long createTime;

        public String getMessageId() { return messageId; }
        public void setMessageId(String messageId) { this.messageId = messageId; }
        public String getSessionId() { return sessionId; }
        public void setSessionId(String sessionId) { this.sessionId = sessionId; }
        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
        public long getCreateTime() { return createTime; }
        public void setCreateTime(long createTime) { this.createTime = createTime; }
    }
}

package net.ooder.sdk.memory.impl;

import net.ooder.sdk.memory.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

public class InMemoryConversationMemory implements ConversationMemory {
    
    private static final Logger log = LoggerFactory.getLogger(InMemoryConversationMemory.class);
    
    private final Map<String, List<Message>> conversations = new ConcurrentHashMap<>();
    private final Map<String, Map<String, Object>> contexts = new ConcurrentHashMap<>();
    private final Map<String, Integer> maxMessagesMap = new ConcurrentHashMap<>();
    private final int defaultMaxMessages = 100;
    
    @Override
    public String addMessage(String conversationId, Message message) {
        if (conversationId == null) {
            conversationId = "conv-" + System.currentTimeMillis();
        }
        
        if (message.getMessageId() == null) {
            message.setMessageId("msg-" + System.currentTimeMillis() + "-" + UUID.randomUUID().toString().substring(0, 8));
        }
        message.setConversationId(conversationId);
        
        conversations.computeIfAbsent(conversationId, k -> new CopyOnWriteArrayList<>()).add(message);
        
        int maxMessages = maxMessagesMap.getOrDefault(conversationId, defaultMaxMessages);
        List<Message> messages = conversations.get(conversationId);
        while (messages.size() > maxMessages) {
            messages.remove(0);
        }
        
        log.debug("Message added to conversation {}: {}", conversationId, message.getRole());
        return message.getMessageId();
    }
    
    @Override
    public List<Message> getMessages(String conversationId) {
        return new ArrayList<>(conversations.getOrDefault(conversationId, Collections.emptyList()));
    }
    
    @Override
    public List<Message> getRecentMessages(String conversationId, int limit) {
        List<Message> messages = conversations.get(conversationId);
        if (messages == null || messages.isEmpty()) {
            return Collections.emptyList();
        }
        
        int start = Math.max(0, messages.size() - limit);
        return new ArrayList<>(messages.subList(start, messages.size()));
    }
    
    @Override
    public void clearConversation(String conversationId) {
        conversations.remove(conversationId);
        contexts.remove(conversationId);
        maxMessagesMap.remove(conversationId);
        log.info("Conversation cleared: {}", conversationId);
    }
    
    @Override
    public void setContext(String conversationId, String key, Object value) {
        contexts.computeIfAbsent(conversationId, k -> new ConcurrentHashMap<>()).put(key, value);
    }
    
    @Override
    public Object getContext(String conversationId, String key) {
        Map<String, Object> ctx = contexts.get(conversationId);
        return ctx != null ? ctx.get(key) : null;
    }
    
    @Override
    public Map<String, Object> getAllContext(String conversationId) {
        return new HashMap<>(contexts.getOrDefault(conversationId, Collections.emptyMap()));
    }
    
    @Override
    public String summarize(String conversationId) {
        List<Message> messages = conversations.get(conversationId);
        if (messages == null || messages.isEmpty()) {
            return "Empty conversation";
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append("Conversation: ").append(conversationId).append("\n");
        sb.append("Messages: ").append(messages.size()).append("\n");
        
        Map<String, Long> byRole = messages.stream()
            .collect(Collectors.groupingBy(Message::getRole, Collectors.counting()));
        sb.append("By role: ").append(byRole).append("\n");
        
        if (!messages.isEmpty()) {
            sb.append("First: ").append(messages.get(0).getContent().substring(0, Math.min(50, messages.get(0).getContent().length()))).append("...\n");
            Message last = messages.get(messages.size() - 1);
            sb.append("Last: ").append(last.getContent().substring(0, Math.min(50, last.getContent().length()))).append("...");
        }
        
        return sb.toString();
    }
    
    @Override
    public List<String> getActiveConversations() {
        return new ArrayList<>(conversations.keySet());
    }
    
    @Override
    public void setMaxMessages(String conversationId, int maxMessages) {
        maxMessagesMap.put(conversationId, maxMessages);
    }
}
